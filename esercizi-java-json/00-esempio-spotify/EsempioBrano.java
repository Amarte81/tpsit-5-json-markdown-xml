/*
 * ============================================================================
 *  ESEMPIO COMMENTATO 00 - "Il brano del momento" (stile Spotify)
 * ============================================================================
 *
 *  OBIETTIVO
 *  Vedere in UN SOLO programma il ciclo di vita completo di un dato JSON:
 *
 *      oggetto Java  --( serializzazione   : gson.toJson   )-->  stringa JSON
 *      stringa JSON  --( deserializzazione : gson.fromJson )-->  oggetto Java
 *
 *  Quando apri Spotify sul telefono, l'app chiede al server "dammi i dati di
 *  questo brano" e il server risponde con un TESTO in formato JSON. L'app lo
 *  trasforma in un oggetto e ti mostra titolo, artista, durata...
 *  Qui facciamo esattamente la stessa cosa, in piccolo.
 *
 *  LIBRERIA USATA
 *  Gson (di Google): file ../lib/gson-2.11.0.jar
 *  Java NON sa leggere il JSON "da solo": serve una libreria esterna.
 *
 *  COME ESEGUIRLO (Java 17 o superiore), dalla cartella 00-esempio-spotify:
 *
 *      java -cp ../lib/gson-2.11.0.jar EsempioBrano.java
 *
 *  (Java 11+ sa eseguire direttamente un file .java senza javac: la PRIMA
 *   classe del file deve essere quella con il main.)
 * ============================================================================
 */

// Le uniche due classi di Gson che ci servono in questo esempio
import com.google.gson.Gson;          // il "traduttore" Java <-> JSON
import com.google.gson.GsonBuilder;   // la "fabbrica" per creare un Gson configurato

public class EsempioBrano {

    public static void main(String[] args) {

        // --------------------------------------------------------------------
        // PASSO 1 - Creo un normale oggetto Java
        // --------------------------------------------------------------------
        // Niente di speciale: e' una classe come quelle viste in terza/quarta.
        // La classe Brano e' definita in fondo a questo file.
        Brano brano = new Brano("Tuta gold", "Mahmood", 188, 2024, false, 91.5);

        System.out.println("1) Oggetto Java (stampato con toString):");
        System.out.println("   " + brano);

        // --------------------------------------------------------------------
        // PASSO 2 - Creo l'oggetto Gson
        // --------------------------------------------------------------------
        // Gson e' il "traduttore". Se ne crea UNO e lo si riusa quante volte si
        // vuole: e' thread-safe e non conserva stato tra una chiamata e l'altra.
        Gson gson = new Gson();

        // --------------------------------------------------------------------
        // PASSO 3 - SERIALIZZAZIONE: oggetto Java -> stringa JSON
        // --------------------------------------------------------------------
        // Gson guarda (con la "reflection") i CAMPI della classe Brano e per
        // ognuno crea una coppia  "nomeCampo": valore
        //   String  -> "testo tra virgolette"
        //   int/double -> numero
        //   boolean -> true / false
        String json = gson.toJson(brano);

        System.out.println("\n2) JSON compatto (e' quello che viaggia in rete):");
        System.out.println("   " + json);
        // NB: e' una semplice String! Puo' essere inviata su un socket,
        // salvata in un file, mandata via HTTP...

        // --------------------------------------------------------------------
        // PASSO 4 - JSON "bello" (pretty printing) con GsonBuilder
        // --------------------------------------------------------------------
        // Il JSON compatto e' ottimo per le macchine (meno byte) ma scomodo per
        // noi. GsonBuilder permette di configurare il Gson prima di crearlo.
        // Schema: new GsonBuilder() . opzione() . opzione() . create()
        Gson gsonLeggibile = new GsonBuilder()
                .setPrettyPrinting()   // a capo + indentazione di 2 spazi
                .create();

        System.out.println("\n3) JSON indentato (leggibile per gli umani):");
        System.out.println(gsonLeggibile.toJson(brano));

        // --------------------------------------------------------------------
        // PASSO 5 - DESERIALIZZAZIONE: stringa JSON -> oggetto Java
        // --------------------------------------------------------------------
        // Immaginiamo che questa stringa sia arrivata dal server di Spotify.
        // Uso un "text block" (Java 15+): le triple virgolette permettono di
        // scrivere una stringa su piu' righe senza \n e senza \" .
        String jsonRicevuto = """
                {
                  "titolo": "30\u00b0C",
                  "artista": "ANNA",
                  "durataSecondi": 147,
                  "annoUscita": 2024,
                  "esplicito": true,
                  "popolarita": 89.0
                }
                """;

        // fromJson vuole DUE cose:
        //   1) la stringa JSON da leggere
        //   2) la CLASSE in cui trasformarla (Brano.class)
        // Gson crea un Brano vuoto e riempie ogni campo che ha lo STESSO NOME
        // di una chiave del JSON (attenzione: maiuscole/minuscole contano!).
        Brano ricevuto = gson.fromJson(jsonRicevuto, Brano.class);

        System.out.println("\n4) Oggetto ricostruito dal JSON ricevuto:");
        System.out.println("   " + ricevuto);

        // --------------------------------------------------------------------
        // PASSO 6 - Ora e' un oggetto Java a tutti gli effetti
        // --------------------------------------------------------------------
        // Posso usare getter, metodi, if... il JSON "non c'e' piu'".
        System.out.println("\n5) Uso i dati come normali variabili Java:");
        System.out.println("   Durata formattata : " + ricevuto.getDurataFormattata());
        System.out.println("   Contenuto esplicito? " + (ricevuto.isEsplicito() ? "si' [E]" : "no"));
        if (ricevuto.getPopolarita() > brano.getPopolarita()) {
            System.out.println("   " + ricevuto.getTitolo() + " e' piu' popolare di " + brano.getTitolo());
        } else {
            System.out.println("   " + brano.getTitolo() + " e' piu' popolare di " + ricevuto.getTitolo());
        }

        // --------------------------------------------------------------------
        // PASSO 7 - E se il JSON non corrisponde perfettamente alla classe?
        // --------------------------------------------------------------------
        // Caso reale: le API cambiano, mandano campi in piu' o ne omettono.
        //  - chiave in PIU' nel JSON ("genere")    -> Gson la IGNORA
        //  - chiave MANCANTE (es. "durataSecondi") -> il campo resta al valore
        //    di default di Java: 0 per i numeri, false per boolean, null per
        //    gli oggetti (String compresa).
        String jsonIncompleto =
                "{\"titolo\":\"Espresso\",\"artista\":\"Sabrina Carpenter\",\"genere\":\"pop\"}";

        Brano parziale = gson.fromJson(jsonIncompleto, Brano.class);

        System.out.println("\n6) JSON incompleto / con campi extra:");
        System.out.println("   " + parziale);
        System.out.println("   -> 'genere' ignorato, durata=0 e anno=0 perche' mancanti");

        // --------------------------------------------------------------------
        // RIASSUNTO
        // --------------------------------------------------------------------
        //  new Gson()                         crea il traduttore
        //  new GsonBuilder()...create()       crea un traduttore configurato
        //  gson.toJson(oggetto)               Java  -> JSON (String)
        //  gson.fromJson(stringa, X.class)    JSON  -> Java (oggetto di tipo X)
    }
}

