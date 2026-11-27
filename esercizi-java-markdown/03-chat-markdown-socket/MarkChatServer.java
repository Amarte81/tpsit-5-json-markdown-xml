/*
 * ============================================================================
 *  ESERCIZIO 03 (PARTE B) - MarkChatServer (chat Markdown su socket TCP)
 * ============================================================================
 *  ServerSocket, gestione multi-thread e broadcast sono GIA' PRONTI:
 *  tu devi completare i 4 punti marcati TODO B1..B4 (la parte Markdown).
 *
 *  ESECUZIONE (dalla cartella 03-chat-markdown-socket):
 *      java -cp "../lib/*" MarkChatServer.java [porta]
 *      (default: 5055)
 * ============================================================================
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.task.list.items.TaskListItemsExtension;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Code;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class MarkChatServer {

    private static final int PORTA_DEFAULT = 5055;
    private static final DateTimeFormatter ORA_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    // ========================================================================
    // TODO B1: configura ESTENSIONI con:
    //   TablesExtension.create(), StrikethroughExtension.create(), TaskListItemsExtension.create()
    // poi costruisci PARSER con .extensions(ESTENSIONI) e HTML_SICURO con
    //   .extensions(ESTENSIONI).escapeHtml(true).sanitizeUrls(true)
    // ========================================================================
    private static final List<Extension> ESTENSIONI = List.of();
    private static final Parser PARSER = Parser.builder().build();
    private static final HtmlRenderer HTML_SICURO = HtmlRenderer.builder().build();

    // Stato condiviso tra tutti i thread (thread-safe!)
    private static final List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private static final List<MessaggioChat> cronologia = new CopyOnWriteArrayList<>();
    private static final AtomicInteger totaleMessaggi = new AtomicInteger();
    private static final AtomicInteger totaleLink = new AtomicInteger();
    private static final AtomicInteger totaleCodici = new AtomicInteger();

    record MessaggioChat(String ora, String nick, String markdown, String htmlSicuro) { }

    public static void main(String[] args) throws IOException {
        int porta = args.length >= 1 ? Integer.parseInt(args[0]) : PORTA_DEFAULT;
        aggiornaChatHtml();

        try (ServerSocket server = new ServerSocket(porta)) {
            System.out.println("=== MarkChat Server avviato sulla porta " + porta + " ===");
            System.out.println("Anteprima live HTML: " + Path.of("chat_log.html").toAbsolutePath());

            while (true) {
                Socket socket = server.accept();
                ClientHandler handler = new ClientHandler(socket);
                clients.add(handler);
                new Thread(handler).start();
            }
        }
    }

    // ========================================================================
    // TODO B2: visita 'doc' con un AbstractVisitor:
    //   - per ogni Link -> totaleLink.incrementAndGet()
    //   - per ogni Code -> totaleCodici.incrementAndGet()
    // ========================================================================
    private static void analizzaMessaggio(Node doc) {
        // ... scrivi qui il tuo AbstractVisitor ...
    }

    // ========================================================================
    // TODO B3: rigenera il file "chat_log.html" (in UTF-8) con tutti i messaggi
    //          in 'cronologia'. Ogni MessaggioChat ha gia' ora(), nick() e
    //          htmlSicuro(): avvolgili in una pagina HTML con refresh automatico
    //          <meta http-equiv="refresh" content="3">.
    // ========================================================================
    private static synchronized void aggiornaChatHtml() {
        // Suggerimento: costruisci un StringBuilder con i <div class="msg">...</div>
        // e salva in Path.of("chat_log.html") con Files.writeString(..., StandardCharsets.UTF_8).
    }

    // ========================================================================
    // TODO B4: costruisci la trascrizione completa della chat in formato Markdown:
    //   # Trascrizione MarkChat
    //   (vuoto)
    //   Per ogni MessaggioChat m in cronologia:
    //     - **`[ora]` nick**: markdown
    // ========================================================================
    private static String costruisciTrascrizioneMd() {
        return "# Trascrizione MarkChat\n\n(TODO B4: completa costruisciTrascrizioneMd)\n";
    }

    private static void broadcast(String linea) {
        for (ClientHandler c : clients) {
            c.invia(linea);
        }
    }

    private static String escapeTesto(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    // Gestisce un singolo client su un thread dedicato
    static class ClientHandler implements Runnable {
        private final Socket socket;
        private PrintWriter out;
        private String nick;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        void invia(String linea) {
            if (out != null) out.println(linea);
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                 PrintWriter writer = new PrintWriter(
                         new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

                this.out = writer;
                invia("INFO Connesso a MarkChat! Invia NICK <nome> per entrare.");

                String riga;
                while ((riga = in.readLine()) != null) {
                    riga = riga.trim();
                    if (riga.isEmpty()) continue;

                    if (riga.startsWith("NICK ")) {
                        String nuovoNick = riga.substring(5).trim().replace("|", "");
                        if (nuovoNick.isEmpty()) {
                            invia("ERR Nickname non valido");
                        } else {
                            this.nick = nuovoNick;
                            System.out.println("[+] " + nick + " connesso (" + clients.size() + " online)");
                            broadcast("INFO " + nick + " e' entrato in chat! (online: " + clients.size() + ")");
                        }
                    } else if (riga.equals("QUIT")) {
                        invia("INFO Alla prossima!");
                        break;
                    } else if (this.nick == null) {
                        invia("ERR Devi prima registrarti con NICK <nome>");
                    } else if (riga.startsWith("MSG ")) {
                        String md = riga.substring(4);
                        if (md.isBlank()) continue;

                        Node doc = PARSER.parse(md);
                        analizzaMessaggio(doc);
                        totaleMessaggi.incrementAndGet();

                        String ora = LocalTime.now().format(ORA_FMT);
                        String html = HTML_SICURO.render(doc);
                        cronologia.add(new MessaggioChat(ora, nick, md, html));
                        aggiornaChatHtml();

                        System.out.println("[" + ora + "] " + nick + ": " + md);
                        broadcast("CHAT " + ora + "|" + nick + "|" + md);
                    } else if (riga.equals("STATS")) {
                        invia("INFO Statistiche -> Messaggi: " + totaleMessaggi.get()
                                + " | Link: " + totaleLink.get()
                                + " | Snippet codice: " + totaleCodici.get()
                                + " | Online: " + clients.size());
                    } else if (riga.equals("EXPORT")) {
                        invia("BEGIN_MD");
                        for (String l : costruisciTrascrizioneMd().split("\n", -1)) {
                            invia(l);
                        }
                        invia("END_MD");
                    } else {
                        invia("ERR Comando sconosciuto. Usa: NICK, MSG, STATS, EXPORT, QUIT");
                    }
                }
            } catch (IOException e) {
                // disconnessione improvvisa del client
            } finally {
                clients.remove(this);
                if (nick != null) {
                    System.out.println("[-] " + nick + " disconnesso");
                    broadcast("INFO " + nick + " ha lasciato la chat.");
                }
                try { socket.close(); } catch (IOException ignored) { }
            }
        }
    }
}
