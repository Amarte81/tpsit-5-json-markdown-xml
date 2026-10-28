# SCHEDA OPERATIVA STUDENTI: GUIDA AI LINGUAGGI DI INTERSCAMBIO DATI E MARKUP
**Classe 5ª Informatica e Telecomunicazioni — TPSIT (135 Minuti Totali)**
*Materiale didattico ad alta densità informativa, accessibile e conforme PDP/DSA*

---

## 🌐 INTRODUZIONE: A COSA SERVONO I LINGUAGGI DI INTERSCAMBIO DEI DATI?

Nelle reti informatiche moderne, programmi scritti in **linguaggi diversi** devono comunicare continuamente tra loro. Ad esempio:
* Una centralina domotica programmata in **C** legge un sensore di temperatura.
* La centralina invia il dato via rete a un server centrale scritto in **Java**.
* Il server Java elabora il dato e lo mostra su uno smartphone o in un browser **JavaScript**.

Questi sistemi sono **eterogenei**: una `struct` in C, un oggetto nella memoria RAM di **Java** e una variabile del browser hanno formati binari incompatibili. Non è possibile spedire sulla rete un indirizzo di memoria RAM.

Per far parlare questi sistemi serve una **lingua franca testuale**, standardizzata e indipendente dall'hardware:
1. **Serializzazione**: il programma mittente trasforma il proprio oggetto in memoria in una **stringa di testo strutturata** (codificata in **UTF-8**) pronta per viaggiare in rete.
2. **Deserializzazione (Parsing)**: il programma destinatario legge la stringa ricevuta, ne controlla la sintassi e ricostruisce l'oggetto nelle proprie variabili.
3. **Scambio Dati vs Lettura Umana**: linguaggi come **JSON** e **XML** servono per scambiare dati strutturati tra **macchine**, mentre linguaggi di **markup leggero** come **Markdown** servono per impaginare testo tecnico destinato agli **esseri umani** e alle **Intelligenze Artificiali**.

---

## 1️⃣ PARTE 1: IL LINGUAGGIO DELLE MACCHINE E DELLE API — JSON (45 min)

### 📖 Spiegazione Teorica: Come funziona JSON
**JSON** (*JavaScript Object Notation*) è il formato standard per le **API Web REST**, le applicazioni mobile e i dispositivi IoT. Anche se il nome cita JavaScript, JSON è **indipendente dal linguaggio** ed è supportato in **Java**, **C**, **Python** e ogni altro ambiente moderno.

JSON rappresenta qualsiasi struttura dati combinando solo **due elementi**:
* **L'Oggetto `{ ... }`**: racchiuso tra parentesi graffe, contiene coppie `"chiave": valore` separate da virgola. Corrisponde a un'istanza di una **classe Java** (`new Player()`), dove ogni **chiave** è un attributo e deve avere **sempre i doppi apici**.
* **L'Array `[ ... ]`**: racchiuso tra parentesi quadre, contiene una lista ordinata di elementi separati da virgola. Corrisponde a un **array** (`String[]`) o a un `ArrayList` in Java.

### Tabella di Traduzione Rapida: Da Java a JSON

| Concetto in Java / C | Come si scrive in JSON | Regola Obbligatoria (Zero Errori) |
| :--- | :--- | :--- |
| **Oggetto** (`class Player`) | `{ ... }` | Si apre e si chiude con parentesi graffe bilanciate. |
| **Attributo** (`String nick = "Gio";`) | `"nick": "Gio"` | Il nome della **chiave** vuole **SEMPRE i doppi apici** `" "`. |
| **Numero** (`int hp = 100;` / `double t = 22.5;`) | `"hp": 100` / `"t": 22.5` | I numeri **NON** vogliono virgolette. Separatore decimale con il **punto**. |
| **Booleano** (`boolean online = true;`) | `"online": true` | Scrivi sempre in **minuscolo**: `true` o `false` (mai `TRUE`). |
| **Array / Lista** (`String[] items`) | `["spada", "scudo"]` | Tra quadre `[ ]`, separati da virgole. **Nessuna virgola finale** prima di `]`. |

