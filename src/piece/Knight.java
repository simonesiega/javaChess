package piece;

import utils.Move;
import utils.Pair;
import utils.constant.MoveType;

import java.awt.*;
import java.util.ArrayList;

/**
 * Classe che rappresenta il Cavallo.
 * Il Cavallo si muove a "L" (due caselle in una direzione e una perpendicolare),
 * e può saltare sopra altri pezzi.
 */
public class Knight extends ChessButton {

    /**
     * Costruttore del Cavallo.
     * @param piece           Coppia di coordinate che rappresenta il cavallo, tipo - colore
     * @param backgroundColor Colore dello sfondo del pulsante associato al cavallo
     * @param row             Riga del cavallo sulla scacchiera
     * @param col             Colonna del cavallo sulla scacchiera
     */
    public Knight(Pair piece, Color backgroundColor, int value, int row, int col) {
        super(piece, backgroundColor, value, row, col);
    }

    public Knight(ChessButton first) {
        super(first);
    }

    /**
     * Il Cavallo salta sopra gli ostacoli, quindi non ci sono passi intermedi tra le mosse.
     * @param end  Posizione di arrivo del Cavallo
     * @return Lista vuota, poiché il Cavallo non ha passi intermedi
     */
    @Override
    public ArrayList<Move> getStepForThisMove(Move end) {
        return new ArrayList<>();
    }

    /**
     * Restituisce tutte le mosse teoricamente possibili del Cavallo,
     * ignorando la presenza di altri pezzi sulla scacchiera.
     * @return Lista delle mosse teoriche disponibili
     */
    @Override
    public ArrayList<Move> getAllPossibleMoves() {
        ArrayList<Move> res = new ArrayList<>();

        // Tutti i possibili movimenti a "L" del Cavallo
        int[][] directions = {
                {-2, -1}, {-2, 1}, {2, -1}, {2, 1},
                {-1, -2}, {-1, 2}, {1, -2}, {1, 2}
        };

        // Aggiunge solo le mosse valide
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            if (isValidPosition(newRow, newCol)) {
                res.add(new Move(newRow, newCol, MoveType.NORMAL));
            }
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
}