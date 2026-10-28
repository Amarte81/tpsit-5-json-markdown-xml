# Progettazione Didattica: Linguaggi di Interscambio Dati e Markup Leggero (JSON, Markdown, XML)

## 1. Inquadramento Curricolare e Programmatico

| Voce | Dettaglio Tecnico e Normativo |
| :--- | :--- |
| **Destinatari** | **Classe Quinta** – Istituto Tecnico Settore Tecnologico (Indirizzo Informatica e Telecomunicazioni), **24 studenti** (17-18 anni). |
| **Profilo della Classe** | Buona padronanza delle **architetture Web**, conoscenze **base di C** e **buone di Java** (OOP, classi, array/collezioni). Alta familiarità con social/gaming; scarsa propensione allo studio domestico prolungato; forte preferenza per **testi ad alta densità informativa**, schematici e immediatamente operativi. |
| **Riferimento Manuale** | P. Camagni, R. Nikolassy, *Nuovo Tecnologie e progettazione di sistemi informatici e di telecomunicazioni – Vol. 3*, Hoepli (Unità su *Tecnologie XML e JSON*, *Serializzazione dati* e *Web Services REST*). |
| **Direttive Ministeriali TPSIT (V anno)** | • **Conoscenze**: Formati standard per la rappresentazione e lo scambio di dati tra sistemi eterogenei (XML, JSON); linguaggi di markup per la documentazione tecnica (Markdown).<br>• **Abilità**: Sintetizzare, validare e trasformare strutture dati per la comunicazione client-server; individuare anomalie sintattiche/strutturali nei payload di rete.<br>• **Competenze**: Scegliere il formato di serializzazione o markup più adeguato in base a vincoli di banda, leggibilità umana, struttura dati e architettura applicativa. |
| **Competenze Digitali (DigComp 2.2)** | • **1.2 Valutare dati e informazioni digitali**: Ispezionare criticamente flussi di dati e messaggi prima di elaborarli o eseguirli.<br>• **3.1 Sviluppare contenuti digitali**: Produrre strutture dati ben formate in JSON e documentazione tecnica in Markdown.<br>• **5.2 Individuare fabbisogni e risposte tecnologiche**: Selezionare razionalmente tra JSON, Markdown e XML in base al contesto d'uso. |
| **Monte Ore Totale** | **135 minuti esatti**, articolati in **3 lezioni interattive da 45 minuti ciascuna**. |
| **Regole Auree (Modello Fact-Checking)** | 1. **SEMPRE** iniziare con un **aggancio visivo** quotidiano (WhatsApp, Discord/Minecraft, GitHub/AI, IoT).<br>2. **MAI** superare i **15 minuti** di lezione frontale passiva per modulo (target fissato a **12 minuti**).<br>3. **SEMPRE** attività centrale attiva/interattiva a coppie con **griglia visuale a passaggi brevi**.<br>4. **MAI** quiz nozionistici: solo **compiti autentici di individuazione e decodifica dell'anomalia**.<br>5. **Criterio di successo**: **≥ 90% degli studenti** (almeno 22 su 24, ovvero 11 coppie su 12) completa con successo la checklist sui **3 casi reali simulati** entro il tempo limite. |

---

## 2. Architettura Oraria delle 3 Lezioni (Totale: 135 Minuti)

