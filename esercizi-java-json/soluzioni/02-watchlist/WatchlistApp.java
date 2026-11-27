/*
 * ============================================================================
 *  SOLUZIONE ESERCIZIO 02 - "La mia Watchlist" (Gson: tutte le funzioni base)
 * ============================================================================
 *  Esecuzione (dalla cartella 02-watchlist, dove si trova watchlist.json):
 *      java -cp ../lib/gson-2.11.0.jar ../soluzioni/02-watchlist/WatchlistApp.java
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

    // Un Gson "normale" e uno per il JSON leggibile: si creano UNA volta sola
    static final Gson GSON = new Gson();
    static final Gson GSON_BELLO = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args) {

        // --------------------------------------------------------------------
        // PASSO 2 - Lettura da FILE
        // --------------------------------------------------------------------
        titolo("PASSO 2 - Lettura di " + FILE_INPUT);
        Watchlist wl = caricaWatchlist(FILE_INPUT);
        if (wl == null) {
            System.out.println("Impossibile proseguire senza watchlist.");
            return;
        }
        System.out.println("Watchlist di " + wl.getProprietario()
                + " (aggiornata il " + wl.getAggiornataIl() + "): "
                + wl.getSerie().size() + " serie");

        // Prova anche con un file che non esiste (gestione IOException)
        caricaWatchlist("non_esiste.json");

        // --------------------------------------------------------------------
        // PASSO 3 - Navigare oggetti annidati, liste, enum, null
        // --------------------------------------------------------------------
        titolo("PASSO 3 - Contenuto della watchlist");
        double sommaVoti = 0;
        int inCorso = 0;
        for (SerieTv s : wl.getSerie()) {
            String ep = (s.getUltimoEpisodio() == null) ? "-" : s.getUltimoEpisodio().toString();
            System.out.printf("  %-16s %-8s %-10s ultimo: %-7s generi: %s%n",
                    s.getTitolo(), s.getPiattaforma(), s.getStato(), ep,
                    String.join(", ", s.getGeneri()));
            if (s.getNote() != null) {
                System.out.println("      nota: " + s.getNote());
            }
            sommaVoti += s.getVoto();
            if (s.getStato() == Stato.IN_CORSO) inCorso++;
        }
        System.out.printf("  Voto medio: %.2f  |  Serie in corso: %d%n",
                sommaVoti / wl.getSerie().size(), inCorso);

        // --------------------------------------------------------------------
        // PASSO 4 - Modificare e SALVARE su file
        // --------------------------------------------------------------------
        titolo("PASSO 4 - Modifica e salvataggio");
        SerieTv nuova = new SerieTv("One Piece", "Netflix", 2, 8.0, Stato.DA_VEDERE,
                List.of("Avventura", "Fantasy"), null, null);
        wl.getSerie().add(nuova);

        SerieTv mare = wl.cerca("Mare Fuori");
        if (mare != null) {
            mare.getUltimoEpisodio().avanza();          // ho visto un altro episodio
            mare.setModificata(true);                   // campo transient: NON salvato
            System.out.println("  Mare Fuori ora e' a " + mare.getUltimoEpisodio());
        }
        wl.setAggiornataIl(LocalDate.now().toString());
        salvaWatchlist(wl, FILE_OUTPUT);

        // --------------------------------------------------------------------
        // PASSO 5 - I valori null: default vs serializeNulls()
        // --------------------------------------------------------------------
        titolo("PASSO 5 - null nel JSON");
        System.out.println("  Default        : " + GSON.toJson(nuova));
        Gson gsonConNull = new GsonBuilder().serializeNulls().create();
        System.out.println("  serializeNulls : " + gsonConNull.toJson(nuova));

        // --------------------------------------------------------------------
        // PASSO 6 - JSON che inizia con [ ] : liste e TypeToken
        // --------------------------------------------------------------------
        titolo("PASSO 6 - Array come radice (TypeToken)");
        String top3 = """
                [
                  {"titolo":"Squid Game","piattaforma":"Netflix","stagioni":3,"voto":8.7,"stato":"COMPLETATA","generi":["Thriller"]},
                  {"titolo":"The Last of Us","piattaforma":"Sky","stagioni":2,"voto":9.1,"stato":"IN_CORSO","generi":["Drammatico","Post-apocalittico"]},
                  {"titolo":"Arcane","piattaforma":"Netflix","stagioni":2,"voto":9.5,"stato":"COMPLETATA","generi":["Animazione"]}
                ]
                """;
        // SBAGLIATO: List.class non dice a Gson il tipo degli elementi
        List<?> grezza = GSON.fromJson(top3, List.class);
        System.out.println("  Con List.class gli elementi sono: " + grezza.get(0).getClass().getSimpleName());

        // GIUSTO: TypeToken "ricorda" il tipo generico completo List<SerieTv>
        Type tipoLista = new TypeToken<List<SerieTv>>() {}.getType();
        List<SerieTv> classifica = GSON.fromJson(top3, tipoLista);
        System.out.println("  Con TypeToken gli elementi sono: " + classifica.get(0).getClass().getSimpleName());
        for (int i = 0; i < classifica.size(); i++) {
            System.out.println("   " + (i + 1) + ". " + classifica.get(i).getTitolo()
                    + " (" + classifica.get(i).getVoto() + ")");
        }

        // --------------------------------------------------------------------
        // PASSO 7 - Map <-> oggetto JSON
        // --------------------------------------------------------------------
        titolo("PASSO 7 - Map");
        Map<String, Integer> perPiattaforma = new TreeMap<>();
        for (SerieTv s : wl.getSerie()) {
            perPiattaforma.merge(s.getPiattaforma(), 1, Integer::sum);
        }
        String jsonMappa = GSON.toJson(perPiattaforma);
        System.out.println("  Map -> JSON : " + jsonMappa);

        String abbonamenti = "{\"Netflix\":13.99,\"RaiPlay\":0.0,\"Sky\":14.99,\"Prime Video\":4.99}";
        Type tipoMappa = new TypeToken<Map<String, Double>>() {}.getType();
        Map<String, Double> costi = GSON.fromJson(abbonamenti, tipoMappa);
        double spesa = 0;
        for (String piattaforma : perPiattaforma.keySet()) {
            spesa += costi.getOrDefault(piattaforma, 0.0);
        }
        System.out.printf("  JSON -> Map : %s%n  Spesa mensile per la tua watchlist: %.2f euro%n", costi, spesa);

        // --------------------------------------------------------------------
        // PASSO 8 - Modello ad ALBERO: JsonParser, JsonObject, JsonArray
        // --------------------------------------------------------------------
        titolo("PASSO 8 - JSON senza classe (tree model)");
        // Risposta di una API esterna (simile a TMDB): non ci interessa tutto,
        // non vogliamo scrivere una classe per ogni campo.
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
        JsonObject radice = JsonParser.parseString(rispostaApi).getAsJsonObject();
        System.out.println("  Risultati totali: " + radice.get("total_results").getAsInt());
        JsonArray risultati = radice.getAsJsonArray("results");
        for (JsonElement el : risultati) {
            JsonObject serie = el.getAsJsonObject();
            String nome = serie.get("name").getAsString();
            // has(): la chiave potrebbe mancare!
            String voto = serie.has("vote_average")
                    ? String.valueOf(serie.get("vote_average").getAsDouble()) : "n.d.";
            String paese = serie.getAsJsonArray("origin_country").get(0).getAsString();
            System.out.printf("   - %-16s voto %-5s paese %s%n", nome, voto, paese);
        }

        // Costruire un JSON "a mano"
        JsonObject profilo = new JsonObject();
        profilo.addProperty("utente", wl.getProprietario());
        profilo.addProperty("serieTotali", wl.getSerie().size());
        profilo.addProperty("premium", true);
        JsonArray preferite = new JsonArray();
        preferite.add("Arcane");
        preferite.add("Stranger Things");
        profilo.add("preferite", preferite);
        System.out.println("  JSON costruito a mano: " + profilo);

        // Da POJO ad albero, poi aggiungo un campo che la classe non ha
        JsonObject arricchito = GSON.toJsonTree(nuova).getAsJsonObject();
        arricchito.addProperty("consigliataDa", "Luca");
        arricchito.remove("piattaforma");
        System.out.println("  POJO -> albero modificato: " + arricchito);

        // --------------------------------------------------------------------
        // PASSO 9 - Gestione degli errori
        // --------------------------------------------------------------------
        titolo("PASSO 9 - Errori");
        String[] daProvare = {
                "{\"titolo\":\"Dark\",\"voto\":\"altissimo\"}",          // tipo sbagliato
                "{\"titolo\":\"Dark\",\"stato\":\"ABBANDONATA\"}",       // enum sconosciuto
                "{\"titolo\":\"Dark\", \"voto\": 9.0",                   // manca }
        };
        for (String j : daProvare) {
            try {
                SerieTv s = GSON.fromJson(j, SerieTv.class);
                System.out.println("  OK   " + j + "  -> stato=" + s.getStato());
            } catch (JsonSyntaxException | NumberFormatException e) {
                // Attenzione: per i campi double Gson lancia direttamente
                // NumberFormatException (non incapsulata): meglio catturarle entrambe.
                System.out.println("  ERR  " + j + "  -> " + e.getClass().getSimpleName());
            }
        }
    }

    // ========================================================================
    //  Metodi di supporto
    // ========================================================================

    /** Legge il file e lo trasforma in Watchlist; null in caso di errore. */
    static Watchlist caricaWatchlist(String nomeFile) {
        // try-with-resources: il Reader viene chiuso in automatico
        try (Reader reader = Files.newBufferedReader(Path.of(nomeFile), StandardCharsets.UTF_8)) {
            return GSON.fromJson(reader, Watchlist.class);   // fromJson accetta anche un Reader
        } catch (IOException e) {
            System.out.println("  [errore I/O] file non leggibile: " + e.getMessage());
        } catch (JsonSyntaxException | NumberFormatException e) {
            System.out.println("  [errore JSON] contenuto non valido: " + e.getMessage());
        }
        return null;
    }

    /** Salva la watchlist su file in formato leggibile. */
    static void salvaWatchlist(Watchlist wl, String nomeFile) {
        try (Writer writer = Files.newBufferedWriter(Path.of(nomeFile), StandardCharsets.UTF_8)) {
            GSON_BELLO.toJson(wl, writer);                   // toJson puo' scrivere su un Writer
            System.out.println("  Salvato in " + nomeFile + " (aprilo e controllalo!)");
        } catch (IOException e) {
            System.out.println("  [errore I/O] salvataggio fallito: " + e.getMessage());
        }
    }

    static void titolo(String t) {
        System.out.println("\n=== " + t + " ===");
    }
}

