/*
 * ============================================================================
 *  ESERCIZIO 02 (GUIDATO) - "Portale del Torneo Esports di Classe"
 * ============================================================================
 *  Evoluzione dell'esercizio 01: qui usi TUTTE le funzionalita' essenziali
 *  per padroneggiare il Markdown in Java con commonmark-java:
 *   - file .md in UTF-8
 *   - estensioni GFM (tabelle, barrato, checklist) + YAML Front Matter
 *   - indice automatico (TOC) e audit dei link / checklist via AbstractVisitor
 *   - AttributeProvider per personalizzare l'HTML (id, target="_blank", CSS)
 *   - costruzione programmatica dell'AST e rendering inverso con MarkdownRenderer
 *
 *  ESECUZIONE (dalla cartella 02-diario-torneo):
 *      java -cp "../lib/*" TorneoSiteApp.java
 *
 *  Il file compila gia': completa i TODO passo dopo passo ed esegui ogni volta.
 * ============================================================================
 */

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.commonmark.Extension;
import org.commonmark.ext.front.matter.YamlFrontMatterExtension;
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TableBlock;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.task.list.items.TaskListItemMarker;
import org.commonmark.ext.task.list.items.TaskListItemsExtension;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.BulletList;
import org.commonmark.node.Code;
import org.commonmark.node.CustomNode;
import org.commonmark.node.Document;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.Heading;
import org.commonmark.node.Link;
import org.commonmark.node.ListItem;
import org.commonmark.node.Node;
import org.commonmark.node.Paragraph;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.AttributeProvider;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.markdown.MarkdownRenderer;

public class TorneoSiteApp {

    public static void main(String[] args) throws IOException {

        System.out.println("Esercizio 02 - Portale del Torneo Esports di Classe\n");

        // ====================================================================
        // PASSO 1 - Configura le 4 estensioni (GFM + YAML Front Matter) e Parser
        // ====================================================================
        // TODO 1: inserisci nella lista le 4 estensioni:
        //   TablesExtension.create()
        //   StrikethroughExtension.create()
        //   TaskListItemsExtension.create()
        //   YamlFrontMatterExtension.create()
        List<Extension> estensioni = List.of(
                // ... aggiungi qui le 4 estensioni ...
        );

        // TODO 1b: passa le estensioni al Parser con .extensions(estensioni)
        Parser parser = Parser.builder()
                .build();

        // ====================================================================
        // PASSO 2 - Leggi il file torneo.md in UTF-8 e fai il parsing
        // ====================================================================
        String markdownOriginale = caricaMarkdown(Path.of("torneo.md"));
        if (markdownOriginale == null) {
            System.out.println("TODO 2: completa caricaMarkdown() per proseguire!");
            return;
        }
        Node doc = parser.parse(markdownOriginale);
        System.out.println("=== PASSO 2 - File letto ===");
        System.out.println("  torneo.md caricato (" + markdownOriginale.length() + " caratteri)");

        // ====================================================================
        // PASSO 3 - Estrai i metadati YAML Front Matter
        // ====================================================================
        System.out.println("\n=== PASSO 3 - Metadati YAML Front Matter ===");
        // TODO 3: usa YamlFrontMatterVisitor:
        //   YamlFrontMatterVisitor yamlVisitor = new YamlFrontMatterVisitor();
        //   doc.accept(yamlVisitor);
        //   Map<String, List<String>> meta = yamlVisitor.getData();
        // Stampa titolo, autore, data e tag (es. meta.getOrDefault("titolo", List.of("Senza titolo")).get(0))
        String titoloPagina = "Torneo Esports"; // <-- sostituisci con il titolo letto dal YAML

        // ====================================================================
        // PASSO 4 - Genera l'Indice automatico (TOC) dai nodi Heading
        // ====================================================================
        System.out.println("\n=== PASSO 4 - Indice automatico (TOC) ===");
        List<VoceIndice> indice = new ArrayList<>();
        // TODO 4: visita doc con un AbstractVisitor su Heading:
        //   per ogni Heading estrai testo = estraiTesto(h), slug = creaSlug(testo),
        //   aggiungi new VoceIndice(h.getLevel(), testo, slug) a indice,
        //   e stampa ogni voce indentata (es. "  ".repeat(v.livello()) + "- " + v.testo() + " (#" + v.slug() + ")").

        // ====================================================================
        // PASSO 5 - Checklist (TaskListItemMarker) & Audit Link / Codice
        // ====================================================================
        System.out.println("\n=== PASSO 5 - Checklist & Audit ===");
        // TODO 5: visita doc con un AbstractVisitor:
        //   NOTA: TaskListItemMarker e' un CustomNode! Per intercettarlo in
        //   AbstractVisitor sovrascrivi visit(CustomNode customNode) e controlla:
        //     if (customNode instanceof TaskListItemMarker marker) { ... marker.isChecked() ... }
        //   Inoltre:
        //   - raccogli tutti i Link e segnala [ATTENZIONE] se l'URL non inizia con "https://"
        //   - raccogli i linguaggi dei FencedCodeBlock

        // ====================================================================
        // PASSO 6 - HtmlRenderer con AttributeProvider e salvataggio torneo.html
        // ====================================================================
        System.out.println("\n=== PASSO 6 - Generazione torneo.html con AttributeProvider ===");
        // TODO 6: configura HtmlRenderer con:
        //   .extensions(estensioni)
        //   .escapeHtml(true)
        //   .sanitizeUrls(true)
        //   .attributeProviderFactory(context -> (node, tagName, attributes) -> {
        //       if (node instanceof Heading h) {
        //           attributes.put("id", creaSlug(estraiTesto(h)));
        //       } else if (node instanceof Link l && l.getDestination().startsWith("http")) {
        //           attributes.put("target", "_blank");
        //           attributes.put("rel", "noopener noreferrer");
        //       } else if (node instanceof TableBlock) {
        //           attributes.put("class", "tabella-torneo");
        //       }
        //   })
        // Poi genera corpoHtml = htmlRenderer.render(doc), costruisci la pagina con
        // costruisciPaginaHtml(titoloPagina, indice, corpoHtml) e salvala in "torneo.html".

        // ====================================================================
        // PASSO 7 - Costruzione AST in Java -> Markdown con MarkdownRenderer
        // ====================================================================
        System.out.println("\n=== PASSO 7 - AST Java -> Markdown (MarkdownRenderer) ===");
        // TODO 7: costruisci un nuovo Document in Java contenente:
        //   1) Heading livello 2 con Text("Aggiornamento Finale")
        //   2) Paragraph con Text("La squadra vincitrice e' ") + StrongEmphasis("NullPointer") + Text("!")
        //   3) BulletList con 2 ListItem:
        //        - "Premiazione: venerdi' ore 12:00 in Aula Magna"
        //        - "Foto ufficiali su " + Link("https://github.com/Amarte81", "Archivio Foto")
        // Poi renderizza il nuovo Document in stringa Markdown usando:
        //   MarkdownRenderer mdRenderer = MarkdownRenderer.builder().extensions(estensioni).build();
        //   String nuovaSezioneMd = mdRenderer.render(nuovaSezione);
        // Stampa nuovaSezioneMd e salva markdownOriginale + "\n" + nuovaSezioneMd
        // nel file "torneo_aggiornato.md".
    }

