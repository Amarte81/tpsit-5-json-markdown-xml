/*
 * ============================================================================
 *  ESERCIZIO 02 (GUIDATO) - "La mia Watchlist"
 *  Evoluzione dell'esercizio 01: tutte le funzionalita' essenziali di Gson
 * ============================================================================
 *  Leggi prima la traccia nel file README.md di questa cartella.
 *
 *  COME LAVORARE
 *   - Passi in ordine (1 -> 9). Dopo ogni passo ESEGUI e controlla l'output.
 *   - I blocchi commentati con ___ vanno scommentati e completati.
 *   - Il file compila e gira anche cosi' com'e'.
 *
 *  ESECUZIONE (dalla cartella 02-watchlist, dove c'e' watchlist.json):
 *      java -cp ../lib/gson-2.11.0.jar WatchlistApp.java
 * ============================================================================
 */
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class WatchlistApp {

    static final String FILE_INPUT  = "watchlist.json";
    static final String FILE_OUTPUT = "watchlist_aggiornata.json";

    static final Gson GSON = new Gson();
    // TODO 0 - crea un secondo Gson con il pretty printing (vedi esercizio 01)
    static final Gson GSON_BELLO = new Gson();

    public static void main(String[] args) {

        // ====================================================================
        // PASSO 1 -> e' in fondo al file: controlla il MODELLO DATI
        // ====================================================================

        // ====================================================================
        // PASSO 2 - Lettura da FILE  (completa prima il metodo caricaWatchlist)
        // ====================================================================
        titolo("PASSO 2 - Lettura di " + FILE_INPUT);
        Watchlist wl = caricaWatchlist(FILE_INPUT);
        if (wl == null) {
            System.out.println("Impossibile proseguire senza watchlist: completa caricaWatchlist()!");
            return;
        }
        System.out.println("Watchlist di " + wl.getProprietario()
                + " (aggiornata il " + wl.getAggiornataIl() + "): "
                + wl.getSerie().size() + " serie");
        // Domanda: se "aggiornata il" stampa null, cosa manca nel modello? (PASSO 1)

        // Prova con un file inesistente: deve stampare un errore, NON crashare
        caricaWatchlist("non_esiste.json");

        // ====================================================================
        // PASSO 3 - Navigare oggetti annidati, liste, enum e valori null
        // ====================================================================
        titolo("PASSO 3 - Contenuto della watchlist");
        // Per ogni serie stampa una riga tipo:
        //   Mare Fuori  RaiPlay  IN_CORSO  ultimo: S4E6  generi: Drammatico, Crime
        // e, SOLO se presente, la nota su una seconda riga.
        // Alla fine stampa il voto medio e quante serie sono IN_CORSO.
        //
        // Suggerimenti:
        //  - ultimoEpisodio puo' essere null -> stampa "-"
        //  - String.join(", ", lista) unisce una List<String>
        //  - gli enum si confrontano con ==  (s.getStato() == Stato.IN_CORSO)
        //
        // for (SerieTv s : wl.getSerie()) {
        //     ___
        // }

        // ====================================================================
        // PASSO 4 - Modificare e SALVARE su file (completa salvaWatchlist)
        // ====================================================================
        titolo("PASSO 4 - Modifica e salvataggio");
        // a) Aggiungi alla lista la serie "One Piece" (Netflix, 2 stagioni,
        //    voto 8.0, DA_VEDERE, generi Avventura e Fantasy, nessun episodio, nessuna nota)
        // SerieTv nuova = new SerieTv("One Piece", "Netflix", 2, 8.0, Stato.___,
        //         List.of("___", "___"), ___, ___);
        // wl.getSerie().add(nuova);
        //
        // b) Hai visto un altro episodio di Mare Fuori: usa wl.cerca(...) e
        //    getUltimoEpisodio().avanza(); poi setModificata(true).
        //
        // c) Aggiorna la data: wl.setAggiornataIl(LocalDate.now().toString());
        //
        // d) salvaWatchlist(wl, FILE_OUTPUT);  -> apri il file generato:
        //    - il campo "modificata" c'e'? Perche'?
        //    - la chiave e' "ultimo_episodio" o "ultimoEpisodio"?

        // ====================================================================
        // PASSO 5 - I valori null: comportamento di default vs serializeNulls()
        // ====================================================================
        titolo("PASSO 5 - null nel JSON");
        // Stampa "nuova" con GSON e poi con un Gson creato cosi':
        //   new GsonBuilder().___().create()
        // Che differenza vedi per "ultimo_episodio" e "note"?

        // ====================================================================
        // PASSO 6 - JSON che inizia con [ ]  ->  List e TypeToken
        // ====================================================================
        titolo("PASSO 6 - Array come radice (TypeToken)");
        String top3 = """
                [
                  {"titolo":"Squid Game","piattaforma":"Netflix","stagioni":3,"voto":8.7,"stato":"COMPLETATA","generi":["Thriller"]},
                  {"titolo":"The Last of Us","piattaforma":"Sky","stagioni":2,"voto":9.1,"stato":"IN_CORSO","generi":["Drammatico","Post-apocalittico"]},
                  {"titolo":"Arcane","piattaforma":"Netflix","stagioni":2,"voto":9.5,"stato":"COMPLETATA","generi":["Animazione"]}
                ]
                """;
        // a) Prova prima cosi' e stampa la classe del primo elemento: e' un SerieTv?
        // List<?> grezza = GSON.fromJson(top3, List.class);
        // System.out.println(grezza.get(0).getClass().getSimpleName());
        //
        // b) Ora nel modo corretto, con TypeToken:
        // Type tipoLista = new TypeToken<List<___>>() {}.getType();
        // List<SerieTv> classifica = GSON.fromJson(top3, tipoLista);
        // Stampa la classifica numerata: "1. Squid Game (8.7)" ...

        // ====================================================================
        // PASSO 7 - Map <-> oggetto JSON
        // ====================================================================
        titolo("PASSO 7 - Map");
        // a) Conta quante serie hai per piattaforma in una Map<String,Integer>
        //    (suggerimento: mappa.merge(chiave, 1, Integer::sum)) e stampala
        //    come JSON con GSON.toJson(mappa). Che forma ha il JSON?
        //
        // b) Leggi questo JSON in una Map<String, Double> (serve un TypeToken!)
        String abbonamenti = "{\"Netflix\":13.99,\"RaiPlay\":0.0,\"Sky\":14.99,\"Prime Video\":4.99}";
        //    e calcola quanto spendi al mese per le piattaforme della tua watchlist.

        // ====================================================================
        // PASSO 8 - Modello ad ALBERO: JsonParser, JsonObject, JsonArray
        // ====================================================================
        titolo("PASSO 8 - JSON senza classe (tree model)");
        // Risposta di una API esterna (simile a TMDB, il database di film e serie).
        // NON scrivere una classe: naviga il JSON come un albero.
        String rispostaApi = """
                {
                  "page": 1,
                  "total_results": 3,
                  "results": [
                    {"id": 66732, "name": "Stranger Things", "vote_average": 8.6,
                     "origin_country": ["US"], "first_air_date": "2016-07-15"},
                    {"id": 94605, "name": "Arcane", "vote_average": 8.8,
                     "origin_country": ["US", "FR"]},
                    {"id": 99999, "name": "Mare Fuori", "origin_country": ["IT"]}
                  ]
                }
                """;
        // JsonObject radice = JsonParser.parseString(rispostaApi).___();
        // System.out.println("Risultati totali: " + radice.get("total_results").___());
        // JsonArray risultati = radice.getAsJsonArray("___");
        // for (JsonElement el : risultati) {
        //     JsonObject serie = el.getAsJsonObject();
        //     // stampa name, vote_average (se manca -> "n.d.", usa serie.has(...))
        //     // e il primo paese di origin_country
        // }
        //
        // b) Costruisci "a mano" questo JSON e stampalo:
        //    {"utente":"Giulia","serieTotali":5,"premium":true,"preferite":["Arcane","Stranger Things"]}
        //    (JsonObject.addProperty, new JsonArray(), JsonArray.add, JsonObject.add)
        //
        // c) Trasforma "nuova" in albero con GSON.toJsonTree(nuova).getAsJsonObject(),
        //    aggiungi la proprieta' "consigliataDa": "Luca", rimuovi "piattaforma"
        //    e stampalo.

        // ====================================================================
        // PASSO 9 - Gestione degli errori
        // ====================================================================
        titolo("PASSO 9 - Errori");
        String[] daProvare = {
                "{\"titolo\":\"Dark\",\"voto\":\"altissimo\"}",          // tipo sbagliato
                "{\"titolo\":\"Dark\",\"stato\":\"ABBANDONATA\"}",       // enum sconosciuto
                "{\"titolo\":\"Dark\", \"voto\": 9.0",                   // manca }
        };
        // Per ognuno prova fromJson dentro un try/catch e stampa OK o ERR + tipo
        // di eccezione (e.getClass().getSimpleName()).
        // Attenzione: per i double Gson puo' lanciare NumberFormatException:
        //   catch (JsonSyntaxException | NumberFormatException e)
        // Cosa succede con l'enum sconosciuto? E' un errore o no?
    }

    // ========================================================================
    //  Metodi di supporto da completare
    // ========================================================================

    /** Legge il file e lo trasforma in Watchlist; restituisce null in caso di errore. */
    static Watchlist caricaWatchlist(String nomeFile) {
        // TODO 2 - Apri il file con try-with-resources e passa il Reader a fromJson:
        //
        // try (Reader reader = Files.newBufferedReader(Path.of(nomeFile), StandardCharsets.UTF_8)) {
        //     return GSON.fromJson(___, ___.class);
        // } catch (IOException e) {
        //     System.out.println("  [errore I/O] file non leggibile: " + e.getMessage());
        // } catch (JsonSyntaxException | NumberFormatException e) {
        //     System.out.println("  [errore JSON] contenuto non valido: " + e.getMessage());
        // }
        return null;
    }

    /** Salva la watchlist su file in formato leggibile. */
    static void salvaWatchlist(Watchlist wl, String nomeFile) {
        // TODO 4 - Come sopra ma con Files.newBufferedWriter(...) e
        //          GSON_BELLO.toJson(oggetto, writer)
    }

    static void titolo(String t) {
        System.out.println("\n=== " + t + " ===");
    }
}

