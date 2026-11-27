/*
 * ============================================================================
 *  SOLUZIONE ESERCIZIO 03 (PARTE A) - MarkChatClient
 * ============================================================================
 *  Esecuzione (dalla cartella 03-chat-markdown-socket):
 *      java -cp "../lib/*" ../soluzioni/03-chat-markdown-socket/MarkChatClient.java [host] [porta]
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

    private static final String RESET  = "\u001B[0m";
    private static final String BOLD   = "\u001B[1m";
    private static final String ITALIC = "\u001B[3m";
    private static final String STRIKE = "\u001B[9m";
    private static final String CYAN   = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";
    private static final String GRAY   = "\u001B[90m";

    // TODO A1 - Risolto
    private static final List<Extension> ESTENSIONI = List.of(
            StrikethroughExtension.create()
    );
    private static final Parser PARSER = Parser.builder()
            .extensions(ESTENSIONI)
            .build();
    private static final HtmlRenderer HTML_SICURO = HtmlRenderer.builder()
            .extensions(ESTENSIONI)
            .escapeHtml(true)
            .sanitizeUrls(true)
            .build();

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
            if (!tastiera.hasNextLine()) return;
            String nick = tastiera.nextLine().trim();
            if (nick.isEmpty()) nick = "Anonimo";
            out.println("NICK " + nick);

            Thread lettore = new Thread(() -> ascoltaServer(in));
            lettore.setDaemon(true);
            lettore.start();

            System.out.println("Scrivi messaggi in Markdown (**grassetto**, *corsivo*, `codice`, ~~barrato~~, [link](url))");
            System.out.println("Comandi: /stats  |  /export  |  /quit\n");

            while (tastiera.hasNextLine()) {
                String riga = tastiera.nextLine().trim();
                if (riga.isEmpty()) continue;

                // TODO A3 - Risolto
                if (riga.equalsIgnoreCase("/quit")) {
                    out.println("QUIT");
                    break;
                } else if (riga.equalsIgnoreCase("/stats")) {
                    out.println("STATS");
                } else if (riga.equalsIgnoreCase("/export")) {
                    out.println("EXPORT");
                } else {
                    out.println("MSG " + riga);
                }
            }

            // Piccola attesa per ricevere eventuali ultimi messaggi (es. EXPORT)
            try { lettore.join(500); } catch (InterruptedException ignored) { }

        } catch (IOException e) {
            System.out.println("Impossibile connettersi al server: " + e.getMessage());
        }
    }

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

    // TODO A2 - Risolto
    private static String formattaPerTerminale(String markdown) {
        Node doc = PARSER.parse(markdown);
        StringBuilder sb = new StringBuilder();

        doc.accept(new AbstractVisitor() {
            @Override
            public void visit(Text text) {
                sb.append(text.getLiteral());
            }

            @Override
            public void visit(StrongEmphasis strong) {
                sb.append(BOLD);
                visitChildren(strong);
                sb.append(RESET);
            }

            @Override
            public void visit(Emphasis em) {
                sb.append(ITALIC);
                visitChildren(em);
                sb.append(RESET);
            }

            @Override
            public void visit(Code code) {
                sb.append(YELLOW).append("[").append(code.getLiteral()).append("]").append(RESET);
            }

            @Override
            public void visit(Link link) {
                visitChildren(link);
                sb.append(CYAN).append(" (").append(link.getDestination()).append(")").append(RESET);
            }

            @Override
            public void visit(CustomNode customNode) {
                if (customNode instanceof Strikethrough) {
                    sb.append(STRIKE);
                    visitChildren(customNode);
                    sb.append(RESET);
                } else {
                    visitChildren(customNode);
                }
            }
        });

        return sb.toString();
    }

    // TODO A4 - Risolto
    private static void salvaEsportazione(String trascrizioneMd) {
        try {
            Path outMd = Path.of("chat_esportata.md");
            Files.writeString(outMd, trascrizioneMd, StandardCharsets.UTF_8);

            String corpoHtml = HTML_SICURO.render(PARSER.parse(trascrizioneMd));
            String paginaHtml = """
                    <!DOCTYPE html>
                    <html lang="it">
                    <head>
                      <meta charset="UTF-8">
                      <title>MarkChat - Esportazione</title>
                      <style>
                        body { font-family: system-ui, sans-serif; background: #1e1f22; color: #dbdee1;
                               max-width: 780px; margin: 36px auto; padding: 24px; line-height: 1.6; }
                        h1 { color: #5865f2; }
                        a { color: #00a8fc; }
                        code { background: #2b2d31; padding: 2px 6px; border-radius: 4px; }
                      </style>
                    </head>
                    <body>
                    %s
                    </body>
                    </html>
                    """.formatted(corpoHtml);

            Path outHtml = Path.of("chat_esportata.html");
            Files.writeString(outHtml, paginaHtml, StandardCharsets.UTF_8);
            System.out.println(GRAY + "[*] Esportazione salvata in "
                    + outMd.getFileName() + " e " + outHtml.getFileName() + RESET);
        } catch (IOException e) {
            System.out.println(YELLOW + "[ERRORE] Salvataggio esportazione fallito: " + e.getMessage() + RESET);
        }
    }
}