### I 4 Errori JSON più Frequenti (Caccia all'Anomalia):
1. **Chiavi senza doppi apici**: `{ utente: "Marco" }` ❌ $\rightarrow$ `{"utente": "Marco"}` ✅.
2. **Trailing comma (virgola finale)**: `{"a": 1, "b": 2,}` ❌ (la virgola prima di `}` o `]` blocca il parser).
3. **Tipo incompatibile con Java**: `"temperatura": "24.5°C"` (stringa) quando il server Java attende un `double` numerico (`24.5`).
4. **Booleano maiuscolo**: `TRUE` o `FALSE` non sono ammessi dallo standard JSON.

### ☕ Uso in Java: Serializzazione e Deserializzazione con Gson / Jackson
In Java si usano librerie come **Gson** (`com.google.gson.Gson`) o **Jackson** (`ObjectMapper`):
```java
// SERIALIZZAZIONE: da Oggetto Java a Stringa JSON
Sensore s1 = new Sensore("ESP32_01", 24.5, true);
String jsonPayload = new Gson().toJson(s1);

// DESERIALIZZAZIONE: da Stringa JSON a Oggetto Java tipizzato
Sensore s2 = new Gson().fromJson(jsonPayload, Sensore.class);
```
* **Regola Java**: la classe deve possedere un **costruttore vuoto senza argomenti** e i nomi degli attributi privati devono coincidere con le chiavi JSON (oppure usare annotazioni come `@SerializedName("chiave")`).

---

## 2️⃣ PARTE 2: IL LINGUAGGIO PER UMANI, SVILUPPATORI E IA — MARKDOWN (45 min)

### 📖 Spiegazione Teorica: Perché Markdown ha sostituito Word nello sviluppo software
**JSON** è ottimo per i programmi ma è scomodo da leggere per una persona che cerca istruzioni. Al contrario, i file di videoscrittura tradizionali (come `.docx` di Word) sono file binari pesanti che non possono essere confrontati riga per riga da **Git**.

