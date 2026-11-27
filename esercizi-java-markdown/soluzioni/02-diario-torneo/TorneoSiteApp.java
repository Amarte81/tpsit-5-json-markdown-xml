/*
 * ============================================================================
 *  SOLUZIONE ESERCIZIO 02 - "Portale del Torneo Esports di Classe"
 * ============================================================================
 *  Esecuzione (dalla cartella 02-diario-torneo, dove si trova torneo.md):
 *      java -cp "../lib/*" ../soluzioni/02-diario-torneo/TorneoSiteApp.java
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
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.markdown.MarkdownRenderer;

public class TorneoSiteApp {

    public static void main(String[] args) throws IOException {

        System.out.println("Esercizio 02 - Portale del Torneo Esports di Classe\n");

        // PASSO 1 - Estensioni GFM + YAML Front Matter
        List<Extension> estensioni = List.of(
                TablesExtension.create(),
                StrikethroughExtension.create(),
                TaskListItemsExtension.create(),
                YamlFrontMatterExtension.create()
        );

        Parser parser = Parser.builder()
                .extensions(estensioni)
                .build();

        // PASSO 2 - Lettura file torneo.md
        String markdownOriginale = caricaMarkdown(Path.of("torneo.md"));
        if (markdownOriginale == null) {
            return;
        }
        Node doc = parser.parse(markdownOriginale);
        System.out.println("=== PASSO 2 - File letto ===");
        System.out.println("  torneo.md caricato (" + markdownOriginale.length() + " caratteri)");

        // PASSO 3 - Estrazione metadati YAML Front Matter
        System.out.println("\n=== PASSO 3 - Metadati YAML Front Matter ===");
        YamlFrontMatterVisitor yamlVisitor = new YamlFrontMatterVisitor();
        doc.accept(yamlVisitor);
        Map<String, List<String>> meta = yamlVisitor.getData();

        String titoloPagina = meta.getOrDefault("titolo", List.of("Torneo Esports")).get(0);
        String autore = meta.getOrDefault("autore", List.of("Sconosciuto")).get(0);
        String data = meta.getOrDefault("data", List.of("-")).get(0);
        List<String> tag = meta.getOrDefault("tag", List.of());

        System.out.println("  Titolo : " + titoloPagina);
        System.out.println("  Autore : " + autore);
        System.out.println("  Data   : " + data);
        System.out.println("  Tag    : " + tag);

        // PASSO 4 - Generazione automatica Indice (TOC)
        System.out.println("\n=== PASSO 4 - Indice automatico (TOC) ===");
        List<VoceIndice> indice = new ArrayList<>();
        doc.accept(new AbstractVisitor() {
            @Override
            public void visit(Heading h) {
                String testo = estraiTesto(h);
                String slug = creaSlug(testo);
                indice.add(new VoceIndice(h.getLevel(), testo, slug));
                visitChildren(h);
            }
        });
        for (VoceIndice v : indice) {
            System.out.println("  ".repeat(v.livello()) + "- " + v.testo() + " (#" + v.slug() + ")");
        }

        // PASSO 5 - Checklist & Audit Link / Codice
        System.out.println("\n=== PASSO 5 - Checklist & Audit ===");
        int[] completate = { 0 };
        int[] totaliTask = { 0 };
        List<String> links = new ArrayList<>();
        List<String> linguaggi = new ArrayList<>();

        doc.accept(new AbstractVisitor() {
            @Override
            public void visit(CustomNode customNode) {
                if (customNode instanceof TaskListItemMarker marker) {
                    totaliTask[0]++;
                    if (marker.isChecked()) {
                        completate[0]++;
                    }
                }
                visitChildren(customNode);
            }

            @Override
            public void visit(Link l) {
                links.add(l.getDestination());
                visitChildren(l);
            }

            @Override
            public void visit(FencedCodeBlock block) {
                linguaggi.add(block.getInfo().isEmpty() ? "(nessuno)" : block.getInfo());
                visitChildren(block);
            }
        });

        int perc = totaliTask[0] == 0 ? 0 : (completate[0] * 100 / totaliTask[0]);
        System.out.println("  Attivita' completate: " + completate[0] + "/" + totaliTask[0] + " (" + perc + "%)");
        System.out.println("  Link trovati: " + links.size());
        for (String url : links) {
            if (url.startsWith("https://")) {
                System.out.println("    [OK]         " + url);
            } else {
                System.out.println("    [ATTENZIONE] " + url + "  (non HTTPS!)");
            }
        }
        System.out.println("  Blocchi codice: " + linguaggi);

        // PASSO 6 - HtmlRenderer con AttributeProvider + salvataggio torneo.html
        System.out.println("\n=== PASSO 6 - Generazione torneo.html con AttributeProvider ===");
        HtmlRenderer htmlRenderer = HtmlRenderer.builder()
                .extensions(estensioni)
                .escapeHtml(true)
                .sanitizeUrls(true)
                .attributeProviderFactory(context -> (node, tagName, attributes) -> {
                    if (node instanceof Heading h) {
                        attributes.put("id", creaSlug(estraiTesto(h)));
                    } else if (node instanceof Link l && l.getDestination().startsWith("http")) {
                        attributes.put("target", "_blank");
                        attributes.put("rel", "noopener noreferrer");
                    } else if (node instanceof TableBlock) {
                        attributes.put("class", "tabella-torneo");
                    }
                })
                .build();

        String corpoHtml = htmlRenderer.render(doc);
        String paginaHtml = costruisciPaginaHtml(titoloPagina, indice, corpoHtml);
        Path outHtml = Path.of("torneo.html");
        Files.writeString(outHtml, paginaHtml, StandardCharsets.UTF_8);
        System.out.println("  Pagina HTML salvata in: " + outHtml.toAbsolutePath());

        // PASSO 7 - Costruzione AST in Java -> Markdown con MarkdownRenderer
        System.out.println("\n=== PASSO 7 - AST Java -> Markdown (MarkdownRenderer) ===");
        Document nuovaSezione = new Document();

        Heading h2 = new Heading();
        h2.setLevel(2);
        h2.appendChild(new Text("Aggiornamento Finale"));
        nuovaSezione.appendChild(h2);

        Paragraph p = new Paragraph();
        p.appendChild(new Text("La squadra vincitrice e' "));
        StrongEmphasis strong = new StrongEmphasis("**");
        strong.appendChild(new Text("NullPointer"));
        p.appendChild(strong);
        p.appendChild(new Text("!"));
        nuovaSezione.appendChild(p);

        BulletList lista = new BulletList();
        lista.setMarker("-");

        ListItem item1 = new ListItem();
        Paragraph p1 = new Paragraph();
        p1.appendChild(new Text("Premiazione: venerdi' ore 12:00 in Aula Magna"));
        item1.appendChild(p1);
        lista.appendChild(item1);

        ListItem item2 = new ListItem();
        Paragraph p2 = new Paragraph();
        p2.appendChild(new Text("Foto ufficiali su "));
        Link linkFoto = new Link("https://github.com/Amarte81", null);
        linkFoto.appendChild(new Text("Archivio Foto"));
        p2.appendChild(linkFoto);
        item2.appendChild(p2);
        lista.appendChild(item2);

        nuovaSezione.appendChild(lista);

        MarkdownRenderer mdRenderer = MarkdownRenderer.builder()
                .extensions(estensioni)
                .build();
        String nuovaSezioneMd = mdRenderer.render(nuovaSezione);
        System.out.println(nuovaSezioneMd);

        Path outMd = Path.of("torneo_aggiornato.md");
        Files.writeString(outMd, markdownOriginale + "\n" + nuovaSezioneMd, StandardCharsets.UTF_8);
        System.out.println("  Markdown aggiornato salvato in: " + outMd.toAbsolutePath());
    }

    private static String caricaMarkdown(Path percorso) {
        try {
            return Files.readString(percorso, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Errore nella lettura di " + percorso + ": " + e.getMessage());
            return null;
        }
    }

    record VoceIndice(int livello, String testo, String slug) { }

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

    private static String creaSlug(String testo) {
        return testo.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-");
    }

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
