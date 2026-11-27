/*
 * ============================================================================
 *  ESERCIZIO 01 (GUIDATO) - "La mia serie TV preferita"
 * ============================================================================
 *  E' una VARIANTE dell'esempio 00 (il brano di Spotify): stessa logica,
 *  argomento diverso. Leggi prima la traccia nel file README.md.
 *
 *  COME LAVORARE
 *   - Segui i PASSI in ordine (1 -> 8).
 *   - In ogni passo trovi un blocco di codice COMMENTATO con degli spazi
 *     ___ da riempire: togli i commenti (//) e completa.
 *   - Dopo ogni passo ESEGUI il programma e controlla l'output.
 *
 *  ESECUZIONE (dalla cartella 01-serie-tv):
 *      java -cp ../lib/gson-2.11.0.jar SerieTvApp.java
 *
 *  Il programma compila e gira anche cosi' com'e' (non fa ancora nulla).
 * ============================================================================
 */
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class SerieTvApp {

    public static void main(String[] args) {

        System.out.println("Esercizio 01 - La mia serie TV preferita\n");

        // ====================================================================
        // PASSO 1 -> NON e' qui: completa prima la classe SerieTv in fondo al file!
        // ====================================================================

        // ====================================================================
        // PASSO 2 - Crea un oggetto SerieTv con la TUA serie preferita e stampalo
        // ====================================================================
        // SerieTv mia = new SerieTv("___", "___", ___, ___, ___, ___);
        // System.out.println("PASSO 2 - Oggetto Java:");
        // System.out.println("  " + mia);

        // ====================================================================
        // PASSO 3 - SERIALIZZAZIONE compatta (oggetto -> JSON)
        // ====================================================================
        // Suggerimento: serve un oggetto Gson e il suo metodo toJson(...)
        // Gson gson = new ___();
        // String jsonCompatto = gson.___(mia);
        // System.out.println("\nPASSO 3 - JSON compatto:");
        // System.out.println("  " + jsonCompatto);

        // ====================================================================
        // PASSO 4 - SERIALIZZAZIONE leggibile (pretty printing)
        // ====================================================================
        // Suggerimento: new GsonBuilder() . ___() . create()
        // Gson gsonBello = new GsonBuilder().___().create();
        // String jsonBello = gsonBello.toJson(mia);
        // System.out.println("\nPASSO 4 - JSON indentato:");
        // System.out.println(jsonBello);
        // Confronta le lunghezze delle due stringhe: quale "pesa" di piu' in rete?
        // System.out.println("  Lunghezza compatto = " + jsonCompatto.___()
        //         + ", indentato = " + jsonBello.___());

        // ====================================================================
        // PASSO 5 - DESERIALIZZAZIONE (JSON -> oggetto)
        // ====================================================================
        // Un amico ti manda su WhatsApp questo JSON con la serie che ti consiglia.
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
        // Trasformalo in un oggetto SerieTv e stampa una frase del tipo:
        //   Il tuo amico ti consiglia "Mare Fuori" su RaiPlay: 5 stagioni, voto 8.5/10
        // SerieTv consigliata = gson.fromJson(___, ___.class);
        // System.out.println("\nPASSO 5 - Serie consigliata dall'amico:");
        // System.out.println("  Il tuo amico ti consiglia \"" + consigliata.___() + "\" su ...");

        // ====================================================================
        // PASSO 6 - Usa i due oggetti con normale logica Java
        // ====================================================================
        // a) Con un if confronta i voti delle due serie e stampa quale "vince".
        // b) Stampa le ore stimate per vedere la serie consigliata
        //    (metodo oreStimate() della classe SerieTv).
        // ... scrivi qui il tuo codice ...

        // ====================================================================
        // PASSO 7 - Modifica e ri-serializza
        // ====================================================================
        // Hai finito Mare Fuori in un weekend: imposta completata = true e
        // voto = 9.5 con i setter, poi stampa di nuovo il JSON compatto.
        // Cosa e' cambiato nella stringa?
        // ... scrivi qui il tuo codice ...

        // ====================================================================
        // PASSO 8 - Esperimenti: cosa succede quando il JSON "non va"?
        // ====================================================================
        // 8a) Chiave con la maiuscola sbagliata: che valore ha il titolo?
        String chiaveSbagliata = "{\"Titolo\":\"One Piece\",\"piattaforma\":\"Netflix\"}";
        // SerieTv s1 = gson.fromJson(chiaveSbagliata, SerieTv.class);
        // System.out.println("8a) titolo letto = " + s1.getTitolo());

        // 8b) Tipo sbagliato: "stagioni" contiene una parola, non un numero.
        //     Racchiudi fromJson in un try/catch (JsonSyntaxException e)
        //     e stampa e.getMessage().
        String tipoSbagliato = "{\"titolo\":\"Arcane\",\"stagioni\":\"due\"}";
        // try {
        //     ___
        // } catch (___ e) {
        //     ___
        // }

        // 8c) JSON malformato: manca la } finale. Gestiscilo come in 8b.
        String malformato = "{\"titolo\":\"Squid Game\", \"stagioni\": 3";
        // ... scrivi qui il tuo codice ...
    }
}

/*
 * ============================================================================
 *  PASSO 1 - Completa la classe modello (POJO)
 * ============================================================================
 *  Il JSON che vogliamo ottenere/leggere ha questa forma:
 *
 *      {
 *        "titolo": "Stranger Things",     -> String
 *        "piattaforma": "Netflix",        -> String
 *        "stagioni": 5,                   -> int
 *        "annoUscita": 2016,              -> int
 *        "voto": 9.0,                     -> double
 *        "completata": true               -> boolean
 *      }
 *
 *  Ricorda le regole di Gson:
 *   - nome del campo Java == nome della chiave JSON (maiuscole comprese!)
 *   - serve un costruttore VUOTO (lo usa Gson)
 *   - i getter/setter servono a te, non a Gson
 * ============================================================================
 */
class SerieTv {

    // TODO 1.1 - Dichiara i 6 campi private (nomi IDENTICI alle chiavi JSON)


    // TODO 1.2 - Costruttore vuoto


    // TODO 1.3 - Costruttore con tutti e 6 i parametri, nell'ordine:
    //            titolo, piattaforma, stagioni, annoUscita, voto, completata


    // TODO 1.4 - Getter per tutti i campi (getTitolo, getPiattaforma, ...,
    //            isCompletata) e setter per voto e completata


    // TODO 1.5 - Metodo int oreStimate(): stagioni * 8 episodi * 50 minuti / 60
    //            Domanda: questo valore finira' nel JSON? Perche'?


    // TODO 1.6 - toString() che restituisca ad esempio:
    //   SerieTv[Stranger Things (2016) su Netflix, 5 stagioni, voto 9.0, completata]

}
