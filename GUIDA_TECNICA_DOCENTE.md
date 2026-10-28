# 🛠️ GUIDA TECNICA E OPERATIVA PER IL DOCENTE
**Configurazione, Requisiti, Messa in Opera e Soluzioni del Laboratorio TPSIT (JSON, Markdown, XML)**

---

## 1. Panoramica dei File del Progetto

| File | Scopo e Destinatario |
| :--- | :--- |
| `index.html`, `styles.css`, `app.js` | **Web App Interattiva (Single Page)** per gli studenti e per la proiezione su LIM. Funziona su PC (16:9) e Smartphone (9:16) senza installazioni. |
| `scheda_operativa_studenti.md` | **Scheda Operativa Studenti** ad alta densità informativa e conforme PDP/DSA (stampabile o distribuibile su Classroom). |
| `piano_lezioni_docente.md` | **Piano Didattico delle 3 Lezioni da 45 minuti** (135 min totali), descrittori ministeriali TPSIT V anno, DigComp 2.2 e griglia di valutazione. |
| `google_workspace_script.gs` | **Script Google Apps Script** per raccogliere automaticamente le consegne su Google Sheets d'istituto e calcolare in tempo reale il KPI $\ge 90\%$. |
| `GUIDA_TECNICA_DOCENTE.md` | Questo manuale tecnico di istruzioni, requisiti, setup e **soluzioni complete** degli esercizi. |

---

## 2. Requisiti Tecnici e Conformità Normativa

### Lato Studente (Zero Installazioni)
* **Dispositivo**: Qualsiasi PC del laboratorio di informatica (formato **16:9**) oppure lo smartphone personale dello studente in modalità BYOD (formato verticale **9:16**).
* **Software richiesto**: **Nessuna installazione**. È sufficiente un browser web standard aggiornato (Chrome, Edge, Firefox, Safari).
* **Account**: Nessuna registrazione a siti commerciali esterni. Per la consegna finale, lo studente inserisce semplicemente il proprio indirizzo istituzionale (es. `nome.cognome@scuola.edu.it`) o i nomi della coppia.
* **Accessibilità (PDP / DSA / BES)**:
  * In alto a destra nella barra fissa della Web App è presente il pulsante **"👁️ Alta Leggibilità (DSA/PDP)"** che attiva istantaneamente il tema ad alto contrasto chiaro su sfondo crema caldo (`#fcfbf9`), aumenta l'interlinea a `1.9` e la spaziatura tra caratteri.
  * Il pulsante **"🖨️ Stampa Guida"** formatta automaticamente l'intera pagina per la stampa cartacea o l'esportazione in PDF pulito.

### Lato Docente
* Un browser web e la LIM / proiettore d'aula.
* *(Opzionale ma consigliato per la raccolta centralizzata)*: Un account **Google Workspace for Education** (`@scuola.edu.it`) con accesso a **Google Fogli (Google Sheets)**.

---

## 3. Come Avviare e Distribuire la Web App agli Studenti

Hai a disposizione **2 modalità** (utilizzabili anche contemporaneamente):

### Modalità A — Online tramite GitHub Pages (Consigliata per Smartphone BYOD e Lab)
La Web App è pubblicata su **GitHub Pages**:
1. Condividi il link pubblico di GitHub Pages su **Google Classroom** (oppure genera un codice QR alla LIM).
2. Gli studenti aprono il link dal proprio smartphone o PC: la pagina è completamente autonoma (*client-side*) e non può mai andare in sovraccarico anche se tutti i 24 studenti cliccano contemporaneamente.

### Modalità B — Offline / Locale (Senza connessione Internet)
Se il laboratorio di informatica dovesse avere problemi di rete:
1. Copia i 3 file `index.html`, `styles.css` e `app.js` in una cartella condivisa di rete (o su chiavetta USB).
2. Fai doppio clic su `index.html`: l'intera applicazione, inclusi i 6 mini-test, i validatori live JSON/Markdown, il generatore di classi Java e i 3 casi reali, funziona **al 100% offline**.
3. Al termine, gli studenti cliccano su **"💾 Scarica Report Ispezione (.json)"** e consegnano il file generato.