**Markdown** (file `.md`) risolve questo problema: è un **linguaggio di markup leggero** che usa semplici simboli della tastiera (`#`, `*`, `-`, `` ` ``) per formattare il testo.
* È **leggibile subito** anche come testo puro nel terminale, ma diventa una pagina **HTML** ordinata su **GitHub**, **Discord** e **WhatsApp**.
* È il **linguaggio nativo delle Intelligenze Artificiali (LLM)**: ChatGPT, Gemini e Claude ricevono prompt e generano codice e tabelle usando **Markdown** perché è compatto e privo di tag ridondanti.

### I 6 Costrutti Fondamentali di Markdown

| Obiettivo Visivo | Sintassi Markdown | Regola Pratica per Non Sbagliare |
| :--- | :--- | :--- |
| **Titoli (H1, H2, H3)** | `# H1` • `## H2` • `### H3` | **Spazio obbligatorio** dopo il cancelletto `#`. Senza spazio viene letto come hashtag! |
| **Grassetto & Corsivo** | `**grassetto**` e `*corsivo*` | Chiudi sempre gli asterischi prima di andare a capo. |
| **Codice Inline** | `` `variabile` `` | Usa un singolo backtick (accento grave). |
| **Blocco Codice Multilinea** | ```` ```java ... ``` ```` | Tre backtick all'inizio (con nome linguaggio) e tre backtick per chiudere su riga nuova. |
| **Tabella Tecnica** | `| Col1 | Col2 |` <br> `| --- | --- |` | La **seconda riga con i trattini** è obbligatoria per generare le colonne! |
| **Link Cliccabile** | `[Testo Visibile](https://...)` | Prima le **[quadre col testo]**, poi le **(tonde con l'URL)**. Non invertirle! |

### ☕ Uso in Java: Javadoc Markdown (Java 23+ JEP 467) e Bot Discord
Dalla versione **Java 23 (JEP 467)** la documentazione ufficiale **Javadoc** non richiede più i tag HTML verbosi ma si scrive direttamente in Markdown anteponendo tre barre `///`:
```java
/// # GestoreServer
/// Servizio per la gestione del cluster.
/// 
/// ## Esempio:
/// ```java
/// GestoreServer g = new GestoreServer(8080);
/// ```
/// | Porta | Protocollo |
/// | :--- | :--- |
/// | 8080 | TCP |
public class GestoreServer { ... }
```
Inoltre, nei bot Java per Discord (libreria **JDA**) o con framework AI (**Spring AI** / **LangChain4j**), i messaggi di risposta vengono costruiti e ricevuti formattando il testo con Markdown.

---

## 3️⃣ PARTE 3: L'ARCHITETTO DEL DATO — SCELTA & CONFRONTO CON XML (45 min)

### 📖 Spiegazione Teorica: Il Confronto con XML e i Criteri di Scelta
Prima di JSON, lo standard per lo scambio dati era **XML** (*eXtensible Markup Language*). In XML il progettista inventa i propri tag (es. `<sensore id="1"><temp>24.5</temp></sensore>`) creando una struttura ad albero.

Oggi nelle API web e nell'IoT si preferisce **JSON** perché **XML è molto verboso**: ripetere il tag di apertura e di chiusura raddoppia i byte trasmessi in rete e richiede in Java parser più complessi (**DOM**, che carica tutto l'albero in RAM, o **SAX** a eventi).
Tuttavia **XML resta fondamentale** quando servono **attributi sui tag**, *namespace* e soprattutto una **validazione formale rigida** tramite schemi **XSD** (*XML Schema Definition*) o **DTD**: ad esempio nel file `pom.xml` di **Maven** in Java, nei layout **Android** e nei contratti informatici bancari o ministeriali.

### Matrice Decisionale a 3 Vie: Quale Linguaggio Scelgo?

| Criterio Ingegneristico | Scegli **JSON** (`.json`) | Scegli **Markdown** (`.md`) | Scegli **XML** (`.xml`) |
| :--- | :--- | :--- | :--- |
| **Destinatario del File** | **Macchina $\leftrightarrow$ Macchina** (API REST, App Mobile, Server Java, Microcontrollori C). | **Umano $\leftrightarrow$ Umano / Modello IA** (Guide GitHub, chat, prompt). | **Sistemi Enterprise** che richiedono contratti formali e validazione con schema **XSD/DTD**. |
| **Consumo di Rete (Byte)** | **Minimo** (elimina i tag di chiusura; risparmia dal 40% al 50% di banda rispetto a XML). | **Puro Testo** (non adatto per parsing automatico di variabili in memoria). | **Alto** (la ripetizione di `<tag>...</tag>` raddoppia i byte trasmessi). |
| **Parsing in Java** | Immediato e leggero tramite librerie `Gson` / `Jackson`. | Non supporta la mappatura diretta in variabili e oggetti Java. | Parser **DOM** (albero in RAM) o **SAX** (a eventi) con validazione **XSD**. |
| **Esempio Reale** | Payload JSON delle API web e salvataggio inventario di gioco. | File `README.md` di un progetto su GitHub o risposte di un'IA. | File `pom.xml` per le dipendenze Maven o layout interfaccia Android. |

### ☕ Uso in Java: Parser DOM, SAX e Binding JAXB
In Java la gestione di XML avviene tramite il package `javax.xml.parsers`:
```java
// 1. Parser DOM (carica tutto l'albero XML nella memoria RAM)
DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
Document doc = builder.parse(new ByteArrayInputStream(xmlString.getBytes()));
String temp = doc.getElementsByTagName("temp").item(0).getTextContent();

// 2. Parser SAX (lettura a eventi in streaming, zero spreco di RAM per file enormi)
// 3. JAXB: Marshalling (Java -> XML) e Unmarshalling (XML -> Java) con @XmlRootElement
```

---

## 🎯 CHECKLIST DI ISPEZIONE SUI 3 CASI REALI (SFIDA FINALE)

Usa questa checklist prima di confermare l'invio nella Web App:
* [ ] **Caso Reale 1 (Smart Home IoT $\rightarrow$ Server Java)**: Ho selezionato **JSON**, messo i doppi apici sulla chiave `"stanza"`, convertito la temperatura in `double` numerico (`24.5`), scritto il booleano `true` in minuscolo e rimosso la virgola finale.
* [ ] **Caso Reale 2 (Guida GitHub & Prompt IA)**: Ho selezionato **Markdown**, inserito lo spazio dopo `#`, chiuso il grassetto `**Java**` e il blocco codice con ```` ``` ````, aggiunto la riga `| --- | --- |` nella tabella e corretto il link in `[Repository Ufficiale](https://github.com/classe5/bot)`.
* [ ] **Caso Reale 3 (Webhook Discord Ibrido & Scelta XML)**: Ho indicato che **XML** si usa per `pom.xml` di Maven/XSD, che il webhook usa un **involucro JSON con testo Markdown**, e ho riparato la virgola, il booleano `false` e il grassetto `**Torneo Minecraft**`.
