package piece;

import utils.Move;
import utils.Pair;
import utils.constant.MoveType;

import java.awt.*;
import java.util.ArrayList;

/**
 * Classe che rappresenta il Re.
 * Il Re può muoversi di una casella in qualsiasi direzione (orizzontale, verticale o diagonale)
 * e ha la possibilità di effettuare l'arrocco, se soddisfa le condizioni necessarie.
 */
public class King extends ChessButton {

    /**
     * Costruttore del Re.
     * @param piece           Coppia di coordinate che rappresenta il re, tipo - colore
     * @param backgroundColor Colore dello sfondo del pulsante associato al re
     * @param row             Riga del re sulla scacchiera
     * @param col             Colonna del re sulla scacchiera
     */
    public King(Pair piece, Color backgroundColor, int row, int col) {
        super(piece, backgroundColor, 0, row, col);
    }

    public King(ChessButton first) {
        super(first);
    }

    /**
     * Restituisce tutte le mosse teoricamente possibili del Re,
     * comprese le mosse normali e le possibilità di arrocco.
     * @return Lista delle mosse teoriche disponibili per il Re
     */
    public ArrayList<Move> getAllPossibleMoves(){
        ArrayList<Move> res = new ArrayList<>();

        // Movimenti standard del Re (una casella in ogni direzione)
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue; // Esclude la posizione attuale
                int newRow = row + i;
                int newCol = col + j;
                if (isValidPosition(newRow, newCol)) {
                    res.add(new Move(newRow, newCol, MoveType.NORMAL));
                }
            }
        }

        // Possibilità di arrocco se il Re non si è mai mosso
        // System.out.println("il re si é giá mosso? "+ this.isAlreadyMoved);
        if (!this.isAlreadyMoved) {
            res.add(new Move(row, col - 2, MoveType.LONG_CASTLE));
            res.add(new Move(row, col + 2, MoveType.SHORT_CASTLE));
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
     * Il Re si muove sempre di una sola casella, quindi non ha passi intermedi.
     * @param end Posizione di arrivo del Re
     * @return Lista vuota, poiché il Re non ha passi intermedi nelle sue mosse
     */
    @Override
    public ArrayList<Move> getStepForThisMove(Move end) {
        return new ArrayList<>();
    }

    /**
     * Restituisce le caselle attraversate dal Re durante l'arrocco.
     * @param isLong True se si tratta dell'arrocco lungo, False per l'arrocco corto
     * @return Lista delle mosse intermedie necessarie per l'arrocco
     */
    public ArrayList<Move> getStepForThisMoveCastle(boolean isLong) {
        ArrayList<Move> res = new ArrayList<>();
        int direction = isLong ? -1 : 1;
        int steps = isLong ? 3 : 2;

        for (int i = 1; i <= steps; i++) {
            res.add(new Move(row, col + (i * direction), MoveType.MOVEMENT));
        }

        return res;
    }
}