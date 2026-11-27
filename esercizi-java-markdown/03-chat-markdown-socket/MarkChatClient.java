/*
 * ============================================================================
 *  ESERCIZIO 03 (PARTE A) - MarkChatClient (chat Markdown su socket TCP)
 * ============================================================================
 *  Leggi il protocollo nel file README.md.
 *  Socket, thread di ascolto e lettura tastiera sono GIA' PRONTI:
 *  tu devi completare i 4 punti marcati TODO A1..A4.
 *
 *  ESECUZIONE (dalla cartella 03-chat-markdown-socket):
 *      java -cp "../lib/*" MarkChatClient.java [host] [porta]
 *      (default: localhost 5055)
 *
 *  Per provarlo subito prima di scrivere il server, collegati al server
 *  avviato dal docente (o al tuo MarkChatServer in un altro terminale).
 * ============================================================================
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.strikethrough.Strikethrough;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Code;
import org.commonmark.node.CustomNode;
import org.commonmark.node.Emphasis;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class MarkChatClient {

    // Codici ANSI per colorare/formattare il terminale
    private static final String RESET  = "\u001B[0m";
    private static final String BOLD   = "\u001B[1m";
    private static final String ITALIC = "\u001B[3m";
    private static final String STRIKE = "\u001B[9m";
    private static final String CYAN   = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";
    private static final String GRAY   = "\u001B[90m";

    // ========================================================================
    // TODO A1: configura le estensioni (StrikethroughExtension.create()),
    //          PARSER con .extensions(ESTENSIONI) e HTML_SICURO con
    //          .extensions(ESTENSIONI).escapeHtml(true).sanitizeUrls(true)
    // ========================================================================
    private static final List<Extension> ESTENSIONI = List.of(
            // StrikethroughExtension.create()
    );
    private static final Parser PARSER = Parser.builder().build();
    private static final HtmlRenderer HTML_SICURO = HtmlRenderer.builder().build();

    public static void main(String[] args) {
        String host = args.length >= 1 ? args[0] : "localhost";
        int porta = args.length >= 2 ? Integer.parseInt(args[1]) : 5055;

        System.out.println("=== MarkChat Client (connessione a " + host + ":" + porta + ") ===");

        try (Socket socket = new Socket(host, porta);
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(
                     new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
             Scanner tastiera = new Scanner(System.in, StandardCharsets.UTF_8)) {

            System.out.print("Scegli il tuo nickname: ");
            String nick = tastiera.nextLine().trim();
            if (nick.isEmpty()) nick = "Anonimo";
            out.println("NICK " + nick);

            // Thread dedicato alla lettura dei messaggi in arrivo dal server
            Thread lettore = new Thread(() -> ascoltaServer(in));
            lettore.setDaemon(true);
            lettore.start();

            System.out.println("Scrivi messaggi in Markdown (**grassetto**, *corsivo*, `codice`, ~~barrato~~, [link](url))");
            System.out.println("Comandi: /stats  |  /export  |  /quit\n");

            while (tastiera.hasNextLine()) {
                String riga = tastiera.nextLine().trim();
                if (riga.isEmpty()) continue;

                // ============================================================
                // TODO A3: traduci i comandi utente nel protocollo del server:
                //   "/quit"   -> invia "QUIT" ed esci dal ciclo (break)
                //   "/stats"  -> invia "STATS"
                //   "/export" -> invia "EXPORT"
                //   altrimenti -> invia "MSG " + riga
                // ============================================================
                if (riga.equalsIgnoreCase("/quit")) {
                    out.println("QUIT");
                    break;
                }
                System.out.println("TODO A3: gestisci /stats, /export e MSG <testo>!");
            }

        } catch (IOException e) {
            System.out.println("Impossibile connettersi al server: " + e.getMessage());
        }
    }

    // Legge in loop le righe dal server e le mostra a schermo
    private static void ascoltaServer(BufferedReader in) {
        try {
            String riga;
            boolean inExport = false;
            StringBuilder bufferMd = new StringBuilder();

            while ((riga = in.readLine()) != null) {
                if (inExport) {
                    if (riga.equals("END_MD")) {
                        inExport = false;
                        salvaEsportazione(bufferMd.toString());
                        bufferMd.setLength(0);
                    } else {
                        bufferMd.append(riga).append("\n");
                    }
                    continue;
                }

                if (riga.equals("BEGIN_MD")) {
                    inExport = true;
                    bufferMd.setLength(0);
                } else if (riga.startsWith("INFO ")) {
                    System.out.println(GRAY + "[*] " + riga.substring(5) + RESET);
                } else if (riga.startsWith("ERR ")) {
                    System.out.println(YELLOW + "[ERRORE] " + riga.substring(4) + RESET);
                } else if (riga.startsWith("CHAT ")) {
                    String[] parti = riga.substring(5).split("\\|", 3);
                    if (parti.length == 3) {
                        String ora = parti[0];
                        String mittente = parti[1];
                        String md = parti[2];
                        System.out.println(GRAY + "[" + ora + "] " + CYAN + BOLD + mittente + RESET
                                + ": " + formattaPerTerminale(md));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(GRAY + "[*] Connessione chiusa dal server." + RESET);
        }
    }

    // ========================================================================
    // TODO A2: fai il parsing di 'markdown' con PARSER e usa un AbstractVisitor
    //          per trasformare i nodi in testo formattato con i codici ANSI:
    //            - Text           -> sb.append(t.getLiteral())
    //            - StrongEmphasis -> BOLD + figli + RESET
    //            - Emphasis       -> ITALIC + figli + RESET
    //            - Code           -> YELLOW + "[" + c.getLiteral() + "]" + RESET
    //            - Link           -> figli + CYAN + " (" + l.getDestination() + ")" + RESET
    //            - CustomNode (instanceof Strikethrough) -> STRIKE + figli + RESET
    // ========================================================================
    private static String formattaPerTerminale(String markdown) {
        // Per ora restituisce il testo grezzo: sostituisci con parsing + AbstractVisitor!
        return markdown;
    }

    // ========================================================================
    // TODO A4: salva la trascrizione Markdown ricevuta dal server in:
    //   1) "chat_esportata.md" (UTF-8)
    //   2) "chat_esportata.html" (UTF-8), convertendo il Markdown con HTML_SICURO
    //      e avvolgendolo in una semplice pagina HTML (o usando HTML_SICURO.render)
    // ========================================================================
    private static void salvaEsportazione(String trascrizioneMd) {
        System.out.println("TODO A4: salva chat_esportata.md e chat_esportata.html ("
                + trascrizioneMd.length() + " caratteri ricevuti)");
    }
}
