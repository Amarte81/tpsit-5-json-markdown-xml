/*
 * ============================================================================
 *  SOLUZIONE ESERCIZIO 03 - "QuizBattle" (stile Kahoot) - SERVER
 * ============================================================================
 *  Esecuzione (dalla cartella 03-quiz-socket, dove c'e' domande.json):
 *      java -cp ../lib/gson-2.11.0.jar ../soluzioni/03-quiz-socket/QuizServer.java [porta]
 *
 *  PROTOCOLLO: TCP, un messaggio JSON per riga (terminato da \n), UTF-8.
 *  Ogni messaggio ha il campo "tipo". Vedi README.md per la tabella completa.
 * ============================================================================
 */
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class QuizServer {

    static final int PORTA_DEFAULT = 5050;
    static final int TEMPO_SECONDI = 20;      // tempo per rispondere a ogni domanda
    static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();  // apostrofi leggibili nel JSON (vedi README)

    static List<Domanda> domande;
    /** nickname -> punteggio. Condivisa tra i thread: serve una mappa thread-safe. */
    static final Map<String, Integer> classifica = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        int porta = (args.length > 0) ? Integer.parseInt(args[0]) : PORTA_DEFAULT;

        domande = caricaDomande("domande.json");
        System.out.println("Caricate " + domande.size() + " domande da domande.json");

        try (ServerSocket server = new ServerSocket(porta)) {
            System.out.println("QuizBattle server in ascolto sulla porta " + porta + " ...");
            while (true) {
                Socket socket = server.accept();                 // attende un giocatore
                System.out.println("Connesso: " + socket.getRemoteSocketAddress());
                new Thread(() -> gestisciGiocatore(socket)).start(); // un thread per giocatore
            }
        }
    }

    /** Legge il file JSON (un array) e lo trasforma in List<Domanda> con TypeToken. */
    static List<Domanda> caricaDomande(String file) throws IOException {
        try (Reader r = Files.newBufferedReader(Path.of(file), StandardCharsets.UTF_8)) {
            return GSON.fromJson(r, new TypeToken<List<Domanda>>() {}.getType());
        }
    }

    /** Dialogo completo con UN giocatore (eseguito in un thread dedicato). */
    static void gestisciGiocatore(Socket socket) {
        String nickname = "?";
        try (socket;
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(
                     new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            // 1) Il primo messaggio deve essere LOGIN
            JsonObject login = leggiMessaggio(in, out);
            if (login == null) return;
            if (!"LOGIN".equals(tipoDi(login)) || !login.has("nickname")) {
                invia(out, new MsgErrore("Il primo messaggio deve essere {\"tipo\":\"LOGIN\",\"nickname\":\"...\"}"));
                return;
            }
            nickname = registraNickname(login.get("nickname").getAsString());
            invia(out, new MsgBenvenuto(nickname, domande.size(), TEMPO_SECONDI));

            // 2) Ciclo delle domande
            int totale = 0;
            for (int i = 0; i < domande.size(); i++) {
                Domanda d = domande.get(i);
                // ATTENZIONE: al client si invia MsgDomanda, che NON contiene la risposta giusta!
                invia(out, new MsgDomanda(i + 1, domande.size(), d.categoria, d.testo, d.opzioni, TEMPO_SECONDI));
                long inizio = System.currentTimeMillis();

                MsgRisposta r = null;
                while (r == null) {
                    JsonObject msg = leggiMessaggio(in, out);
                    if (msg == null) return;                       // client disconnesso
                    if (!"RISPOSTA".equals(tipoDi(msg))) {
                        invia(out, new MsgErrore("Attendevo un messaggio RISPOSTA"));
                        continue;
                    }
                    r = GSON.fromJson(msg, MsgRisposta.class);     // JsonObject -> POJO
                }
                // Il tempo lo misura il SERVER: il client potrebbe barare!
                long tempoMs = System.currentTimeMillis() - inizio;
                boolean inTempo = tempoMs <= TEMPO_SECONDI * 1000L;
                boolean giusta = inTempo && r.scelta == d.corretta;
                int punti = giusta ? calcolaPunti(tempoMs) : 0;
                totale += punti;
                classifica.put(nickname, totale);

                invia(out, new MsgEsito(i + 1, giusta, inTempo, d.corretta, punti, totale, tempoMs, d.spiegazione));
            }

            // 3) Fine partita con classifica
            invia(out, new MsgFine(totale, classificaOrdinata()));
            System.out.println(nickname + " ha finito con " + totale + " punti");

        } catch (IOException e) {
            System.out.println("Connessione con " + nickname + " chiusa: " + e.getMessage());
        }
    }

    /**
     * Legge UNA riga dal socket e la interpreta come oggetto JSON.
     * Se la riga non e' JSON valido risponde con ERRORE e legge la successiva.
     * Restituisce null se il client ha chiuso la connessione.
     */
    static JsonObject leggiMessaggio(BufferedReader in, PrintWriter out) throws IOException {
        String riga;
        while ((riga = in.readLine()) != null) {
            if (riga.isBlank()) continue;
            System.out.println("  <- " + riga);
            try {
                JsonObject obj = JsonParser.parseString(riga).getAsJsonObject();
                if (!obj.has("tipo")) {
                    invia(out, new MsgErrore("Manca il campo \"tipo\""));
                    continue;
                }
                return obj;
            } catch (JsonParseException | IllegalStateException e) {
                // JsonParseException: testo non JSON; IllegalStateException: JSON ma non oggetto
                invia(out, new MsgErrore("JSON non valido: " + riga));
            }
        }
        return null;
    }

    /** Serializza un messaggio e lo spedisce come UNA riga. */
    static void invia(PrintWriter out, Object messaggio) {
        String json = GSON.toJson(messaggio);   // Gson compatto: niente \n interni
        System.out.println("  -> " + json);
        out.println(json);
    }

    static String tipoDi(JsonObject msg) {
        return msg.get("tipo").getAsString();
    }

    /** Evita nickname duplicati aggiungendo un numero. */
    static String registraNickname(String richiesto) {
        String base = richiesto.isBlank() ? "Anonimo" : richiesto.trim();
        String nick = base;
        int n = 2;
        while (classifica.putIfAbsent(nick, 0) != null) {
            nick = base + n++;
        }
        return nick;
    }

    /** Stile Kahoot: risposta immediata = 1000 punti, allo scadere = 500. */
    static int calcolaPunti(long tempoMs) {
        double frazione = Math.min(1.0, tempoMs / (TEMPO_SECONDI * 1000.0));
        return (int) Math.round(1000 * (1 - frazione / 2));
    }

    static List<VoceClassifica> classificaOrdinata() {
        List<VoceClassifica> lista = new ArrayList<>();
        classifica.forEach((nick, punti) -> lista.add(new VoceClassifica(nick, punti)));
        lista.sort((a, b) -> Integer.compare(b.punti, a.punti));
        return lista;
    }
}