// ============================================================================
//  PASSO 1 - Modello dati
// ============================================================================
//  Confronta le classi con il file watchlist.json e completa i TODO.
//  Il programma compila anche senza, ma alcuni dati verranno letti come null:
//  e' proprio quello che devi scoprire e correggere!
// ============================================================================

/** Un enum nel JSON diventa una stringa: "IN_CORSO" */
enum Stato { DA_VEDERE, IN_CORSO, COMPLETATA }

/** Oggetto annidato:  "ultimo_episodio": { "stagione": 4, "episodio": 6 } */
class Episodio {
    private int stagione;
    private int episodio;

    public Episodio() { }

    public Episodio(int stagione, int episodio) {
        this.stagione = stagione;
        this.episodio = episodio;
    }

    // TODO 1.1 - incrementa il numero di episodio
    public void avanza() { }

    public int getStagione() { return stagione; }
    public int getEpisodio() { return episodio; }

    // TODO 1.2 - deve restituire ad esempio "S4E6"
    @Override
    public String toString() { return "?"; }
}

class SerieTv {
    private String titolo;
    private String piattaforma;
    private int stagioni;
    private double voto;
    private Stato stato;
    private List<String> generi = new ArrayList<>();

    // TODO 1.3 - nel JSON la chiave si chiama "ultimo_episodio" (snake_case),
    //            in Java vogliamo ultimoEpisodio (camelCase).
    //            Aggiungi l'annotazione giusta sopra al campo:  @SerializedName("___")
    private Episodio ultimoEpisodio;