// ============================================================================
//  PASSO 1 - Modello dati
// ============================================================================

/** Un enum viene scritto/letto nel JSON come stringa: "IN_CORSO" */
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

    public void avanza() { episodio++; }

    public int getStagione() { return stagione; }
    public int getEpisodio() { return episodio; }

    @Override
    public String toString() { return "S" + stagione + "E" + episodio; }
}

class SerieTv {
    private String titolo;
    private String piattaforma;
    private int stagioni;
    private double voto;
    private Stato stato;                       // enum
    private List<String> generi = new ArrayList<>();   // array JSON di stringhe

    @SerializedName("ultimo_episodio")         // chiave JSON diversa dal nome Java
    private Episodio ultimoEpisodio;           // oggetto annidato (puo' essere null)

    private String note;                       // puo' essere null o mancare

    private transient boolean modificata;      // transient: Gson lo IGNORA

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

    @SerializedName("aggiornata_il")
    private String aggiornataIl;               // data come stringa ISO "AAAA-MM-GG"

    private List<SerieTv> serie = new ArrayList<>();

    public SerieTv cerca(String titolo) {
        for (SerieTv s : serie) {
            if (s.getTitolo().equalsIgnoreCase(titolo)) return s;
        }
        return null;
    }

    public String getProprietario()      { return proprietario; }
    public String getAggiornataIl()      { return aggiornataIl; }
    public List<SerieTv> getSerie()      { return serie; }
    public void setAggiornataIl(String d) { this.aggiornataIl = d; }
}
