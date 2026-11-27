# ✏️ 01 — Esercizio guidato: "La mia serie TV preferita"

> **Variante dell'esempio 00**: stessa logica del brano di Spotify, ma ora il codice lo scrivi tu.

## 🎯 Obiettivi
* Scrivere una **classe modello (POJO)** compatibile con Gson.
* Usare le classi essenziali: `Gson`, `GsonBuilder`, `JsonSyntaxException`.
* Fare **serializzazione** (`toJson`) e **deserializzazione** (`fromJson`).
* Capire cosa succede quando il JSON **non corrisponde** alla classe.

## 📝 Scenario
Vuoi condividere con gli amici la tua serie preferita in un formato che qualsiasi app possa leggere: il JSON. Un amico ti risponde su WhatsApp con un JSON della serie che consiglia lui.

Il JSON di una serie ha questa forma:

```json
{
  "titolo": "Stranger Things",
  "piattaforma": "Netflix",
  "stagioni": 5,
  "annoUscita": 2016,
  "voto": 9.0,
  "completata": true
}
```

## ▶️ Esecuzione

```bash
cd 01-serie-tv
java -cp ../lib/gson-2.11.0.jar SerieTvApp.java
```

Il file [`SerieTvApp.java`](./SerieTvApp.java) **compila già**: dopo ogni passo togli i `//` dal blocco di codice, riempi gli spazi `___` e riesegui.

## 🪜 Passi

| Passo | Cosa fare | Classi / metodi |
|:-:|:--|:--|
| **1** | Completa la classe `SerieTv` in fondo al file: 6 campi, costruttore vuoto, costruttore completo, getter, 2 setter, `oreStimate()`, `toString()` | POJO |
| **2** | Crea un oggetto con la **tua** serie preferita e stampalo | `new SerieTv(...)` |
| **3** | Trasformalo in JSON compatto | `new Gson()`, `toJson()` |
| **4** | Stampalo indentato e confronta le lunghezze | `GsonBuilder`, `setPrettyPrinting()` |
| **5** | Leggi il JSON dell'amico e stampa *"Il tuo amico ti consiglia…"* | `fromJson(json, SerieTv.class)` |
| **6** | Confronta i voti con un `if` e stampa le ore stimate | logica Java normale |
| **7** | Modifica la serie dell'amico (vista tutta, voto 9.5) e ri-serializza | setter + `toJson()` |
| **8** | Esperimenti: chiave con maiuscola sbagliata, tipo sbagliato, JSON malformato | `try` / `catch (JsonSyntaxException e)` |

### ✅ Output atteso (parziale)

```text
PASSO 3 - JSON compatto:
  {"titolo":"Stranger Things","piattaforma":"Netflix","stagioni":5,"annoUscita":2016,"voto":9.0,"completata":true}
...
PASSO 8 - Esperimenti:
  8a) titolo letto = null  (le chiavi sono case-sensitive!)
  8b) JsonSyntaxException: java.lang.NumberFormatException: For input string: "due"
  8c) JsonSyntaxException: JSON malformato -> java.io.EOFException: End of input ...
```

## ❓ Domande di verifica
1. Il metodo `oreStimate()` compare nel JSON? Perché?
2. Cosa cambia nel JSON se rinomini il campo `annoUscita` in `anno`? E cosa succede quando rileggi il JSON vecchio?
3. Perché nel passo 8a non c'è un errore ma il titolo vale `null`?
4. Che differenza c'è tra l'errore del passo 8b e quello del passo 8c?

## 🚀 Sfida extra (facoltativa)
Chiedi al compagno di banco il JSON della **sua** serie preferita (copialo da chat o da un file di testo), incollalo nel programma e stampa chi dei due ha la serie con il voto più alto.

➡️ Quando hai finito passa all'[esercizio 02](../02-watchlist/).