/*
 * ============================================================================
 *  CLASSE MODELLO (POJO = Plain Old Java Object)
 * ============================================================================
 *  Regole per farla "andare d'accordo" con Gson:
 *   1. I NOMI dei campi diventano le CHIAVI del JSON (case-sensitive).
 *   2. I campi possono essere private: Gson li legge comunque.
 *   3. Conviene avere un costruttore SENZA parametri: Gson lo usa per creare
 *      l'oggetto vuoto prima di riempirlo durante fromJson.
 *   4. Getter/setter NON servono a Gson, servono a NOI per usare l'oggetto.
 * ============================================================================
 */
class Brano {

    private String titolo;         // JSON: stringa  -> "titolo": "Tuta gold"
    private String artista;        // JSON: stringa  -> "artista": "Mahmood"
    private int durataSecondi;     // JSON: numero   -> "durataSecondi": 188
    private int annoUscita;        // JSON: numero   -> "annoUscita": 2024
    private boolean esplicito;     // JSON: booleano -> "esplicito": false
    private double popolarita;     // JSON: numero   -> "popolarita": 91.5 (0-100)

    // Costruttore vuoto: usato da Gson in fromJson
    public Brano() {
    }

    // Costruttore completo: usato da noi per creare oggetti "a mano"
    public Brano(String titolo, String artista, int durataSecondi,
                 int annoUscita, boolean esplicito, double popolarita) {
        this.titolo = titolo;
        this.artista = artista;
        this.durataSecondi = durataSecondi;
        this.annoUscita = annoUscita;
        this.esplicito = esplicito;
        this.popolarita = popolarita;
    }

    // Metodo "di comodo": NON e' un campo, quindi NON finisce nel JSON
    public String getDurataFormattata() {
        return String.format("%d:%02d", durataSecondi / 60, durataSecondi % 60);
    }

    public String getTitolo()      { return titolo; }
    public String getArtista()     { return artista; }
    public int getDurataSecondi()  { return durataSecondi; }
    public int getAnnoUscita()     { return annoUscita; }
    public boolean isEsplicito()   { return esplicito; }
    public double getPopolarita()  { return popolarita; }

    @Override
    public String toString() {
        return "Brano[" + titolo + " - " + artista + ", " + getDurataFormattata()
                + ", " + annoUscita + ", esplicito=" + esplicito
                + ", popolarita=" + popolarita + "]";
    }
}
