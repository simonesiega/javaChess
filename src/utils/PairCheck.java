package utils;

import piece.*;
import utils.constant.ChessType;

import java.util.Objects;

/**
 * La classe PairCheck rappresenta una coppia di pezzi scacchistici, uno "attivo" e uno "passivo".
 * Viene utilizzata per gestire situazioni come i pezzi che pinnano altri pezzi.
 */
public class PairCheck {
    private final ChessButton active;
    private final ChessButton passive;

    /**
     * Costruttore della classe PairCheck.
     * @param a Il pezzo attivo (che sta pinnando).
     * @param b Il pezzo passivo (che viene pinnato).
     */
    public PairCheck(ChessButton a , ChessButton b){
        this.active = a;
        this.passive = b;
    }

    /**
     * Costruttore di copia per un oggetto PairCheck.
     * Crea una nuova coppia di pezzi con i pezzi dell'oggetto PairCheck fornito.
     *
     * @param pairCheck Il PairCheck da copiare.
     */
    public PairCheck(PairCheck pairCheck){
        this.active = createChessbutton(pairCheck.getActive());
        this.passive = createChessbutton(pairCheck.getPassive());
    }

    /**
     * Crea un nuovo oggetto {@code ChessButton} a partire da uno esistente,
     * copiando il tipo di pezzo e altre caratteristiche.
     * @param b Il pezzo da copiare.
     * @return Un nuovo oggetto {@code ChessButton} dello stesso tipo.
     */
    public ChessButton createChessbutton(ChessButton b){
        switch(b.getPiece().getChessType()){
            case ChessType.TOWER -> {
                return new Tower(b);
            }
            case ChessType.BISHOP -> {
                return new Bishop(b);
            }
            case ChessType.KNIGHT -> {
                return new Knight(b);
            }
            case ChessType.QUEEN -> {
                return new Queen(b);
            }
            case ChessType.KING -> {
                return new King(b);
            }
            case ChessType.PAWN -> {
                return new Pawn(b);
            }

            default -> {
                return new Blank(b);
            }
        }
    }

    /**
     * Restituisce una rappresentazione in formato stringa della coppia di pezzi.
     * La stringa contiene informazioni sui pezzi attivo e passivo.
     * @return Una stringa che rappresenta la coppia di pezzi.
     */
    @Override
    public String toString() {
        return "utils.Pair{" +
                "a=" + active +
                ", b=" + passive +
                '}';
    }

    /**
     * Restituisce il pezzo attivo (quello che sta pinnando).
     * @return Il pezzo attivo.
     */
    public ChessButton getActive() {
        return active;
    }

    /**
     * Restituisce il pezzo passivo (quello che è pinnato).
     * @return Il pezzo passivo.
     */
    public ChessButton getPassive() {
        return passive;
    }

    /**
     * Confronta due oggetti PairCheck per verificare se sono uguali.
     * Due oggetti PairCheck sono uguali se entrambi i loro pezzi attivo e passivo sono uguali.
     * @param obj L'oggetto da confrontare con PairCheck.
     * @return true se le coppie di pezzi sono uguali, altrimenti false.
     */
    @Override
    public boolean equals (Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (PairCheck) obj;
        return Objects.equals(this.active, that.active) &&
                Objects.equals(this.passive, that.passive);
    }

    /**
     * Restituisce un valore hash per l'oggetto PairCheck.
     * Il valore hash è calcolato utilizzando i pezzi attivo e passivo.
     * @return Il valore hash dell'oggetto.
     */
    @Override
    public int hashCode() {
        return Objects.hash(active, passive);
    }
}