    // TODO 2: leggi il file in UTF-8 con Files.readString(...).
    // Se il file non esiste (IOException), stampa un messaggio chiaro e restituisci null.
    private static String caricaMarkdown(Path percorso) {
        // ... scrivi qui il tuo codice ...
        return null;
    }

    // Record di supporto per le voci dell'indice (TOC)
    record VoceIndice(int livello, String testo, String slug) { }

    // Helper gia' pronto: estrae il testo puro da un nodo (es. un Heading)
    private static String estraiTesto(Node nodo) {
        StringBuilder sb = new StringBuilder();
        nodo.accept(new AbstractVisitor() {
            @Override
            public void visit(Text text) { sb.append(text.getLiteral()); }

            @Override
            public void visit(Code code) { sb.append(code.getLiteral()); }
        });
        return sb.toString();
    }

    // Helper gia' pronto: trasforma "Classifica Provvisoria" -> "classifica-provvisoria"
    private static String creaSlug(String testo) {
        return testo.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-");
    }

    // Helper gia' pronto: impacchetta indice + HTML in una pagina statica pronta per il browser
    private static String costruisciPaginaHtml(String titolo, List<VoceIndice> indice, String corpoHtml) {
        StringBuilder tocHtml = new StringBuilder("<nav class=\"toc\"><strong>Indice</strong><ul>");
        for (VoceIndice v : indice) {
            tocHtml.append("<li style=\"margin-left:")
                    .append((v.livello() - 1) * 14)
                    .append("px\"><a href=\"#")
                    .append(v.slug())
                    .append("\">")
                    .append(v.testo())
                    .append("</a></li>");
        }
        tocHtml.append("</ul></nav>");

        return """
                <!DOCTYPE html>
                <html lang="it">
                <head>
                  <meta charset="UTF-8">
                  <title>%s</title>
                  <style>
                    body { font-family: system-ui, sans-serif; background: #0b1120; color: #e2e8f0;
                           max-width: 820px; margin: 36px auto; padding: 28px; line-height: 1.65; }
                    h1, h2 { color: #38bdf8; }
                    a { color: #7dd3fc; }
                    .toc { background: #1e293b; padding: 14px 20px; border-radius: 10px; margin-bottom: 24px; }
                    .toc ul { list-style: none; padding-left: 0; margin: 8px 0 0; }
                    .tabella-torneo { width: 100%%; border-collapse: collapse; margin: 16px 0; }
                    .tabella-torneo th, .tabella-torneo td { border: 1px solid #334155; padding: 8px 12px; }
                    .tabella-torneo th { background: #1e293b; color: #38bdf8; }
                    pre { background: #1e293b; padding: 14px; border-radius: 8px; overflow-x: auto; }
                    blockquote { border-left: 4px solid #38bdf8; margin: 14px 0; padding: 6px 16px;
                                 background: #1e293b; border-radius: 0 6px 6px 0; }
                  </style>
                </head>
                <body>
                %s
                %s
                </body>
                </html>
                """.formatted(titolo, tocHtml, corpoHtml);
    }
}
