/*
 * ============================================================================
 *  ESEMPIO COMMENTATO 00 - "Annuncio & Patch Notes" (stile Discord)
 * ============================================================================
 *
 *  OBIETTIVO
 *  Vedere in UN SOLO programma il ciclo di vita completo di un testo Markdown
 *  in Java:
 *
 *      Stringa Markdown --( Parser.parse )--> AST (albero di Node)
 *                                                |-- HtmlRenderer        --> HTML
 *                                                |-- TextContentRenderer --> testo puro
 *                                                \-- AbstractVisitor     --> analisi dati
 *
 *  Quando scrivi un messaggio su Discord o leggi un README su GitHub, il testo
 *  e' in formato Markdown. L'app lo analizza ("parsing") in un albero di nodi
 *  (AST = Abstract Syntax Tree) e poi lo trasforma ("rendering") in HTML per
 *  mostrarlo sullo schermo, oppure in testo semplice per le notifiche.
 *
 *  LIBRERIA USATA
 *  commonmark-java 0.24.0: file ../lib/commonmark-0.24.0.jar
 *  Java NON ha un parser Markdown nella libreria standard: serve una libreria.
 *
 *  COME ESEGUIRLO (Java 17 o superiore), dalla cartella 00-esempio-discord:
 *
 *      java -cp "../lib/*" EsempioDiscord.java
 *
 *  (Java 11+ sa eseguire direttamente un file .java senza javac.)
 * ============================================================================
 */

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

// Le classi essenziali di commonmark-java
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Code;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.Heading;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.text.TextContentRenderer;

public class EsempioDiscord {

