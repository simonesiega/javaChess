package utils;

import java.util.Objects;

/**
 * La classe Pair rappresenta una coppia di valori interi.
 * È utilizzata per rappresentare proprietà come il tipo di pezzo e il colore di un pezzo
 * all'interno del gioco degli scacchi.
 */
public class Pair {
    private final Integer a;
    private final Integer b;

    /**
     * Costruttore della classe Pair.
     * @param a Il primo valore della coppia, tipicamente il tipo di pezzo.
     * @param b Il secondo valore della coppia, tipicamente il colore del pezzo.
     */
    public Pair(Integer a , Integer b){
        this.a = a;
        this.b = b;
    }

    /**
     * Costruttore di copia per un oggetto Pair.
     * Crea una nuova coppia con i valori del Pair fornito.
     * @param piece Il Pair da copiare.
     */
    public Pair(Pair piece) {
        this.a = piece.getChessType();
        this.b = piece.getChessColor();
    }

    /**
     * Restituisce una rappresentazione in formato stringa della coppia.
     * La stringa contiene i valori di a e b.
     * @return Una stringa che rappresenta la coppia.
     */
    @Override
    public String toString() {
        return "utils.Pair{" +
                "a=" + a +
                ", b=" + b +
                '}';
    }

    /**
     * Restituisce il tipo di pezzo rappresentato dalla coppia.
     * @return Il tipo di pezzo (ad esempio, un intero che rappresenta un tipo di {@code ChessType}).
     */
    public Integer getChessType() {
        return a;
    }

    /**
     * Restituisce il colore del pezzo rappresentato dalla coppia.
     * @return Il colore del pezzo (ad esempio, un intero che rappresenta un colore {@code ChessColor}).
     */
    public Integer getChessColor() {
        return b;
    }

    /**
     * Confronta due oggetti Pair per verificare se sono uguali.
     * Due oggetti Pair sono uguali se entrambi i loro valori sono uguali.
     * @param obj L'oggetto da confrontare con la coppia.
     * @return true se le coppie sono uguali, altrimenti false.
     */
    @Override
    public boolean equals (Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Pair) obj;
        return Objects.equals(this.a, that.a) &&
                Objects.equals(this.b, that.b);
    }

    /**
     * Restituisce un valore hash per l'oggetto Pair.
     * Il valore hash è calcolato utilizzando i valori di a e b.
     * @return Il valore hash dell'oggetto.
     */
    @Override
    public int hashCode() {
        return Objects.hash(a, b);
    }
}