```
LEZIONE 1 (45 min) — Dal Post Social al Payload: Sintassi e Struttura di JSON (con ponte Java)
├── [00' - 08'] Fase 1: Aggancio Visivo (Notifica WhatsApp/Telegram & Bot Discord "rotti")
├── [08' - 20'] Fase 2: Pillola Tecnica Ad Alta Densità (12 min max - Da Oggetto Java a JSON)
├── [20' - 38'] Fase 3: Laboratorio Attivo a Coppie — Caso Studio 1 (Ispezione & Fix Payload)
└── [38' - 45'] Fase 4: Debriefing Rapido & Consolidamento sulla Checklist

LEZIONE 2 (45 min) — Testo per Umani e IA: Markdown per Documentazione, Chat e Prompt
├── [00' - 08'] Fase 1: Aggancio Visivo (Messaggio WhatsApp/Discord + Risposta ChatGPT + README GitHub)
├── [08' - 20'] Fase 2: Pillola Tecnica Ad Alta Densità (12 min max - Sintassi MD e Differenza Dati vs Testo)
├── [20' - 38'] Fase 3: Laboratorio Attivo a Coppie — Caso Studio 2 (Decodifica Anomalia README/Webhook)
└── [38' - 45'] Fase 4: Debriefing Rapido & Confronto Diretto JSON vs Markdown

LEZIONE 3 (45 min) — L'Architetto del Dato: Confronto con XML e Scelta del Linguaggio Migliore
├── [00' - 08'] Fase 1: Aggancio Visivo (Centralina Smart Home/Gaming Room -> Server Java & Config XML vs JSON)
├── [08' - 20'] Fase 2: Pillola Tecnica Ad Alta Densità (12 min max - Matrice Decisionale XML vs JSON vs MD)
├── [20' - 38'] Fase 3: Sfida Finale Autentica — I 3 Casi Reali Simulati sulla Web App + Google Workspace
└── [38' - 45'] Fase 4: Verifica Live del KPI (≥ 90% su Google Sheets) e Chiusura
```

---

## 3. Dettaglio Operativo Lezione per Lezione

### LEZIONE 1: Il Linguaggio delle App e delle API — Padroneggiare JSON (45 minuti)

**Obiettivo specifico**: Comprendere come le app quotidiane serializzano gli oggetti in transito, padroneggiare la sintassi JSON (oggetti, array, tipi primitivi) a partire dalle conoscenze di Java/C e diagnosticare anomalie sintattiche che bloccano il parsing.

#### Blocco 1.1 — Aggancio Visivo Quotidiano (8 minuti | `00:00 - 00:08`)
* **Cosa proietta il docente (dalla Web App, Tab "Agganci Visivi")**:
  * A sinistra: lo screenshot simulato di una **notifica push di WhatsApp/Telegram** e la card di un **inventario giocatore di Minecraft / Bot Discord**.
  * A destra: un messaggio che mostra sul telefono la scritta anomala `{"error": "SyntaxError: Unexpected token } in JSON at position 142"}` invece del contenuto del messaggio.
* **Domanda stimolo alla classe (Sospensione del giudizio)**:
  * *"Quando inviate un messaggio su WhatsApp o equipaggiate una spada su Minecraft, sulla rete viaggia l'interfaccia grafica o viaggia del testo? E perché una singola virgola fuori posto può mandare in crash l'intera app?"*
* **Reazione guidata**: Gli studenti riconoscono che tra client (App) e server (es. backend Java) viaggia una stringa strutturata (**serializzazione**).

