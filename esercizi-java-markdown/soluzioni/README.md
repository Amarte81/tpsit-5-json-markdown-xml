# 🔑 Soluzioni (per il docente)

Soluzioni complete e testate (Java 21, commonmark-java 0.24.0). Ogni file riporta in testa il comando di esecuzione.

| Esercizio | File | Come eseguirla |
|:--|:--|:--|
| 01 — Scheda gioco | [`01-scheda-gioco/SchedaGiocoApp.java`](./01-scheda-gioco/SchedaGiocoApp.java) | da `soluzioni/01-scheda-gioco`: `java -cp "../../lib/*" SchedaGiocoApp.java` |
| 02 — Portale torneo | [`02-diario-torneo/TorneoSiteApp.java`](./02-diario-torneo/TorneoSiteApp.java) | da `02-diario-torneo`: `java -cp "../lib/*" ../soluzioni/02-diario-torneo/TorneoSiteApp.java` |
| 03 — MarkChat server | [`03-chat-markdown-socket/MarkChatServer.java`](./03-chat-markdown-socket/MarkChatServer.java) | da `03-chat-markdown-socket`: `java -cp "../lib/*" ../soluzioni/03-chat-markdown-socket/MarkChatServer.java` |
| 03 — MarkChat client | [`03-chat-markdown-socket/MarkChatClient.java`](./03-chat-markdown-socket/MarkChatClient.java) | da `03-chat-markdown-socket`: `java -cp "../lib/*" ../soluzioni/03-chat-markdown-socket/MarkChatClient.java [host]` |

## Risposte alle domande di verifica (sintesi)

**Esempio 00** — 1) L'AST rispetta la grammatica (es. non sostituisce `**` dentro un blocco di codice) e permette più operazioni sullo stesso albero (HTML, testo puro, analisi con `Visitor`). 2) Per mostrare anteprime pulite nelle notifiche push o indicizzare il testo per la ricerca senza simboli Markdown. 3) Il browser eseguirebbe lo script JavaScript (attacco XSS). 4) Senza `visitChildren(heading)`, il visitor non scenderebbe nei nodi interni al titolo (es. un link o grassetto dentro il titolo).

**Esercizio 01** — 1) Perché i tag HTML (`<strong>…</strong>`, `<ul><li>…</li></ul>`) sono più verbosi dei simboli Markdown (`**…**`, `-`). 2) Perché la stringa Markdown grezza contiene simboli (`#`, `-`, URL) che falsano il conteggio delle parole. 3) Eventuali nodi annidati dentro il titolo (es. grassetto o link) verrebbero saltati dal visitor. 4) `escapeHtml(true)` neutralizza tag HTML scritti a mano (es. `<script>`, `<img onerror=...>`); `sanitizeUrls(true)` blocca gli schemi URL pericolosi dentro i link Markdown (`[clicca](javascript:...)`).

**Esercizio 02** — 1) La tabella non viene riconosciuta come `TableBlock` e finisce in un normale paragrafo `<p>` pieno di `|`. 2) Perché `YamlFrontMatterExtension` li interpreta come metadati strutturati (`YamlFrontMatterBlock`), non come contenuto visibile della pagina. 3) Impedisce alla nuova scheda aperta dal link esterno di accedere alla pagina di origine tramite `window.opener` (sicurezza *tabnabbing*). 4) L'AST garantisce sintassi valida e gestisce automaticamente l'escape dei caratteri speciali Markdown.

**Esercizio 03** — 1) Così ogni client può renderizzarlo come preferisce (ANSI nel terminale, HTML nel browser, testo puro su mobile) ed è più leggero in rete. 2) Perché leggere dalla tastiera (`Scanner.nextLine()`) è bloccante: senza un thread dedicato non si vedrebbero i messaggi degli altri finché non si preme Invio. 3) Perché più thread (un `ClientHandler` per client) li incrementano contemporaneamente: `AtomicInteger` evita *race condition*. 4) Chiunque potrebbe iniettare `<script>` in un messaggio e farlo eseguire sul browser del docente che proietta `chat_log.html`.
