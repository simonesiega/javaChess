package piece;

import utils.Move;
import utils.Pair;
import utils.constant.ChessColor;
import utils.constant.MoveType;

import java.awt.*;
import java.util.ArrayList;

/**
 * Classe che rappresenta un Pedone nel gioco degli scacchi.
 * Il pedone può muoversi di una casella in avanti, di due se non è stato ancora mosso,
 * e può catturare in diagonale.
 */
public class Pawn extends ChessButton {

    private final int rowPromote;
    private int whenFirstTwoStep = -1;

    /**
     * Costruttore del pedone.
     * @param piece             Coppia di coordinate che rappresenta il pedone, tipo e colore
     * @param backgroundColor   Colore dello sfondo del pulsante associato al pedone
     * @param value             Valore del pedone
     * @param row               Riga del pedone sulla scacchiera
     * @param col               Colonna del pedone sulla scacchiera
     */
    public Pawn(Pair piece, Color backgroundColor, int value, int row, int col) {
        super(piece, backgroundColor, value, row, col);
        this.rowPromote = calculateRowPromote();
    }

    /**
     * Calcola la riga in cui il pedone dovrà essere promosso (0 per il bianco, 7 per il nero).
     * @return La riga di promozione del pedone
     */
    private int calculateRowPromote() {
        return (this.piece.getChessColor().equals(ChessColor.WHITE)) ? 0 : 7;
    }

    /**
     * Costruttore che crea una copia di un altro pedone.
     * @param first Il pedone da copiare
     */
    public Pawn(ChessButton first) {
        super(first);
        this.rowPromote = calculateRowPromote();
    }

    /**
     * Verifica se il pedone è arrivato alla riga di promozione.
     * @return True se il pedone è sulla riga di promozione, altrimenti False
     */
    public boolean isOnPromote() {
        if (this.piece.getChessColor().equals(ChessColor.WHITE))
            return this.row - 1 == this.rowPromote;
        else
            return this.row + 1 == this.rowPromote;
    }

    /**
     * Imposta il valore del movimento iniziale di due caselle per il pedone.
     * @param whenFirstTwoStep La riga in cui il pedone ha mosso per la prima volta due caselle
     */
    public void setWhenFirstTwoStep(int whenFirstTwoStep) {
        if (!this.isAlreadyMoved)
            this.whenFirstTwoStep = whenFirstTwoStep;
    }

    /**
     * Restituisce la riga in cui il pedone ha mosso per la prima volta due caselle.
     * @return La riga in cui il pedone ha mosso per la prima volta due caselle
     */
    public int getWhenFirstTwoStep() {
        return whenFirstTwoStep;
    }

    /**
     * Restituisce tutte le mosse teoricamente possibili del pedone,
     * ignorando la presenza di altri pezzi sulla scacchiera.
     * @return Lista delle mosse teoriche disponibili
     */
    @Override
    public ArrayList<Move> getAllPossibleMoves() {
        ArrayList<Move> res = new ArrayList<>();

        int color = (Integer) piece.getChessColor();
        int step = (color == ChessColor.BLACK) ? 1 : -1;

        if (color == ChessColor.BLANK) {
            // System.out.println("Errore: Colore non valido");
            return res;
        }

        // Mossa di uno in avanti
        if (isValidPosition(row + step, col)) {
            res.add(new Move(row + step, col, MoveType.MOVEMENT));
        }

        // Mossa di due caselle in avanti (solo se non è stato mosso)
        if (!isAlreadyMoved && isValidPosition(row + step, col) && isValidPosition(row + 2 * step, col)) {
            res.add(new Move(row + 2 * step, col, MoveType.MOVEMENT));
        }

        // Cattura a sinistra
        if (isValidPosition(row + step, col - 1)) {
            res.add(new Move(row + step, col - 1, MoveType.CAPTURE));
        }

        // Cattura a destra
        if (isValidPosition(row + step, col + 1)) {
            res.add(new Move(row + step, col + 1, MoveType.CAPTURE));
        }

        return res;
    }

    /**
     * Verifica se una posizione è valida all'interno della scacchiera.
     * @param i Riga da controllare
     * @param j Colonna da controllare
     * @return True se la posizione è all'interno della scacchiera (8x8), altrimenti False
     */
    private boolean isValidPosition(int i, int j) {
        return i >= 0 && i < 8 && j >= 0 && j < 8;
    }

    /**
     * Restituisce i passi intermedi necessari per effettuare una mossa dalla posizione di partenza a quella di arrivo.
     * @param end Posizione di arrivo del Pedone
     * @return Lista di mosse intermedie tra il punto di partenza e quello di arrivo
     */
    @Override
    public ArrayList<Move> getStepForThisMove(Move end) {
        ArrayList<Move> res = new ArrayList<>();

        if (!isAlreadyMoved && Math.abs(row - end.getRow()) == 2 && (col - end.getCol()) == 0) {
            int color = (Integer) piece.getChessColor();
            int step = (color == ChessColor.BLACK) ? 1 : -1;

            res.add(new Move(row + step, col, MoveType.MOVEMENT));
        }

        return res;
    }
}