---

## 4. Configurazione Passo-Passo di Google Workspace (Tempo richiesto: 3 minuti)

Se desideri che il pulsante **"🚀 Invia Risultati a Google Workspace"** salvi automaticamente in tempo reale i risultati delle coppie in una tabella Google Sheets calcolando se almeno il **90% della classe** ha superato la prova, segui questi passaggi:

### Passo 1 — Crea il Foglio Google e Incolla lo Script
1. Accedi a [Google Sheets](https://sheets.google.com) con il tuo account docente `@scuola.edu.it` e crea un **Foglio di lavoro vuoto** (chiamalo ad es. `TPSIT 5A - Risultati JSON e MD`).
2. Dal menu in alto, clicca su **Estensioni** $\rightarrow$ **Apps Script**.
3. Cancella il codice predefinito `function myFunction() {}` e incolla l'intero contenuto del file `google_workspace_script.gs`.
4. Clicca sull'icona del dischetto **Salva progetto** (in alto).

### Passo 2 — Inizializza la Tabella e il Cruscotto KPI ($\ge 90\%$)
1. Nella barra degli strumenti di Apps Script, assicurati che nel menu a tendina delle funzioni sia selezionata **`setupDashboardClasse`** e clicca su **Esegui**.
2. Alla prima esecuzione, Google chiederà di **Autorizzare gli accessi** per il tuo foglio: clicca su *Rivedi autorizzazioni*, seleziona il tuo account e conferma.
3. Torna sul tuo Foglio Google: vedrai che lo script ha creato automaticamente due schede formattate:
   * **`Risultati_Studenti`**: dove appariranno le righe inviate dagli studenti in tempo reale.
   * **`KPI_Didattico_90`**: il cruscotto che calcola automaticamente la percentuale della classe che ha completato i 3 casi su 3 e mostra la scritta **"🎯 OBIETTIVO RAGGIUNTO (>= 90%)"**.

### Passo 3 — Pubblica come Applicazione Web e Ottieni l'URL
1. In Apps Script, clicca sul pulsante blu in alto a destra **Implementa** $\rightarrow$ **Nuova implementazione**.
2. Clicca sull'icona dell'ingranaggio accanto a *Seleziona tipo* e scegli **Applicazione web**.
3. Imposta i parametri:
   * **Descrizione**: `Raccolta Risultati TPSIT 5A`
   * **Esegui come**: **Me stesso** (il tuo indirizzo email docente)
   * **Chi ha accesso**: Seleziona **Chiunque** (oppure *Chiunque all'interno del dominio scuola*, ma **Chiunque** evita blocchi CORS dai browser mobile degli studenti).
4. Clicca su **Implementa** e **copia l'URL dell'applicazione web** (termina con `/exec`).

### Passo 4 — Collega l'URL alla Web App degli Studenti (3 Modi)
Puoi passare l'URL appena copiato agli studenti in tre modi (dal più comodo al più manuale):
* **Metodo 1 (Automatico tramite Link su Classroom - Consigliato)**: Aggiungi `?webhook=TUO_URL` alla fine del link di GitHub Pages quando lo pubblichi su Classroom. Esempio:
  `https://amarte81.github.io/NOME-REPO/?webhook=https://script.google.com/macros/s/AKfycb.../exec`
  *Aprendo quel link, gli studenti troveranno il campo URL già pre-compilato!*
* **Metodo 2 (Fisso nel codice)**: Apri il file `app.js` alla riga 9 e incolla l'URL dentro la costante `const DEFAULT_TEACHER_WEBHOOK_URL = 'https://script.google.com/.../exec';`, poi salva e fai push su GitHub.
* **Metodo 3 (Manuale)**: Gli studenti incollano l'URL direttamente nella casella *"URL Web App Apps Script Docente"* in fondo alla pagina prima di cliccare su Invia.

---

## 5. Soluzioni Complete per il Docente (Mini-Test e 3 Casi Reali)

### Soluzioni dei 6 Mini-Test lungo lo scorrimento
* **Mini-Test 1.1 (Notifica Push JSON)**: Risposta corretta **C** (Chiave `mittente` senza doppi apici, booleano `TRUE` maiuscolo, virgola finale prima di `}`).
* **Mini-Test 1.2 (Array e Tipi Java)**: Risposta corretta **B** (Si scatena una `NumberFormatException` o errore di deserializzazione perché `"42_livello"` non è un `int`).
* **Mini-Test 2.1 (Titoli Markdown)**: Risposta corretta **A** (È obbligatorio inserire uno spazio dopo il cancelletto: `# ProgettoJava`).
* **Mini-Test 2.2 (Link nel README)**: Risposta corretta **A** (`[Link Repository](https://github.com/classe5/repo)` — prima le quadre col testo, poi le tonde con l'URL).
* **Mini-Test 3.1 (Banda IoT JSON vs XML)**: Risposta corretta **A** (JSON elimina i tag di chiusura ridondanti, risparmiando il 40-50% di byte e velocizzando il parsing).
* **Mini-Test 3.2 (Quando usare XML)**: Risposta corretta **B** (Nel file `pom.xml` di Maven o quando è richiesta validazione formale rigorosa tramite schema XSD).

---

### Soluzioni Esatte della Sfida Finale (I 3 Casi Reali Simulati)

#### ✅ Soluzione Caso Reale 1 — Telemetria Smart Home IoT verso Server Java
* **Passo 1 (Formato)**: Selezionare `JSON: leggero sulla rete Wi-Fi e deserializzabile direttamente negli attributi dell'oggetto Java`.
* **Passo 2 (Checklist)**: Spuntare tutte e 3 le caselle di verifica.
* **Passo 3 (Codice JSON riparato da inserire nell'editor)**:
```json
{
  "stanza": "GamingRoom_5A",
  "tempCelsius": 24.5,
  "wattPc": 450,
  "ledAttivi": true
}
```
*(Anomalie corrette: aggiunti doppi apici a `"stanza"`, trasformato `"24.5°C"` nel numero `24.5`, convertito `TRUE` in `true` minuscolo, rimossa la virgola finale).*

---

#### ✅ Soluzione Caso Reale 2 — Documentazione Tecnica GitHub & Prompt IA
* **Passo 1 (Formato)**: Selezionare `Markdown (.md): progettato per la lettura umana, renderizzato nativamente da GitHub e tracciabile con Git`.
* **Passo 2 (Checklist)**: Spuntare tutte e 3 le caselle di verifica.
* **Passo 3 (Codice Markdown riparato da inserire nell'editor)**:
````markdown
# Guida Bot Discord 5A

Per avviare il bot in **Java** esegui questo codice:

```java
BotServer.connect("token_scuola");
```

## Tabella Comandi
| Comando | Descrizione |
| --- | --- |
| !ping | Verifica latenza |

Scarica da: [Repository Ufficiale](https://github.com/classe5/bot)
````
*(Anomalie corrette: spazio dopo `#`, chiuso il grassetto `**Java**`, chiuso il blocco codice con ```` ``` ````, aggiunta la riga `| --- | --- |` nella tabella, invertite le parentesi del link finale).*

---

#### ✅ Soluzione Caso Reale 3 — Notifica Webhook Discord (Ibrido) & Scelta XML
* **Passo 1A (Ruolo XML)**: Selezionare `Nel file pom.xml di Maven per gestire dipendenze e contratti validati con schema XSD`.
* **Passo 1B (Ibrido JSON+MD)**: Selezionare `L'involucro esterno per la rete è un oggetto JSON, mentre il messaggio dentro "content" usa Markdown per il testo formattato`.
* **Passo 2 (Checklist)**: Spuntare entrambe le caselle di verifica.
* **Passo 3 (Payload Webhook riparato da inserire nell'editor)**:
```json
{
  "username": "TorneoBot_5A",
  "tts": false,
  "content": "## Avviso Torneo\nIl server **Torneo Minecraft** inizia alle `16:00`!"
}
```
*(Anomalie corrette: aggiunta la virgola dopo `"TorneoBot_5A"`, convertito `FALSE` in `false` minuscolo, chiuso il grassetto `**Torneo Minecraft**` dentro `"content"`).*
