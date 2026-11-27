# 🔑 Soluzioni (per il docente)

Soluzioni complete e testate (Java 21, Gson 2.11.0). Ogni file riporta in testa il comando di esecuzione.

| Esercizio | File | Come eseguirla |
|:--|:--|:--|
| 01 — Serie TV | [`01-serie-tv/SerieTvApp.java`](./01-serie-tv/SerieTvApp.java) | da `soluzioni/01-serie-tv`: `java -cp ../../lib/gson-2.11.0.jar SerieTvApp.java` |
| 02 — Watchlist | [`02-watchlist/WatchlistApp.java`](./02-watchlist/WatchlistApp.java) | da `02-watchlist`: `java -cp ../lib/gson-2.11.0.jar ../soluzioni/02-watchlist/WatchlistApp.java` |
| 03 — QuizBattle server | [`03-quiz-socket/QuizServer.java`](./03-quiz-socket/QuizServer.java) | da `03-quiz-socket`: `java -cp ../lib/gson-2.11.0.jar ../soluzioni/03-quiz-socket/QuizServer.java` |
| 03 — QuizBattle client | [`03-quiz-socket/QuizClient.java`](./03-quiz-socket/QuizClient.java) | da `03-quiz-socket`: `java -cp ../lib/gson-2.11.0.jar ../soluzioni/03-quiz-socket/QuizClient.java [host]` |

## Risposte alle domande di verifica (sintesi)

**Esempio 00** — 1) `String`: si può inviare/salvare come testo. 2) Il compatto pesa meno byte. 3) No, le chiavi sconosciute sono ignorate. 4) `0`; per una `String` sarebbe `null`.

**Esercizio 01** — 1) No: Gson serializza i **campi**, non i metodi. 2) La chiave diventa `"anno"`; rileggendo il JSON vecchio il campo `anno` resta `0` perché `"annoUscita"` non corrisponde più. 3) `"Titolo"` ≠ `titolo`: la chiave viene ignorata, non è un errore di sintassi. 4) 8b: JSON valido ma tipo incompatibile (`NumberFormatException` incapsulata); 8c: JSON sintatticamente malformato (`EOFException`).

**Esercizio 02** — 1) Per la *type erasure* dei generics: a runtime `List.class` non sa che gli elementi sono `SerieTv`, quindi Gson crea `LinkedTreeMap`; `TypeToken` conserva il tipo completo. 2) `transient` non viene **mai** scritto né letto; un campo `null` esiste ma viene omesso solo di default (con `serializeNulls()` compare come `null`). 3) No: il campo diventa silenziosamente `null`, il rischio è un `NullPointerException` più avanti → validare sempre i dati ricevuti. 4) Il JSON è per standard (RFC 8259) in UTF-8; senza specificarlo Java potrebbe usare la codifica di sistema (es. Windows-1252) e rovinare accenti ed emoji.

**Esercizio 03** — 1) Il server legge con `readLine()`: un JSON indentato verrebbe spezzato su più righe. 2) Perché il tipo della classe da usare dipende dal campo `"tipo"`, che va letto prima. 3) Il client riceverebbe anche `"corretta"`: chiunque potrebbe barare leggendo il traffico. 4) Più thread (un giocatore ciascuno) la modificano contemporaneamente. 5) Sì, qualsiasi linguaggio con socket TCP e un parser JSON, rispettando la tabella del protocollo (chiavi, tipi, una riga per messaggio, UTF-8).
