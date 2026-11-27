/*
 * ============================================================================
 *  SOLUZIONE ESERCIZIO 03 (PARTE B) - MarkChatServer
 * ============================================================================
 *  Esecuzione (dalla cartella 03-chat-markdown-socket):
 *      java -cp "../lib/*" ../soluzioni/03-chat-markdown-socket/MarkChatServer.java [porta]
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

    // TODO B1 - Risolto
    private static final List<Extension> ESTENSIONI = List.of(
            TablesExtension.create(),
            StrikethroughExtension.create(),
            TaskListItemsExtension.create()
    );
    private static final Parser PARSER = Parser.builder()
            .extensions(ESTENSIONI)
            .build();
    private static final HtmlRenderer HTML_SICURO = HtmlRenderer.builder()
            .extensions(ESTENSIONI)
            .escapeHtml(true)
            .sanitizeUrls(true)
            .build();

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

    // TODO B2 - Risolto
    private static void analizzaMessaggio(Node doc) {
        doc.accept(new AbstractVisitor() {
            @Override
            public void visit(Link link) {
                totaleLink.incrementAndGet();
                visitChildren(link);
            }

            @Override
            public void visit(Code code) {
                totaleCodici.incrementAndGet();
                visitChildren(code);
            }
        });
    }

    // TODO B3 - Risolto
    private static synchronized void aggiornaChatHtml() {
        StringBuilder messaggiHtml = new StringBuilder();
        if (cronologia.isEmpty()) {
            messaggiHtml.append("<p class=\"vuoto\">Nessun messaggio ancora. Collegati con MarkChatClient!</p>");
        } else {
            for (MessaggioChat m : cronologia) {
                messaggiHtml.append("<div class=\"msg\">")
                        .append("<div class=\"meta\"><span class=\"nick\">")
                        .append(escapeTesto(m.nick()))
                        .append("</span> <span class=\"ora\">")
                        .append(m.ora())
                        .append("</span></div>")
                        .append("<div class=\"corpo\">")
                        .append(m.htmlSicuro())
                        .append("</div></div>\n");
            }
        }

        String pagina = """
                <!DOCTYPE html>
                <html lang="it">
                <head>
                  <meta charset="UTF-8">
                  <meta http-equiv="refresh" content="3">
                  <title>MarkChat Live</title>
                  <style>
                    body { font-family: system-ui, sans-serif; background: #1e1f22; color: #dbdee1;
                           max-width: 820px; margin: 28px auto; padding: 20px; }
                    h1 { color: #5865f2; margin-bottom: 4px; }
                    .sub { color: #949ba4; font-size: 0.9rem; margin-bottom: 20px; }
                    .msg { background: #2b2d31; border-radius: 10px; padding: 12px 16px; margin-bottom: 10px; }
                    .meta { margin-bottom: 4px; }
                    .nick { color: #00a8fc; font-weight: 700; }
                    .ora { color: #949ba4; font-size: 0.8rem; margin-left: 8px; }
                    .corpo p { margin: 4px 0; }
                    code { background: #1e1f22; padding: 2px 6px; border-radius: 4px; color: #facc15; }
                    a { color: #38bdf8; }
                    .vuoto { color: #949ba4; font-style: italic; }
                  </style>
                </head>
                <body>
                  <h1># markchat-live</h1>
                  <div class="sub">Messaggi: %d &bull; Link: %d &bull; Snippet codice: %d &bull; Aggiornamento automatico ogni 3s</div>
                  %s
                </body>
                </html>
                """.formatted(totaleMessaggi.get(), totaleLink.get(), totaleCodici.get(), messaggiHtml);

        try {
            Files.writeString(Path.of("chat_log.html"), pagina, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Errore scrittura chat_log.html: " + e.getMessage());
        }
    }

    // TODO B4 - Risolto
    private static String costruisciTrascrizioneMd() {
        StringBuilder sb = new StringBuilder("# Trascrizione MarkChat\n\n");
        if (cronologia.isEmpty()) {
            sb.append("*(Nessun messaggio registrato)*\n");
        } else {
            for (MessaggioChat m : cronologia) {
                sb.append("- **`[").append(m.ora()).append("]` ")
                        .append(m.nick()).append("**: ")
                        .append(m.markdown()).append("\n");
            }
        }
        return sb.toString();
    }

    private static void broadcast(String linea) {
        for (ClientHandler c : clients) {
            c.invia(linea);
        }
    }

    private static String escapeTesto(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

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
