/**
 * La classe Voce rappresenta una voce di un dizionario e contiene due parole una in italiano e una
 * in inglese. Inoltre contiene l'indice della voce nel dizionario per poterla gestire con ISAM.
 * @author Andrea
 * @version 1.0
 */
public class Voce {

    private final String italiano;
    private final String inglese;

    /**
     * Il delimitatore utilizzato da Form1 per separare le due parole e l'indice
     */
    public static final String DELIMITATORE = " ; ";

    /**
     * Costruttore parametrico
     * @param italiano parola in italiano
     * @param inglese parola in inglese
     */
    public Voce(String italiano, String inglese) {
        this.italiano = italiano;
        this.inglese = inglese;
    }

    public String getItaliano() {
        return italiano;
    }

    public String getInglese() {
        return inglese;
    }

    /**
     * Restituisce una stringa che viene utilizzata per salvare la voce su file
     * @return stringa che rappresenta l'oggetto
     */
    @Override
    public String toString() {
        return italiano + DELIMITATORE + inglese;
    }
}
