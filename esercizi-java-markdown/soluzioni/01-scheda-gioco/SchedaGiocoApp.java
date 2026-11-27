/*
 * ============================================================================
 *  SOLUZIONE ESERCIZIO 01 - "La scheda del mio videogioco preferito"
 * ============================================================================
 *  Esecuzione (dalla cartella soluzioni/01-scheda-gioco):
 *      java -cp "../../lib/*" SchedaGiocoApp.java
 * ============================================================================
 */

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Code;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.Heading;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.text.TextContentRenderer;

public class SchedaGiocoApp {

    public static void main(String[] args) throws IOException {

        System.out.println("Esercizio 01 - La scheda del mio videogioco preferito\n");

        // PASSO 1
        String schedaMd = """
                # Hollow Knight

                **Hollow Knight** e' un *metroidvania* indie sviluppato da Team Cherry,
                ambientato nel regno sotterraneo di **Nidosacro**.

                ## Perche' giocarci
                - Atmosfera e colonna sonora **indimenticabili**
                - Boss fight impegnative ma *sempre giuste*
                - Mappa enorme piena di segreti da esplorare

                > "Nessun costo e' troppo alto." — *Il Re Pallido*

                ## Comandi rapidi (PC)
                ```text
                Frecce : Movimento
                Z      : Salto
                X      : Colpo di Aculeo
                A      : Concentrazione / Cura
                ```

                Scopri di piu' sul [sito ufficiale](https://www.hollowknight.com)
                oppure guarda la [pagina Steam](https://store.steampowered.com/app/367520).
                """;

        // PASSO 2 - Parsing in AST e conteggio blocchi principali
        Parser parser = Parser.builder().build();
        Node doc = parser.parse(schedaMd);

        int blocchi = 0;
        for (Node n = doc.getFirstChild(); n != null; n = n.getNext()) {
            blocchi++;
        }
        System.out.println("PASSO 2 - AST creato: " + doc.getClass().getSimpleName()
                + " (blocchi principali: " + blocchi + ")");

        // PASSO 3 - Rendering HTML e confronto lunghezze
        HtmlRenderer htmlRenderer = HtmlRenderer.builder().build();
        String html = htmlRenderer.render(doc);
        System.out.println("\nPASSO 3 - Confronto lunghezze:");
        System.out.println("  Markdown = " + schedaMd.length()
                + " caratteri  |  HTML = " + html.length() + " caratteri");

        // PASSO 4 - Testo puro, anteprima notifica e conteggio parole
        TextContentRenderer textRenderer = TextContentRenderer.builder().build();
        String testoPuro = textRenderer.render(doc).replaceAll("\\s+", " ").trim();
        String anteprima = testoPuro.length() > 100
                ? testoPuro.substring(0, 100) + "..."
                : testoPuro;
        int numeroParole = testoPuro.isEmpty() ? 0 : testoPuro.split(" ").length;
        System.out.println("\nPASSO 4 - Anteprima notifica (max 100 caratteri):");
        System.out.println("  " + anteprima);
        System.out.println("  Parole stimate: " + numeroParole);

        // PASSO 5 - Analisi AST con AbstractVisitor
        List<String> titoli = new ArrayList<>();
        List<String> link = new ArrayList<>();
        int[] grassetti = { 0 };
        int[] blocchiCodice = { 0 };

        doc.accept(new AbstractVisitor() {
            @Override
            public void visit(Heading heading) {
                titoli.add("H" + heading.getLevel() + ": " + estraiTesto(heading));
                visitChildren(heading);
            }

            @Override
            public void visit(Link l) {
                link.add(l.getDestination());
                visitChildren(l);
            }

            @Override
            public void visit(StrongEmphasis strong) {
                grassetti[0]++;
                visitChildren(strong);
            }

            @Override
            public void visit(FencedCodeBlock block) {
                blocchiCodice[0]++;
                visitChildren(block);
            }
        });

        System.out.println("\nPASSO 5 - Analisi AST:");
        System.out.println("  Titoli (" + titoli.size() + "): " + titoli);
        System.out.println("  Link   (" + link.size() + "): " + link);
        System.out.println("  Grassetti : " + grassetti[0]
                + "  |  Blocchi di codice: " + blocchiCodice[0]);

        // PASSO 6 - Test sicurezza XSS
        String recensioneSospetta = """
                Gioco bellissimo! <img src=x onerror="alert('hack')">
                Scarica [trucchi infiniti](javascript:alert('rubato'))
                """;
        HtmlRenderer htmlSicuro = HtmlRenderer.builder()
                .escapeHtml(true)
                .sanitizeUrls(true)
                .build();
        String recensioneSicura = htmlSicuro.render(parser.parse(recensioneSospetta));
        System.out.println("\nPASSO 6 - Test sicurezza XSS:");
        System.out.println("  [SICURO] " + recensioneSicura.trim());

        // PASSO 7 - Esportazione in file HTML
        String pagina = avvolgiInPaginaHtml("La mia scheda gioco", htmlSicuro.render(doc));
        Path fileOut = Path.of("scheda_gioco.html");
        Files.writeString(fileOut, pagina, StandardCharsets.UTF_8);
        System.out.println("\nPASSO 7 - File salvato: " + fileOut.toAbsolutePath());
    }

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

    private static String avvolgiInPaginaHtml(String titolo, String corpoHtml) {
        return """
                <!DOCTYPE html>
                <html lang="it">
                <head>
                  <meta charset="UTF-8">
                  <title>%s</title>
                  <style>
                    body { font-family: system-ui, sans-serif; background: #0f172a; color: #e2e8f0;
                           max-width: 760px; margin: 40px auto; padding: 28px; line-height: 1.65; }
                    h1, h2 { color: #38bdf8; }
                    a { color: #7dd3fc; }
                    pre { background: #1e293b; padding: 14px; border-radius: 8px; overflow-x: auto; }
                    blockquote { border-left: 4px solid #38bdf8; margin: 14px 0; padding: 6px 16px;
                                 background: #1e293b; color: #cbd5e1; border-radius: 0 6px 6px 0; }
                  </style>
                </head>
                <body>
                %s
                </body>
                </html>
                """.formatted(titolo, corpoHtml);
    }
}
