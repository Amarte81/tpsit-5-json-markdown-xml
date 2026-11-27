# ☕ JSON in Java con Gson — Percorso di esercizi introduttivi

Percorso pratico per le classi **quinte (TPSIT)**: dall'idea di base di JSON fino a un quiz multigiocatore in rete, stile **Kahoot**, con **socket TCP**.
Gli argomenti degli esercizi (Spotify, serie TV, quiz su musica, calcio, videogiochi, anime) sono scelti apposta per essere **vicini ai ragazzi di 17-18 anni**.

| # | Cartella | Tipo | Argomento | Cosa si impara | Tempo indicativo |
|:-:|:--|:--|:--|:--|:-:|
| 00 | [`00-esempio-spotify`](./00-esempio-spotify/) | 📖 Esempio commentato | Il brano del momento (Spotify) | Ciclo completo `toJson` / `fromJson`, POJO, `GsonBuilder` | 20 min |
| 01 | [`01-serie-tv`](./01-serie-tv/) | ✏️ Esercizio guidato | La mia serie TV preferita | Variante dell'esempio: classi e costrutti essenziali | 45 min |
| 02 | [`02-watchlist`](./02-watchlist/) | ✏️ Esercizio guidato | La mia Watchlist | **Tutte le funzioni essenziali**: file, annidamento, liste, enum, `null`, `@SerializedName`, `transient`, `TypeToken`, `Map`, tree model, errori | 90 min |
| 03 | [`03-quiz-socket`](./03-quiz-socket/) | ✏️ Esercizio guidato + gioco in classe | QuizBattle (stile Kahoot) | JSON come **protocollo applicativo** su socket TCP, client/server multi-thread | 90-135 min |
| — | Google Drive (accesso riservato) | 🔒 Per il docente | — | Soluzioni complete e risposte di verifica | — |

```mermaid
flowchart LR
    A["00 Esempio<br/>Brano Spotify"] --> B["01 Serie TV<br/>toJson / fromJson"]
    B --> C["02 Watchlist<br/>file, liste, Map, albero"]
    C --> D["03 QuizBattle<br/>JSON su socket"]
```

---

## ⚙️ Requisiti e avvio (zero configurazione)

* **Java 17 o superiore** (si usano i *text block* `"""` e lo `switch` con `->`).
* La libreria **Gson 2.11.0** è già inclusa in [`lib/gson-2.11.0.jar`](./lib/): **non serve Internet né Maven**.
* Ogni programma è **un solo file `.java`**: Java 11+ lo esegue direttamente, senza `javac`.

Da dentro la cartella dell'esercizio (Windows, Linux e macOS):

```bash
java -cp ../lib/gson-2.11.0.jar NomeFile.java
```

> [!TIP]
> Se usate un IDE (IntelliJ, NetBeans, VS Code, Eclipse) aggiungete `lib/gson-2.11.0.jar` alle librerie del progetto, oppure con Maven/Gradle:
>
> ```xml
> <dependency>
>   <groupId>com.google.code.gson</groupId>
>   <artifactId>gson</artifactId>
>   <version>2.11.0</version>
> </dependency>
> ```

---

## 🗺️ Mappa JSON ↔ Java (da tenere sotto mano)

| JSON | Esempio JSON | Java |
|:--|:--|:--|
| stringa | `"Netflix"` | `String` |
| numero intero | `5` | `int`, `long` |
| numero decimale | `8.5` | `double` |
| booleano | `true` / `false` | `boolean` |
| `null` | `null` | `null` (solo per tipi oggetto: `String`, classi, liste…) |
| oggetto | `{ "stagione": 4, "episodio": 6 }` | una **classe** (POJO) oppure `Map<String, …>` oppure `JsonObject` |
| array | `["Horror", "Sci-Fi"]` | `List<…>` (o array Java) oppure `JsonArray` |
| stringa "fissa" | `"IN_CORSO"` | `enum` |

## 🧰 Le classi Gson usate nel percorso

| Classe / costrutto | A cosa serve | Esercizio |
|:--|:--|:-:|
| `Gson` | Il "traduttore": `toJson(oggetto)` e `fromJson(testo, Classe.class)` | 00, 01 |
| `GsonBuilder` | Configura un Gson: `setPrettyPrinting()`, `serializeNulls()`, `disableHtmlEscaping()` | 00, 01, 02, 03 |
| `JsonSyntaxException` | Errore: JSON malformato o con tipi sbagliati | 01, 02 |
| `fromJson(Reader…)` / `toJson(obj, Writer)` | Leggere e scrivere file JSON | 02 |
| `@SerializedName("chiave_json")` | Chiave JSON diversa dal nome del campo Java | 02 |
| `transient` | Campo che **non** deve finire nel JSON | 02 |
| `TypeToken<List<X>>` | Leggere liste e mappe (tipi generici) | 02, 03 |
| `JsonParser`, `JsonObject`, `JsonArray`, `JsonElement` | Leggere/costruire JSON **senza** classi (tree model) | 02, 03 |
| `toJsonTree(obj)` | Da POJO ad albero modificabile | 02 |

---

## 👩‍🏫 Note per il docente

* Le **soluzioni** sono conservate esclusivamente nella cartella **Google Drive con permessi ristretti al docente** (non nel repository pubblico GitHub).
* I file di partenza degli esercizi **compilano ed eseguono già** così come sono: lo studente vede subito un output e completa i `TODO` passo dopo passo.
* Nell'esercizio 02 il modello dati contiene **"bug" voluti** (annotazioni mancanti): lo studente scopre da solo perché alcuni campi risultano `null`.
* Nell'esercizio 03 potete avviare la soluzione del **server sul PC docente** e far giocare tutta la classe con i propri client: è il momento più coinvolgente del percorso.