#### Blocco 1.2 — Pillola Frontale ad Alta Densità: Da Oggetto Java a JSON (12 minuti | `00:08 - 00:20`)
* **Regola**: Massimo 12 minuti, zero digressioni storiche, uso diretto della tabella di mapping **Java ↔ JSON** (in linea con il manuale *Camagni-Nikolassy Vol. 3*):
  * **Oggetto Java (`class Player`)** $\rightarrow$ **Oggetto JSON `{ ... }`**
  * **Attributo (`private String nick;`)** $\rightarrow$ **Coppia `"chiave": valore`** (NB: le chiavi in JSON hanno **sempre** i doppi apici `" "`!).
  * **Array/ArrayList (`String[] items`)** $\rightarrow$ **Array JSON `[ ... ]`**
  * **I 5 errori classici da ispezionare prima di "cliccare/eseguire"**:
    1. Chiavi senza doppi apici o con apici singoli `'chiave'`.
    2. **Trailing comma** (virgola finale dopo l'ultimo elemento prima di `}` o `]`).
    3. Virgola mancante tra due proprietà consecutive.
    4. Valori booleani o nulli scritti con maiuscola (`True`, `NULL`) invece di `true`, `false`, `null`.
    5. Parentesi `{ }` o `[ ]` non bilanciate.

#### Blocco 1.3 — Attività Interattiva a Coppie: "Ispeziona e Ripara il Payload" (18 minuti | `00:20 - 00:38`)
* **Modalità**: Lavoro a coppie su PC o smartphone tramite la **Web App interattiva** (Sezione *Laboratorio & Checklist*).
* **Scenario Tecnico**: Un sensore IoT della Gaming Room (temperatura LED/PC) e un inventario Minecraft inviano dati a un server Java (`Jackson`/`Gson`), ma il parser rifiuta il pacchetto.
* **Compito della coppia**:
  1. Applicare la **Griglia di Ispezione in 4 Passi**:
     * **Passo 1 (STOP)**: Non forzare l'invio; identifica il contesto (Chi parla con chi?).
     * **Passo 2 (STRUTTURA)**: Controlla i delimitatori esterni (`{}` o `[]`) e l'annidamento.
     * **Passo 3 (SINTASSI)**: Verifica doppi apici sulle chiavi, virgole e tipi di dato (`number`, `boolean`, `string`).
     * **Passo 4 (VALIDAZIONE)**: Correggi nell'editor live finché il semaforo diventa **VERDE (JSON Ben Formato)**.

#### Blocco 1.4 — Restituzione Lampo e Take-away (7 minuti | `00:38 - 00:45`)
* Confronto rapido su LIM delle 3 anomalie scoperte dalle coppie (es. una temperatura `"24.5°C"` passata come stringa invece del numero `24.5` atteso dal `double` in Java).

---

### LEZIONE 2: Il Linguaggio per Umani, Sviluppatori e IA — Padroneggiare Markdown (45 minuti)

**Obiettivo specifico**: Comprendere perché JSON non è adatto a scrivere testo formattato leggibile dall'uomo; padroneggiare la sintassi **Markdown (`.md`)** usata su GitHub, Discord, WhatsApp e nei prompt/output delle Intelligenze Artificiali; decodificare anomalie di rendering.

#### Blocco 2.1 — Aggancio Visivo Quotidiano (8 minuti | `00:00 - 00:08`)
* **Cosa proietta il docente**:
  * Tre schermate affiancate:
    1. Un messaggio su **WhatsApp/Discord** con grassetto `*importante*` / `**importante**` e blocchi codice ```` ```java ````.
    2. Una risposta di **ChatGPT / Gemini** copiata "grezza" (raw) piena di `#`, `**`, `-`, `|---|---|`.
    3. La pagina principale (`README.md`) di un progetto open-source su **GitHub**.
* **Domanda stimolo**:
  * *"Cosa hanno in comune WhatsApp, Discord, GitHub e tutte le Intelligenze Artificiali moderne? Perché un programmatore non scrive la documentazione tecnica in Word (`.docx`) o dentro un file JSON?"*

#### Blocco 2.2 — Pillola Frontale ad Alta Densità: Sintassi Markdown Essenziale (12 minuti | `00:08 - 00:20`)
* **Sintassi ad alta densità (6 costrutti fondamentali)**:
  * **Gerarchia titoli**: `# H1`, `## H2`, `### H3` (spazio obbligatorio dopo `#`!).
  * **Enfasi**: `**grassetto**`, `*corsivo*`, `` `codice inline` ``.
  * **Blocchi di codice con syntax highlighting**: triplo backtick ```` ```java ... ``` ````.
  * **Liste**: `- elemento` (puntata) e `1. elemento` (numerata).
  * **Link e Immagini**: `[Testo visibile](https://url...)` vs `![Alt immagine](url)`.
  * **Tabelle tecniche**: pipe `|` e riga separatrice `| :--- | :--- |`.
* **Regola d'oro architetturale**:
  * **JSON** = *Macchina $\leftrightarrow$ Macchina* (dati strutturati chiave-valore per il codice).
  * **Markdown** = *Umano/IA $\leftrightarrow$ Umano* (testo formattato, leggero, versionabile su Git).

#### Blocco 2.3 — Attività Interattiva a Coppie: "Ripara il README e il Webhook Discord" (18 minuti | `00:20 - 00:38`)
* **Modalità**: Sulla Web App interattiva (Live Editor Markdown con anteprima in tempo reale).
* **Scenario Tecnico**: Un team di sviluppatori ha pubblicato su GitHub il `README.md` delle API del proprio progetto Java e un annuncio su Discord Bot, ma la pagina è illeggibile: i titoli non funzionano (`#Titolo` senza spazio), il blocco di codice Java non si chiude e rompe tutta la pagina, la tabella dei parametri API è collassata e il link alla documentazione è invertito `(url)[testo]`.
* **Compito della coppia**: Individuare le **4 anomalie Markdown**, ripararle nell'editor live verificando il rendering istantaneo e trasformare una specifica testuale confusa in una tabella Markdown pulita.

#### Blocco 2.4 — Restituzione Lampo: L'Ibrido Reale (JSON + Markdown) (7 minuti | `00:38 - 00:45`)
* **Colpo di scena tecnico**: Il docente mostra sulla Web App un vero **Webhook di Discord** o una **chiamata API a un LLM**:
  * Il **contenitore di trasporto** sulla rete è **JSON** (`{"username": "ServerBot", "content": "## Stato Server\n- **CPU**: `42%`"}`), mentre il **valore testuale** dentro `"content"` è scritto in **Markdown**! Gli studenti vedono come i due linguaggi collaborano nel mondo reale.

---

### LEZIONE 3: La Scelta Architetturale — JSON vs Markdown vs XML e Sfida dei 3 Casi Reali (45 minuti)

**Obiettivo specifico**: Confrontare JSON e Markdown con **XML** (come da programma ministeriale TPSIT e manuale *Camagni-Nikolassy*), applicare criteri ingegneristici oggettivi per scegliere il linguaggio migliore in base al contesto e completare la verifica autentica sui **3 casi reali simulati**.

#### Blocco 3.1 — Aggancio Visivo: "Il Peso dei Dati" (8 minuti | `00:00 - 00:08`)
* **Cosa proietta il docente (Tab "Confronto & Peso Byte" sulla Web App)**:
  * Lo stesso identico dato (la telemetria di una centralina meteo/domotica con 3 sensori) scritto in **XML** (295 byte), in **JSON** (148 byte: **-50% di traffico dati!**) e in **Markdown** (tabella per un report umano).
* **Domanda stimolo**:
  * *"Se avete 1.000 sensori IoT con scheda SIM a consumo o un'app mobile sotto rete 4G debole, quale formato usate per inviare i dati al server Java? E perché allora XML esiste ancora (es. in `pom.xml` di Maven in Java o nei layout Android)?"*

#### Blocco 3.2 — Pillola Frontale ad Alta Densità: Matrice di Scelta Tecnica (12 minuti | `00:08 - 00:20`)
* Analisi della **Matrice Decisionale a 4 Criteri** (presente anche nella Scheda Studenti):

| Criterio Tecnico | **JSON** (`.json`) | **Markdown** (`.md`) | **XML** (`.xml`) |
| :--- | :--- | :--- | :--- |
| **Scopo Primario** | Scambio dati **App $\leftrightarrow$ Server** (API REST, config leggere, stato). | **Documentazione umana**, README, chat, prompt/output IA. | Documenti complessi, **validazione rigorosa (XSD)**, config enterprise (`pom.xml`, Android UI). |
| **Verbosity (Peso Byte)** | **Basso** (leggero e veloce da parsare). | **Minimo** (puro testo con simboli leggeri). | **Alto** (tag di apertura e chiusura ripetuti `<tag>...</tag>`). |
| **Parsing in Java / JS** | Nativi in JS (`JSON.parse`); rapidi in Java (`Gson`/`Jackson`). | Non pensato per estrarre campi dati in variabili Java. | Parser **DOM** (albero in RAM) o **SAX** (a eventi), validazione **DTD/XSD**. |
| **Quando sceglierlo** | **90% delle Web API moderne**, mobile, IoT, giochi, microservizi. | Qualsiasi testo che debba essere **letto da persone** o generato da LLM. | Quando servono **attributi sui tag**, namespace multipli o contratti formali rigidi. |

#### Blocco 3.3 — Compito Autentico Finale: "La Checklist dei 3 Casi Reali Simulati" (18 minuti | `00:20 - 00:38`)
* **Nessun quiz a crocette mnemonico!** Gli studenti lavorano sulla sezione **"Sfida Finale: I 3 Casi Reali"** della Web App.
* Per ciascuno dei **3 Casi Reali Simulati**, ogni studente/coppia deve completare i **4 step della Checklist di Ispezione**:
  1. **CASO REALE 1 (Smart Home / IoT $\rightarrow$ Server Java)**:
     * *Anomalia da decodificare*: Un collega ha configurato il microcontrollore C/ESP32 per inviare la telemetria al backend Java usando una tabella Markdown invece di un oggetto JSON, e nel tentativo di convertirlo in JSON ha introdotto 3 errori sintattici (chiave senza virgolette, valore booleano `TRUE` maiuscolo, virgola finale).
     * *Azione richiesta*: Selezionare il linguaggio corretto (**JSON**), motivare il perché tecnico (parsing automatico in oggetto Java + basso consumo di banda), individuare e correggere l'anomalia nell'editor.
  2. **CASO REALE 2 (Documentazione API GitHub & Output IA per il Team)**:
     * *Anomalia da decodificare*: Uno sviluppatore ha scritto le istruzioni di installazione del progetto e il changelog per i compagni dentro un file XML verboso e illeggibile, oppure in un `.md` con blocchi codice e titoli corrotti.
     * *Azione richiesta*: Selezionare il linguaggio corretto (**Markdown**), motivare la scelta (leggibilità umana immediata, rendering nativo su GitHub, zero overhead di tag), e riparare il documento `.md` corrotto.
  3. **CASO REALE 3 (Notifica Push / Webhook Bot Discord con Architettura Ibrida vs XML Legacy)**:
     * *Anomalia da decodificare*: Analisi comparativa tra un pacchetto di configurazione/interscambio complesso (dove occorre distinguere quando è obbligatorio mantenere XML per vincoli di schema XSD/Maven e quando usare JSON con payload testuale Markdown in un webhook). Correzione del payload JSON+MD del webhook prima dell'invio.
* **Invio Risultati**: Al termine dei 3 casi, un clic su **"Invia Ispezione sul Dominio Scuola"** registra l'esito (punteggio, tempo, anomalie risolte) sul foglio Google Sheets del docente tramite Google Apps Script.

#### Blocco 3.4 — Verifica Live del Criterio di Successo (≥ 90%) e Debriefing (7 minuti | `00:38 - 00:45`)
* Il docente proietta la dashboard del **Google Sheet** (o il riepilogo della Web App):
  * Verifica che **almeno il 90% della classe** abbia superato tutti e 3 i casi reali.
  * Analisi collettiva di eventuali errori ricorrenti emersi in tempo reale.

---

## 4. Criteri di Valutazione Autentica (Rubrica per il Docente)

| Livello | Completamento Checklist (3 Casi) | Decodifica e Correzione Anomalie (JSON e MD) | Capacità di Scelta Architetturale (JSON vs MD vs XML) | Voto Indicativo |
| :--- | :--- | :--- | :--- | :---: |
| **Avanzato** | 3 casi su 3 completati entro i 18 min. | Individua autonomamente tutte le anomalie sintattiche e di tipo (es. tipi Java vs JSON) e scrive codice pulito. | Giustifica con precisione tecnica la scelta citando banda, parser (DOM/Gson), leggibilità e scopo del dato. | **9 – 10** |
| **Intermedio (Target ≥ 90%)** | 3 casi su 3 completati entro il tempo limite. | Individua e corregge le anomalie principali usando il validatore della checklist. | Sceglie il linguaggio corretto in tutti i contesti distinguendo chiaramente dati per macchine vs testi per umani. | **7 – 8** |
| **Base** | Almeno 2 casi su 3 completati. | Corregge gli errori sintattici più evidenti con il supporto della griglia guidata. | Riconosce la differenza base tra JSON e Markdown ma fatica a contestualizzare il ruolo di XML. | **6** |
| **In via di acquisizione** | Meno di 2 casi completati. | Non sospende il giudizio e non utilizza sistematicamente la checklist per isolare l'errore. | Confonde i formati di serializzazione dati con i linguaggi di formattazione del testo. | **< 6** |
