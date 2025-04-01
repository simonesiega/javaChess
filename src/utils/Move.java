package utils;

import utils.constant.ChessType;
import utils.constant.MoveType;

import java.util.Objects;

import static utils.constant.ChessImg.pieceToUnicode;

/**
 * La classe Move rappresenta una mossa su una scacchiera.
 * Ogni mossa è definita dalla sua posizione sulla scacchiera (riga e colonna)
 * e dal tipo di mossa (normale, cattura, arrocco, etc.).
 *
 * Le coordinate della scacchiera sono espresse come indici (0-7) che corrispondono a una matrice 8x8,
 * mentre la notazione scacchistica (ad esempio "e2" per la colonna e la riga) viene gestita nei metodi.
 */
public class Move {
    /*
     * Tutte le utils.Move vanno da 0-7 quindi non seguono le mosse canoniche ma le coordinate della matrice
     * */

    private final int row;
    private final int col;
    private int moveType;

    /**
     * Costruttore della classe Move.
     *
     * @param row La riga della mossa sulla scacchiera (valore da 0 a 7).
     * @param col La colonna della mossa sulla scacchiera (valore da 0 a 7).
     * @param moveType Il tipo della mossa, rappresentato da un intero che corrisponde a una delle costanti di MoveType.
     */
    public Move(int row, int col, int moveType) {
        this.row = row;
        this.col = col;
        this.moveType = moveType;
    }

    /**
     * Costruttore di copia per un oggetto Move.
     *
     * @param king Un altro oggetto Move da copiare.
     */
    public Move(Move king) {
        this.row = king.getRow();
        this.col = king.getCol();
        this.moveType = king.getMoveType();
    }

    /**
     * Restituisce la riga della mossa.
     *
     * @return La riga della mossa.
     */
    public int getRow() {
        return row;
    }

    /**
     * Restituisce la colonna della mossa.
     *
     * @return La colonna della mossa.
     */
    public int getCol() {
        return col;
    }

    /**
     * Restituisce il tipo di mossa.
     *
     * @return Il tipo di mossa (ad esempio, normale, cattura, arrocco, etc.).
     */
    public int getMoveType() {
        return moveType;
    }

    /**
     * Imposta il tipo di mossa.
     *
     * @param moveType Il tipo di mossa da impostare.
     */
    public void setMoveType(int moveType){
        this.moveType = moveType;
    }

    /**
     * Restituisce una rappresentazione in formato stringa della mossa.
     *
     * @return La rappresentazione della mossa come stringa.
     */
    @Override
    public String toString() {
        return "utils.Move{" +
                "row=" + row +
                ", col=" + col +
                ", moveType=" + moveType +
                '}';
    }

    /**
     * Calcola la posizione unica della mossa sulla scacchiera come un singolo numero.
     * La posizione è rappresentata dalla somma della riga moltiplicata per 8 e della colonna.
     *
     * @return La posizione della mossa come numero intero.
     */
    public int position(){
        return this.row * 8 + this.col;
    }

    /**
     * Restituisce la posizione della mossa in notazione scacchistica standard.
     * Ad esempio, (0,0) -> "a8".
     * Gestisce anche le mosse speciali come l'arrocco e le catture.
     *
     * @param pair Le informazioni sul pezzo che sta effettuando la mossa.
     * @param colFirst La colonna iniziale del pezzo, utile per il pedone.
     * @return La posizione della mossa in notazione scacchistica.
     */
    public String printPosition(Pair pair, int colFirst) {

        StringBuilder res = new StringBuilder();

        if(this.moveType == MoveType.SHORT_CASTLE){
            return "0-0";
        }

        if(this.moveType == MoveType.LONG_CASTLE){
            return "0-0-0";
        }

        if(this.moveType == MoveType.CAPTURE || this.moveType == MoveType.ENPASSANT){

            if(pair.getChessType().equals(ChessType.PAWN)){
                res.append((char) ('a' + colFirst-1));
            }else
                res.append(pieceToUnicode.get(pair)).append(" ");

            res.append('x');
        } else {
            res.append(pieceToUnicode.get(pair)).append(" ");
        }

        char columnLetter = (char) ('a' + this.col);
        int rowNumber = 8 - this.row;
        res.append(columnLetter);
        res.append(rowNumber);

        return res.toString();
    }

    /**
     * Confronta due oggetti Move per verificare se sono uguali.
     * Due mosse sono uguali se hanno la stessa riga e colonna.
     *
     * @param o L'oggetto da confrontare.
     * @return {@code true} se le mosse sono uguali, altrimenti {@code false}.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return row == move.row && col == move.col;
    }

    /**
     * Restituisce un valore hash per l'oggetto Move.
     *
     * @return Il valore hash dell'oggetto.
     */
    @Override
    public int hashCode() {
        return Objects.hash(row, col, moveType);
    }
}
