# ✏️ 01 — Esercizio guidato: "La scheda del mio videogioco preferito"

> **Variante dell'esempio 00**: stessa logica dell'annuncio Discord, ma ora il codice lo scrivi tu per generare la scheda del tuo videogioco preferito (stile README GitHub / pagina Steam).

## 🎯 Obiettivi
* Usare le classi essenziali di CommonMark: `Parser`, `Node`, `HtmlRenderer`, `TextContentRenderer`.
* Navigare l'albero sintattico (**AST**) con `AbstractVisitor` (`Heading`, `Link`, `StrongEmphasis`, `FencedCodeBlock`, `Text`).
* Proteggere il rendering da attacchi **XSS** con `escapeHtml(true)` e `sanitizeUrls(true)`.
* Esportare una pagina `.html` pronta per il browser.

## 📝 Scenario
Stai costruendo il sito del club gaming della scuola: ogni studente scrive la recensione del proprio videogioco preferito in **Markdown** (facile e veloce da scrivere). Il tuo programma Java deve trasformarla in HTML, generare un'anteprima testuale per le notifiche, analizzarne la struttura e bloccare eventuali recensioni "malevole".

## ▶️ Esecuzione

```bash
cd 01-scheda-gioco
java -cp "../lib/*" SchedaGiocoApp.java
```

Il file [`SchedaGiocoApp.java`](./SchedaGiocoApp.java) **compila già**: dopo ogni passo togli i `//` dal blocco di codice, riempi gli spazi `___` e riesegui.

## 🪜 Passi

| Passo | Cosa fare | Classi / metodi |
|:-:|:--|:--|
| **1** | Personalizza il text block `schedaMd` con il **tuo** videogioco preferito (titoli `#`/`##`, `**grassetto**`, `*corsivo*`, lista, citazione, blocco di codice, almeno 2 link) | Text block `"""` |
| **2** | Fai il parsing in un albero AST e conta quanti **blocchi principali** contiene (ciclo `getFirstChild()` → `getNext()`) | `Parser.builder().build()`, `parse()`, `Node` |
| **3** | Trasforma l'AST in HTML e confronta la lunghezza in caratteri tra Markdown e HTML | `HtmlRenderer`, `render()` |
| **4** | Estrai il testo puro, stampa un'anteprima da 100 caratteri e conta le parole | `TextContentRenderer` |
| **5** | Scrivi un `AbstractVisitor` che raccoglie tutti i **titoli**, tutti i **link** e conta parole in **grassetto** e **blocchi di codice** | `AbstractVisitor`, `Heading`, `Link`, `StrongEmphasis`, `FencedCodeBlock` |
| **6** | Testa una recensione con codice XSS (`<img onerror=...>` e `javascript:...`) usando un renderer **sicuro** | `.escapeHtml(true)`, `.sanitizeUrls(true)` |
| **7** | Salva la pagina completa in `scheda_gioco.html` e aprila nel browser | `Files.writeString`, `StandardCharsets.UTF_8` |

### ✅ Output atteso (parziale)

```text
PASSO 2 - AST creato: Document (blocchi principali: 10)

PASSO 3 - Confronto lunghezze:
  Markdown = 620 caratteri  |  HTML = 845 caratteri

PASSO 4 - Anteprima notifica (max 100 caratteri):
  Hollow Knight Hollow Knight e' un metroidvania indie sviluppato da Team Cherry...
  Parole stimate: 87

PASSO 5 - Analisi AST:
  Titoli (3): [H1: Hollow Knight, H2: Perche' giocarci, H2: Comandi rapidi]
  Link   (2): [https://www.hollowknight.com, https://store.steampowered.com/app/367520]
  Grassetti : 4  |  Blocchi di codice: 1

PASSO 6 - Test sicurezza XSS:
  [SICURO] <p>Gioco bellissimo! &lt;img src=x onerror=&quot;alert('hack')&quot;&gt; ...
```

## ❓ Domande di verifica
1. Perché l'HTML generato nel Passo 3 occupa quasi sempre più caratteri del Markdown originale?
2. Nel Passo 4, perché conviene contare le parole sul testo prodotto da `TextContentRenderer` invece che sulla stringa Markdown grezza?
3. Cosa succede nel Passo 5 se dimentichi `visitChildren(heading)` dentro `visit(Heading heading)`?
4. Che differenza c'è tra `.escapeHtml(true)` e `.sanitizeUrls(true)`? Perché servono **entrambi**?

## 🚀 Sfida extra (facoltativa)
Aggiungi un secondo `AbstractVisitor` che controlla se tutti i link della scheda iniziano con `https://` e stampa un avviso `[ATTENZIONE]` per ogni link non sicuro (`http://`).

➡️ Quando hai finito passa all'[esercizio 02](../02-diario-torneo/).