    public static void main(String[] args) throws IOException {

        // --------------------------------------------------------------------
        // PASSO 1 - Una stringa Markdown (stile annuncio su Discord)
        // --------------------------------------------------------------------
        // Uso un "text block" (Java 15+): le triple virgolette """ permettono
        // di scrivere testo su piu' righe senza \n.
        String markdown = """
                # Patch 2.4 - Stagione Neon

                Benvenuti nel server **5A Gaming**! Ecco le novita' di *questa settimana*:

                ## Novita' principali
                - Nuova mappa **Cyber Arena** disponibile in classificata
                - Risolto il bug del comando `/spawn` che bloccava i giocatori
                - Buff alla velocita' di ricarica del `15%`

                > "La vittoria e' di chi non quitta mai." — *Capitano del team*

                ### Come collegarsi
                Usa il comando qui sotto in console:

                ```bash
                connect arena.scuola.local:25565
                ```

                Leggi il [regolamento completo](https://github.com/Amarte81) prima di entrare!
                """;

        System.out.println("1) Testo Markdown originale (lunghezza: " + markdown.length() + " caratteri):");
        System.out.println("------------------------------------------------------------");
        System.out.println(markdown);

        // --------------------------------------------------------------------
        // PASSO 2 - PARSING: stringa Markdown -> AST (albero di nodi)
        // --------------------------------------------------------------------
        // Parser si crea con il pattern Builder: Parser.builder().build()
        // Se ne crea UNO e lo si riusa quante volte si vuole (e' thread-safe).
        Parser parser = Parser.builder().build();

        // parse() trasforma la stringa in un albero di oggetti Node.
        // La radice dell'albero e' un oggetto di tipo Document (sottoclasse di Node).
        Node documento = parser.parse(markdown);

        System.out.println("2) AST creato! Nodo radice: " + documento.getClass().getSimpleName());
        System.out.println("   Primo figlio dell'albero: " + documento.getFirstChild().getClass().getSimpleName());

        // --------------------------------------------------------------------
        // PASSO 3 - RENDERING HTML: AST -> stringa HTML
        // --------------------------------------------------------------------
        // HtmlRenderer trasforma ogni nodo dell'albero nel corrispondente tag HTML:
        //   Heading(1)     -> <h1>...</h1>
        //   StrongEmphasis -> <strong>...</strong>
        //   Link           -> <a href="...">...</a>
        HtmlRenderer htmlRenderer = HtmlRenderer.builder().build();
        String html = htmlRenderer.render(documento);

        System.out.println("\n3) HTML generato da CommonMark:");
        System.out.println("------------------------------------------------------------");
        System.out.println(html);

        // --------------------------------------------------------------------
        // PASSO 4 - RENDERING TESTO PURO: AST -> anteprima senza formattazione
        // --------------------------------------------------------------------
        // Utile per le notifiche push sul telefono o per i motori di ricerca:
        // elimina #, **, *, `, ecc. lasciando solo il contenuto testuale.
        TextContentRenderer textRenderer = TextContentRenderer.builder().build();
        String testoPuro = textRenderer.render(documento);

        System.out.println("4) Testo puro (es. per una notifica sul telefono):");
        System.out.println("------------------------------------------------------------");
        System.out.println(testoPuro);

        // --------------------------------------------------------------------
        // PASSO 5 - ISPEZIONE DELL'AST con AbstractVisitor
        // --------------------------------------------------------------------
        // Grazie all'AST possiamo "interrogare" il documento in Java senza
        // usare regex fragili! Basta estendere AbstractVisitor e sovrascrivere
        // i metodi visit(...) dei nodi che ci interessano.
        List<String> titoli = new ArrayList<>();
        List<String> link = new ArrayList<>();
        List<String> linguaggiCodice = new ArrayList<>();
        int[] numeroCodiciInline = { 0 };

        documento.accept(new AbstractVisitor() {
            @Override
            public void visit(Heading heading) {
                // Estraiamo il testo dentro il titolo usando un piccolo helper
                String testoTitolo = estraiTesto(heading);
                titoli.add("H" + heading.getLevel() + ": " + testoTitolo);
                // IMPORTANTE: chiamare visitChildren() per continuare la visita
                // dentro i figli del nodo corrente!
                visitChildren(heading);
            }

            @Override
            public void visit(Link l) {
                link.add(l.getDestination());
                visitChildren(l);
            }

            @Override
            public void visit(Code code) {
                numeroCodiciInline[0]++;
                visitChildren(code);
            }

            @Override
            public void visit(FencedCodeBlock block) {
                linguaggiCodice.add(block.getInfo().isEmpty() ? "(nessuno)" : block.getInfo());
                visitChildren(block);
            }
        });

        System.out.println("5) Analisi del documento tramite AbstractVisitor:");
        System.out.println("   Titoli trovati     : " + titoli);
        System.out.println("   Link trovati       : " + link);
        System.out.println("   Blocchi di codice  : " + linguaggiCodice);
        System.out.println("   Snippet inline `…` : " + numeroCodiciInline[0]);

        // --------------------------------------------------------------------
        // PASSO 6 - SICUREZZA: HTML grezzo e link pericolosi (XSS)
        // --------------------------------------------------------------------
        // Per standard, Markdown permette di scrivere HTML puro al suo interno.
        // Se il testo arriva da un utente sconosciuto (chat, commenti, forum),
        // qualcuno potrebbe iniettare JavaScript malevolo (attacco XSS)!
        String markdownSospetto = """
                Ciao! <script>alert('Account rubato!');</script>
                Clicca qui per [1000 V-Bucks gratis](javascript:alert('XSS'))
                """;

        Node docSospetto = parser.parse(markdownSospetto);

        // Renderer SICURO: trasforma <script> in &lt;script&gt; e blocca javascript:
        HtmlRenderer htmlSicuro = HtmlRenderer.builder()
                .escapeHtml(true)      // neutralizza i tag HTML scritti dall'utente
                .sanitizeUrls(true)    // blocca URL pericolosi come javascript:
                .build();

        System.out.println("\n6) Sicurezza XSS — confronto renderer normale vs sicuro:");
        System.out.println("   [NON SICURO] " + htmlRenderer.render(docSospetto).trim());
        System.out.println("   [SICURO]     " + htmlSicuro.render(docSospetto).trim());

        // --------------------------------------------------------------------
        // PASSO 7 - Esportazione in un file HTML apribile nel browser
        // --------------------------------------------------------------------
        String paginaCompleta = """
                <!DOCTYPE html>
                <html lang="it">
                <head>
                  <meta charset="UTF-8">
                  <title>Anteprima Discord — Patch 2.4</title>
                  <style>
                    body { font-family: system-ui, sans-serif; background: #1e1f22; color: #dbdee1;
                           max-width: 720px; margin: 40px auto; padding: 24px; line-height: 1.6; }
                    h1, h2, h3 { color: #ffffff; }
                    a { color: #00a8fc; }
                    code { background: #2b2d31; padding: 2px 6px; border-radius: 4px; }
                    pre { background: #2b2d31; padding: 14px; border-radius: 8px; overflow-x: auto; }
                    blockquote { border-left: 4px solid #5865f2; margin: 12px 0; padding: 4px 16px;
                                 color: #b5bac1; background: #2b2d31; border-radius: 0 6px 6px 0; }
                  </style>
                </head>
                <body>
                %s
                </body>
                </html>
                """.formatted(htmlSicuro.render(documento));

        Path output = Path.of("anteprima_discord.html");
        Files.writeString(output, paginaCompleta, StandardCharsets.UTF_8);
        System.out.println("\n7) Pagina salvata in: " + output.toAbsolutePath());
        System.out.println("   Aprila nel browser per vedere il risultato!");

        // --------------------------------------------------------------------
        // RIASSUNTO
        // --------------------------------------------------------------------
        //  Parser.builder().build()                 crea il parser Markdown
        //  Node doc = parser.parse(stringaMd)       Markdown -> AST (albero)
        //  HtmlRenderer.builder()...build()         crea il renderer HTML
        //  TextContentRenderer.builder().build()    crea il renderer testo puro
        //  doc.accept(new AbstractVisitor() {...})  visita i nodi dell'AST
    }

    // Helper: raccoglie tutto il testo contenuto dentro un nodo (es. un Heading)
    private static String estraiTesto(Node nodo) {
        StringBuilder sb = new StringBuilder();
        nodo.accept(new AbstractVisitor() {
            @Override
            public void visit(Text text) {
                sb.append(text.getLiteral());
            }

            @Override
            public void visit(Code code) {
                sb.append(code.getLiteral());
            }
        });
        return sb.toString();
    }
}