// ============================================================================
//  Modello interno (letto da domande.json): contiene la risposta giusta,
//  quindi NON deve mai essere inviato cosi' com'e' al client.
// ============================================================================
class Domanda {
    String categoria;
    String testo;
    List<String> opzioni;
    int corretta;          // indice 0-based in opzioni
    String spiegazione;
}

// ============================================================================
//  Messaggi del protocollo. Il campo "tipo" e' final con valore fisso:
//  Gson lo serializza normalmente e il client lo usa per capire il messaggio.
// ============================================================================
class MsgBenvenuto {
    final String tipo = "BENVENUTO";
    String nickname;
    int numeroDomande;
    int tempoSecondi;
    String messaggio;

    MsgBenvenuto(String nickname, int numeroDomande, int tempoSecondi) {
        this.nickname = nickname;
        this.numeroDomande = numeroDomande;
        this.tempoSecondi = tempoSecondi;
        this.messaggio = "Ciao " + nickname + "! Rispondi in fretta: piu' sei veloce, piu' punti fai.";
    }
}

class MsgDomanda {
    final String tipo = "DOMANDA";
    int numero;
    int totale;
    String categoria;
    String testo;
    List<String> opzioni;
    int tempoSecondi;

    MsgDomanda(int numero, int totale, String categoria, String testo, List<String> opzioni, int tempoSecondi) {
        this.numero = numero;
        this.totale = totale;
        this.categoria = categoria;
        this.testo = testo;
        this.opzioni = opzioni;
        this.tempoSecondi = tempoSecondi;
    }
}

/** Arriva dal client: {"tipo":"RISPOSTA","numero":1,"scelta":2} */
class MsgRisposta {
    String tipo;
    int numero;
    int scelta;            // indice 0-based
}

class MsgEsito {
    final String tipo = "ESITO";
    int numero;
    boolean corretta;
    boolean inTempo;
    int rispostaGiusta;
    int punti;
    int punteggioTotale;
    long tempoMs;
    String spiegazione;

    MsgEsito(int numero, boolean corretta, boolean inTempo, int rispostaGiusta,
             int punti, int punteggioTotale, long tempoMs, String spiegazione) {
        this.numero = numero;
        this.corretta = corretta;
        this.inTempo = inTempo;
        this.rispostaGiusta = rispostaGiusta;
        this.punti = punti;
        this.punteggioTotale = punteggioTotale;
        this.tempoMs = tempoMs;
        this.spiegazione = spiegazione;
    }
}

class VoceClassifica {
    String nickname;
    int punti;

    VoceClassifica(String nickname, int punti) {
        this.nickname = nickname;
        this.punti = punti;
    }
}

class MsgFine {
    final String tipo = "FINE";
    int punteggioTotale;
    List<VoceClassifica> classifica;

    MsgFine(int punteggioTotale, List<VoceClassifica> classifica) {
        this.punteggioTotale = punteggioTotale;
        this.classifica = classifica;
    }
}

class MsgErrore {
    final String tipo = "ERRORE";
    String messaggio;

    MsgErrore(String messaggio) {
        this.messaggio = messaggio;
    }
}
