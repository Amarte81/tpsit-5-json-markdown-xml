/*
 * ============================================================================
 *  SOLUZIONE ESERCIZIO 01 - "La mia serie TV preferita"
 * ============================================================================
 *  Esecuzione (dalla cartella soluzioni/01-serie-tv):
 *      java -cp ../../lib/gson-2.11.0.jar SerieTvApp.java
 * ============================================================================
 */
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class SerieTvApp {

    public static void main(String[] args) {

        // PASSO 2 - Creo la MIA serie preferita
        SerieTv mia = new SerieTv("Stranger Things", "Netflix", 5, 2016, 9.0, true);
        System.out.println("PASSO 2 - Oggetto Java:");
        System.out.println("  " + mia);

        // PASSO 3 - Serializzazione compatta
        Gson gson = new Gson();
        String jsonCompatto = gson.toJson(mia);
        System.out.println("\nPASSO 3 - JSON compatto:");
        System.out.println("  " + jsonCompatto);

        // PASSO 4 - Serializzazione leggibile
        Gson gsonBello = new GsonBuilder().setPrettyPrinting().create();
        String jsonBello = gsonBello.toJson(mia);
        System.out.println("\nPASSO 4 - JSON indentato:");
        System.out.println(jsonBello);
        System.out.println("  Lunghezza compatto = " + jsonCompatto.length()
                + " caratteri, indentato = " + jsonBello.length() + " caratteri");

        // PASSO 5 - Deserializzazione del consiglio dell'amico
        String consiglioAmico = """
                {
                  "titolo": "Mare Fuori",
                  "piattaforma": "RaiPlay",
                  "stagioni": 5,
                  "annoUscita": 2020,
                  "voto": 8.5,
                  "completata": false
                }
                """;
        SerieTv consigliata = gson.fromJson(consiglioAmico, SerieTv.class);
        System.out.println("\nPASSO 5 - Serie consigliata dall'amico:");
        System.out.println("  " + consigliata);
        System.out.println("  Il tuo amico ti consiglia \"" + consigliata.getTitolo()
                + "\" su " + consigliata.getPiattaforma() + ": "
                + consigliata.getStagioni() + " stagioni, voto " + consigliata.getVoto() + "/10");

        // PASSO 6 - Confronto tra i due oggetti (logica Java "normale")
        System.out.println("\nPASSO 6 - Confronto:");
        if (mia.getVoto() > consigliata.getVoto()) {
            System.out.println("  La tua serie ha un voto piu' alto: resti fedele a " + mia.getTitolo());
        } else if (mia.getVoto() < consigliata.getVoto()) {
            System.out.println("  Il tuo amico vince: inizia subito " + consigliata.getTitolo());
        } else {
            System.out.println("  Pareggio! Guardale entrambe.");
        }
        System.out.println("  Ore stimate per " + consigliata.getTitolo() + ": "
                + consigliata.oreStimate() + " h");

        // PASSO 7 - Modifico e ri-serializzo
        consigliata.setCompletata(true);
        consigliata.setVoto(9.5);
        System.out.println("\nPASSO 7 - Dopo averla vista tutta:");
        System.out.println("  " + gson.toJson(consigliata));

        // PASSO 8 - Esperimenti sugli errori
        System.out.println("\nPASSO 8 - Esperimenti:");

        // 8a) chiave scritta male: "Titolo" invece di "titolo" -> campo null
        String chiaveSbagliata = "{\"Titolo\":\"One Piece\",\"piattaforma\":\"Netflix\"}";
        SerieTv s1 = gson.fromJson(chiaveSbagliata, SerieTv.class);
        System.out.println("  8a) titolo letto = " + s1.getTitolo()
                + "  (le chiavi sono case-sensitive!)");

        // 8b) tipo sbagliato: "stagioni" e' una stringa non numerica -> eccezione
        String tipoSbagliato = "{\"titolo\":\"Arcane\",\"stagioni\":\"due\"}";
        try {
            gson.fromJson(tipoSbagliato, SerieTv.class);
            System.out.println("  8b) nessun errore?!");
        } catch (JsonSyntaxException e) {
            System.out.println("  8b) JsonSyntaxException: " + e.getMessage());
        }

        // 8c) JSON malformato (manca la parentesi graffa finale)
        String malformato = "{\"titolo\":\"Squid Game\", \"stagioni\": 3";
        try {
            gson.fromJson(malformato, SerieTv.class);
            System.out.println("  8c) nessun errore?!");
        } catch (JsonSyntaxException e) {
            System.out.println("  8c) JsonSyntaxException: JSON malformato -> " + e.getMessage());
        }
    }
}

/*
 * PASSO 1 - Classe modello (POJO)
 */
class SerieTv {

    private String titolo;
    private String piattaforma;
    private int stagioni;
    private int annoUscita;
    private double voto;
    private boolean completata;

    public SerieTv() {
    }

    public SerieTv(String titolo, String piattaforma, int stagioni,
                   int annoUscita, double voto, boolean completata) {
        this.titolo = titolo;
        this.piattaforma = piattaforma;
        this.stagioni = stagioni;
        this.annoUscita = annoUscita;
        this.voto = voto;
        this.completata = completata;
    }

    /** Stima: 8 episodi a stagione da 50 minuti. Metodo, quindi NON va nel JSON. */
    public int oreStimate() {
        return stagioni * 8 * 50 / 60;
    }

    public String getTitolo()       { return titolo; }
    public String getPiattaforma()  { return piattaforma; }
    public int getStagioni()        { return stagioni; }
    public int getAnnoUscita()      { return annoUscita; }
    public double getVoto()         { return voto; }
    public boolean isCompletata()   { return completata; }

    public void setVoto(double voto)               { this.voto = voto; }
    public void setCompletata(boolean completata)  { this.completata = completata; }

    @Override
    public String toString() {
        return "SerieTv[" + titolo + " (" + annoUscita + ") su " + piattaforma
                + ", " + stagioni + " stagioni, voto " + voto
                + (completata ? ", completata" : ", in corso") + "]";
    }
}
