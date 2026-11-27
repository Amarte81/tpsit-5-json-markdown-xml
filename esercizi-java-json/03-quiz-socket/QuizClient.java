/*
 * ============================================================================
 *  ESERCIZIO 03 (GUIDATO) - "QuizBattle" stile Kahoot - PARTE A: CLIENT
 * ============================================================================
 *  Leggi prima il README.md: contiene la TABELLA DEI MESSAGGI del protocollo.
 *
 *  Per provare il client ti serve un server acceso:
 *   - quello del prof (chiedi l'indirizzo IP), oppure
 *   - la soluzione in locale:
 *       java -cp ../lib/gson-2.11.0.jar ../soluzioni/03-quiz-socket/QuizServer.java
 *
 *  ESECUZIONE (dalla cartella 03-quiz-socket):
 *      java -cp ../lib/gson-2.11.0.jar QuizClient.java [host] [porta]
 *
 *  Il file compila cosi' com'e': completa i TODO A1 -> A6 nell'ordine.
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
import java.util.Scanner;

public class QuizClient {

    /** true = stampa anche il JSON grezzo che viaggia sul socket. Lascialo a true! */
    static final boolean MOSTRA_JSON = true;
    static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
    static final Scanner tastiera = new Scanner(System.in);

    public static void main(String[] args) {
        String host = (args.length > 0) ? args[0] : "localhost";
        int porta = (args.length > 1) ? Integer.parseInt(args[1]) : 5050;

        System.out.print("Il tuo nickname: ");
        String nickname = tastiera.nextLine();

        // Connessione + stream di testo UTF-8 (gia' pronti, come nelle esercitazioni sui socket)
        try (Socket socket = new Socket(host, porta);
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(
                     new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            // ================================================================
            // TODO A1 - Invia il messaggio di LOGIN:
            //     {"tipo":"LOGIN","nickname":"..."}
            // Usa il tree model (non serve una classe):
            //     JsonObject login = new JsonObject();
            //     login.addProperty("___", "___");
            //     login.addProperty("___", nickname);
            //     invia(out, login);
            // ================================================================


            // Ciclo di ricezione: il server manda UN messaggio JSON per RIGA
            String riga;
            while ((riga = in.readLine()) != null) {
                if (MOSTRA_JSON) System.out.println("   [<- " + riga + "]");

                // ============================================================
                // TODO A2 - Trasforma la riga in JsonObject e leggi il campo "tipo"
                //     JsonObject msg = JsonParser.parseString(riga).___();
                //     String tipo = msg.get("___").getAsString();
                // ============================================================
                JsonObject msg = new JsonObject();   // <- sostituisci
                String tipo = "???";                 // <- sostituisci

                switch (tipo) {
                    case "BENVENUTO" -> {
                        // Esempio gia' fatto: leggo i campi direttamente dal JsonObject
                        System.out.println("\n" + msg.get("messaggio").getAsString());
                        System.out.println("Domande: " + msg.get("numeroDomande").getAsInt()
                                + "  |  Tempo per domanda: " + msg.get("tempoSecondi").getAsInt() + " s");
                    }
                    case "DOMANDA" -> {
                        // ====================================================
                        // TODO A3 - Converti msg in un oggetto MsgDomanda,
                        //           mostra la domanda e invia la RISPOSTA:
                        //   MsgDomanda d = GSON.fromJson(msg, ___.class);
                        //   int scelta = mostraDomandaEChiedi(d);
                        //   invia(out, new MsgRisposta(d.numero, scelta));
                        // ====================================================
                    }
                    case "ESITO" -> {
                        // ====================================================
                        // TODO A4 - Converti msg in MsgEsito (completa prima la
                        //           classe in fondo!) e stampa ad esempio:
                        //   >>> GIUSTA! +940 punti (1200 ms)
                        //   >>> SBAGLIATA! Era la 3        (ricorda: indice 0-based!)
                        //   >>> TEMPO SCADUTO! Era la 3
                        //   poi la spiegazione e il punteggio totale
                        // ====================================================
                    }
                    case "FINE" -> {
                        // ====================================================
                        // TODO A5 - Converti msg in MsgFine e stampa la
                        //           classifica numerata. Poi termina con return.
                        // ====================================================
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

    /** Mostra la domanda e restituisce la scelta come indice 0-based. (Gia' pronto) */
    static int mostraDomandaEChiedi(MsgDomanda d) {
        System.out.println("\n--- Domanda " + d.numero + "/" + d.totale + " [" + d.categoria + "] ---");
        System.out.println(d.testo);
        for (int i = 0; i < d.opzioni.size(); i++) {
            System.out.println("  " + (i + 1) + ") " + d.opzioni.get(i));
        }
        while (true) {
            System.out.print("La tua risposta (1-" + d.opzioni.size() + "): ");
            String input = tastiera.nextLine().trim();
            try {
                int n = Integer.parseInt(input);
                if (n >= 1 && n <= d.opzioni.size()) return n - 1;   // utente 1..4, protocollo 0..3
            } catch (NumberFormatException ignored) { }
            System.out.println("Scelta non valida.");
        }
    }

    /** Spedisce un messaggio come UNA riga di JSON. (Gia' pronto) */
    static void invia(PrintWriter out, Object messaggio) {
        String json = (messaggio instanceof JsonObject) ? messaggio.toString() : GSON.toJson(messaggio);
        if (MOSTRA_JSON) System.out.println("   [-> " + json + "]");
        out.println(json);
    }
    // ========================================================================
    //  Classi dei messaggi LATO CLIENT (static annidate per evitare conflitti
    //  con QuizServer se aperte insieme in un IDE).
    //  I nomi dei campi devono coincidere ESATTAMENTE con le chiavi JSON
    //  della tabella del protocollo nel README.
    // ========================================================================

    /** Gia' pronta: esempio di classe-messaggio ricevuta */
    static class MsgDomanda {
        int numero;
        int totale;
        String categoria;
        String testo;
        List<String> opzioni;
        int tempoSecondi;
    }

    /** Gia' pronta: esempio di classe-messaggio inviata ("tipo" con valore fisso) */
    static class MsgRisposta {
        final String tipo = "RISPOSTA";
        int numero;
        int scelta;

        MsgRisposta(int numero, int scelta) {
            this.numero = numero;
            this.scelta = scelta;
        }
    }

    // TODO A6 - Completa i campi guardando il messaggio ESITO nel README
    static class MsgEsito {
    }

    // TODO A6 - Una voce della classifica: {"nickname":"Sara","punti":6994}
    static class VoceClassifica {
    }

    // TODO A6 - Messaggio FINE: contiene punteggioTotale e una LISTA di VoceClassifica
    static class MsgFine {
    }
}
