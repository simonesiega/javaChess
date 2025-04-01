package piece;

import utils.Move;
import utils.Pair;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Classe astratta che rappresenta un pezzo degli scacchi come pulsante grafico.
 * Ogni pezzo ha:
 * - una coppia colore/tipo che lo identifica,
 * - una posizione,
 * - un colore di sfondo,
 * - un valore associato,
 * - un'informazione sullo stato di movimento.
 */
public abstract class ChessButton extends JButton {
    protected final Pair piece;
    protected final Color backgroundColor;
    protected int row;
    protected int col;
    protected int value;
    protected boolean isAlreadyMoved = false;

    /**
     * Costruttore principale.
     * @param piece           Coppia di valori che rappresenta il tipo e colore del pezzo.
     * @param backgroundColor Colore di sfondo del pulsante associato al pezzo.
     * @param value           Valore del pezzo per il calcolo strategico.
     * @param row             Riga in cui si trova il pezzo sulla scacchiera.
     * @param col             Colonna in cui si trova il pezzo sulla scacchiera.
     */
    protected ChessButton(Pair piece, Color backgroundColor, int value, int row, int col) {
        this.piece = piece;
        this.backgroundColor = backgroundColor;
        this.value = value;
        this.row = row;
        this.col = col;
        setBackground(backgroundColor);
    }

    /**
     * Costruttore di copia.
     * @param button Altro oggetto ChessButton da copiare.
     */
    protected ChessButton(ChessButton button) {
        this.piece = new Pair(button.getPiece());
        this.backgroundColor = button.getBackgroundColor();
        this.value = button.value;
        this.row = button.getRow();
        this.col = button.getCol();
        this.isAlreadyMoved = button.isAlreadyMoved;
    }

    /**
     * Restituisce il valore del pezzo.
     * @return Valore strategico del pezzo.
     */
    public int getValue() {
        return value;
    }

    /**
     * Restituisce il tipo e colore del pezzo.
     * @return Coppia che rappresenta il pezzo.
     */
    public Pair getPiece() {
        return piece;
    }

    /**
     * Restituisce il colore di sfondo del pulsante.
     * @return Colore di sfondo.
     */
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Restituisce la riga del pezzo sulla scacchiera.
     * @return Indice di riga.
     */
    public int getRow() {
        return row;
    }

    /**
     * Restituisce la colonna del pezzo sulla scacchiera.
     * @return Indice di colonna.
     */
    public int getCol() {
        return col;
    }

    /**
     * Verifica se il pezzo si è già mosso.
     * @return True se il pezzo si è già mosso almeno una volta, altrimenti false.
     */
    public boolean isAlreadyMoved() {
        return isAlreadyMoved;
    }

    /**
     * Imposta lo stato del pezzo come già mosso.
     */
    public void setAlreadyMoved() {
        isAlreadyMoved = true;
    }

    /**
     * Aggiorna la posizione del pezzo sulla scacchiera.
     *
     * @param row Nuova riga.
     * @param col Nuova colonna.
     */
    public void updatePosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Restituisce la posizione del pezzo sulla scacchiera in formato numerico.
     * @return Indice univoco della posizione sulla scacchiera (da 0 a 63).
     */
    public int position() {
        return this.row * 8 + this.col;
    }


    public String printPosition() {
        char columnLetter = (char) ('a' + this.col);
        int rowNumber = 8 - this.row;
        return columnLetter + "" + rowNumber;
    }

    /**
     * Sposta il pezzo nella posizione di un altro ChessButton.
     * @param chessButton ChessButton che rappresenta la nuova posizione.
     */
    public void move(ChessButton chessButton) {
        this.row = chessButton.row;
        this.col = chessButton.col;
    }

    @Override
    public String toString() {
        return String.format("ChessButton{piece=%s, row=%d, col=%d}", piece, row, col);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessButton that = (ChessButton) o;
        return row == that.row && col == that.col && Objects.equals(piece, that.piece);
    }

    @Override
    public int hashCode() {
        return Objects.hash(piece, row, col);
    }

    /**
     * Restituisce tutte le mosse teoricamente possibili per il pezzo,
     * indipendentemente dagli altri pezzi sulla scacchiera.
     *
     * @return Lista di mosse disponibili.
     */
    public abstract ArrayList<Move> getAllPossibleMoves();

    /**
     * Restituisce i passi intermedi necessari per effettuare una mossa dalla posizione di partenza a quella di arrivo.
     *
     * @param end Posizione di arrivo del pezzo.
     * @return Lista di mosse intermedie tra il punto di partenza e quello di arrivo.
     */
    public abstract ArrayList<Move> getStepForThisMove(Move end);
}
