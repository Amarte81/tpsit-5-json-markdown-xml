/*
 * ============================================================================
 *  SOLUZIONE ESERCIZIO 03 - "QuizBattle" (stile Kahoot) - CLIENT
 * ============================================================================
 *  Esecuzione:
 *      java -cp ../lib/gson-2.11.0.jar ../soluzioni/03-quiz-socket/QuizClient.java [host] [porta]
 *  es. per giocare sul server del prof:  ... QuizClient.java 192.168.1.10
 * ============================================================================
 */
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class QuizClient {

    /** true = stampa anche il JSON grezzo che viaggia sul socket (utile per capire!) */
    static final boolean MOSTRA_JSON = true;
    static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();  // apostrofi leggibili nel JSON (vedi README)
    static final BufferedReader tastiera = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws IOException {
        String host = (args.length > 0) ? args[0] : "localhost";
        int porta = (args.length > 1) ? Integer.parseInt(args[1]) : 5050;

        System.out.print("Il tuo nickname: ");
        String nickname = tastiera.readLine();
        if (nickname == null) return;

        try (Socket socket = new Socket(host, porta);
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(
                     new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            // 1) LOGIN: costruisco il JSON con il tree model (nessuna classe necessaria)
            JsonObject login = new JsonObject();
            login.addProperty("tipo", "LOGIN");
            login.addProperty("nickname", nickname);
            invia(out, login);

            // 2) Ciclo di ricezione: ogni riga e' un messaggio JSON
            String riga;
            while ((riga = in.readLine()) != null) {
                if (MOSTRA_JSON) System.out.println("   [<- " + riga + "]");

                JsonObject msg = JsonParser.parseString(riga).getAsJsonObject();
                String tipo = msg.get("tipo").getAsString();

                switch (tipo) {
                    case "BENVENUTO" -> {
                        System.out.println("\n" + msg.get("messaggio").getAsString());
                        System.out.println("Domande: " + msg.get("numeroDomande").getAsInt()
                                + "  |  Tempo per domanda: " + msg.get("tempoSecondi").getAsInt() + " s");
                    }
                    case "DOMANDA" -> {
                        MsgDomanda d = GSON.fromJson(msg, MsgDomanda.class);
                        int scelta = mostraDomandaEChiedi(d);
                        if (scelta >= 0) {
                            MsgRisposta r = new MsgRisposta(d.numero, scelta);
                            invia(out, r);
                        }
                    }
                    case "ESITO" -> {
                        MsgEsito e = GSON.fromJson(msg, MsgEsito.class);
                        if (e.corretta) {
                            System.out.println(">>> GIUSTA! +" + e.punti + " punti (" + e.tempoMs + " ms)");
                        } else if (!e.inTempo) {
                            System.out.println(">>> TEMPO SCADUTO! Era la " + (e.rispostaGiusta + 1));
                        } else {
                            System.out.println(">>> SBAGLIATA! Era la " + (e.rispostaGiusta + 1));
                        }
                        System.out.println("    " + e.spiegazione);
                        System.out.println("    Totale: " + e.punteggioTotale);
                    }
                    case "FINE" -> {
                        MsgFine f = GSON.fromJson(msg, MsgFine.class);
                        System.out.println("\n===== PARTITA FINITA: " + f.punteggioTotale + " punti =====");
                        System.out.println("CLASSIFICA");
                        int pos = 1;
                        for (VoceClassifica v : f.classifica) {
                            System.out.printf("  %d. %-15s %5d%n", pos++, v.nickname, v.punti);
                        }
                        return;
                    }
                    case "ERRORE" -> System.out.println("[ERRORE dal server] " + msg.get("messaggio").getAsString());
                    default -> System.out.println("[messaggio sconosciuto] " + tipo);
                }
            }
            System.out.println("Il server ha chiuso la connessione.");

        } catch (IOException e) {
            System.out.println("Impossibile connettersi a " + host + ":" + porta + " -> " + e.getMessage());
        }
    }

    /**
     * Mostra la domanda e restituisce la scelta come indice 0-based,
     * oppure -1 se scade il tempo (in quel caso si attende direttamente ESITO dal server).
     */
    static int mostraDomandaEChiedi(MsgDomanda d) throws IOException {
        System.out.println("\n--- Domanda " + d.numero + "/" + d.totale + " [" + d.categoria + "] (" + d.tempoSecondi + " s) ---");
        System.out.println(d.testo);
        for (int i = 0; i < d.opzioni.size(); i++) {
            System.out.println("  " + (i + 1) + ") " + d.opzioni.get(i));
        }
        long scadenza = System.currentTimeMillis() + d.tempoSecondi * 1000L;
        System.out.print("La tua risposta (1-" + d.opzioni.size() + "): ");
        while (true) {
            while (!tastiera.ready()) {
                if (System.currentTimeMillis() >= scadenza) {
                    System.out.println("\nTempo scaduto! Attendo l'esito dal server...");
                    return -1;
                }
                try { Thread.sleep(100); } catch (InterruptedException ignored) { }
            }
            String input = tastiera.readLine();
            if (input == null) return -1;
            try {
                int n = Integer.parseInt(input.trim());
                if (n >= 1 && n <= d.opzioni.size()) return n - 1;   // l'utente conta da 1, il protocollo da 0
            } catch (NumberFormatException ignored) { }
            System.out.print("Scelta non valida. Riprova (1-" + d.opzioni.size() + "): ");
        }
    }

    static void invia(PrintWriter out, Object messaggio) {
        // Se e' gia' un JsonObject toString() da' il JSON, altrimenti serializzo il POJO
        String json = (messaggio instanceof JsonObject) ? messaggio.toString() : GSON.toJson(messaggio);
        if (MOSTRA_JSON) System.out.println("   [-> " + json + "]");
        out.println(json);
    }
    // ========================================================================
    //  Classi dei messaggi LATO CLIENT (static annidate per evitare conflitti
    //  con QuizServer se aperte insieme in un IDE).
    //  Non sono le stesse classi del server (sono due programmi diversi!):
    //  cio' che conta e' che i NOMI DEI CAMPI coincidano con le chiavi JSON.
    //  Il "contratto" tra client e server e' il formato JSON, non il codice Java.
    // ========================================================================
    static class MsgDomanda {
        int numero;
        int totale;
        String categoria;
        String testo;
        List<String> opzioni;
        int tempoSecondi;
    }

    static class MsgRisposta {
        final String tipo = "RISPOSTA";
        int numero;
        int scelta;

        MsgRisposta(int numero, int scelta) {
            this.numero = numero;
            this.scelta = scelta;
        }
    }

    static class MsgEsito {
        int numero;
        boolean corretta;
        boolean inTempo;
        int rispostaGiusta;
        int punti;
        int punteggioTotale;
        long tempoMs;
        String spiegazione;
    }

    static class VoceClassifica {
        String nickname;
        int punti;
    }

    static class MsgFine {
        int punteggioTotale;
        List<VoceClassifica> classifica;
    }
}