    private String note;

    // TODO 1.4 - questo campo serve solo al programma e NON deve finire nel file:
    //            aggiungi la parola chiave giusta (transient)
    private boolean modificata;

    public SerieTv() { }

    public SerieTv(String titolo, String piattaforma, int stagioni, double voto, Stato stato,
                   List<String> generi, Episodio ultimoEpisodio, String note) {
        this.titolo = titolo;
        this.piattaforma = piattaforma;
        this.stagioni = stagioni;
        this.voto = voto;
        this.stato = stato;
        this.generi = new ArrayList<>(generi);
        this.ultimoEpisodio = ultimoEpisodio;
        this.note = note;
    }

    public String getTitolo()            { return titolo; }
    public String getPiattaforma()       { return piattaforma; }
    public int getStagioni()             { return stagioni; }
    public double getVoto()              { return voto; }
    public Stato getStato()              { return stato; }
    public List<String> getGeneri()      { return generi; }
    public Episodio getUltimoEpisodio()  { return ultimoEpisodio; }
    public String getNote()              { return note; }
    public boolean isModificata()        { return modificata; }

    public void setStato(Stato stato)              { this.stato = stato; }
    public void setModificata(boolean modificata)  { this.modificata = modificata; }
}

class Watchlist {
    private String proprietario;

    // TODO 1.5 - nel JSON: "aggiornata_il"
    private String aggiornataIl;

    private List<SerieTv> serie = new ArrayList<>();

    // TODO 1.6 - restituisce la serie con quel titolo (ignorando maiuscole), o null
    public SerieTv cerca(String titolo) {
        return null;
    }

    public String getProprietario()       { return proprietario; }
    public String getAggiornataIl()       { return aggiornataIl; }
    public List<SerieTv> getSerie()       { return serie; }
    public void setAggiornataIl(String d) { this.aggiornataIl = d; }
}
