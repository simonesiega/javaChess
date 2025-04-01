package piece;

import utils.Move;
import utils.Pair;
import utils.constant.MoveType;
import java.awt.*;
import java.util.ArrayList;

/**
 * Classe che rappresenta la Regina.
 * La Regina combina i movimenti della Torre e dell'Alfiere, quindi può muoversi sia in diagonale
 * sia in linea retta in tutte le direzioni, senza limiti di distanza.
 */
public class Queen extends ChessButton {

    /**
     * Costruttore della Regina.
     * @param piece           Coppia di coordinate che rappresenta la regina, tipo - colore
     * @param backgroundColor Colore dello sfondo del pulsante associato alla regina
     * @param row             Riga della regina sulla scacchiera
     * @param col             Colonna della regina sulla scacchiera
     */
    public Queen(Pair piece, Color backgroundColor, int value, int row, int col) {
        super(piece, backgroundColor, value, row, col);
    }

    public Queen(ChessButton first) {
        super(first);
    }

    /**
     * Restituisce tutte le mosse teoricamente possibili della Regina,
     * ignorando la presenza di altri pezzi sulla scacchiera.
     * @return Lista delle mosse teoriche disponibili
     */
    @Override
    public ArrayList<Move> getAllPossibleMoves() {
        ArrayList<Move> res = new ArrayList<>();

        // Direzioni della Regina (combinazione di Torre e Alfiere)
        int[][] directions = {
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1},  // Movimenti diagonali (Alfiere)
                {0, -1}, {-1, 0}, {0, 1}, {1, 0}     // Movimenti rettilinei (Torre)
        };

        // Itera su tutte le direzioni e aggiunge le mosse possibili
        for (int[] dir : directions) {
            addMoves(row, col, dir[0], dir[1], res);
        }

        return res;
    }

    /**
     * Aggiunge le mosse teoriche lungo una direzione specifica finché non si esce dalla scacchiera.
     * @param i      Riga di partenza
     * @param j      Colonna di partenza
     * @param step_i Passo nella direzione della riga
     * @param step_j Passo nella direzione della colonna
     * @param res    Lista in cui aggiungere le mosse valide
     */
    private void addMoves(int i, int j, int step_i, int step_j, ArrayList<Move> res) {
        i += step_i;
        j += step_j;

        // Continua ad aggiungere mosse finché rimane dentro i limiti della scacchiera
        while (isValidPosition(i, j)) {
            res.add(new Move(i, j, MoveType.NORMAL));
            i += step_i;
            j += step_j;
        }
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
     * @param end   Posizione di arrivo della Regina
     * @return Lista di mosse intermedie tra il punto di partenza e quello di arrivo
     */
    @Override
    public ArrayList<Move> getStepForThisMove(Move end) {
        ArrayList<Move> res = new ArrayList<>();

        int step_row = Integer.compare(end.getRow(), row); // -1, 0, 1
        int step_col = Integer.compare(end.getCol(), col); // -1, 0, 1

        // Se si muove in linea retta o in diagonale
        if (step_row == 0 || step_col == 0 || Math.abs(row - end.getRow()) == Math.abs(col - end.getCol())) {
            addSteps(row, col, step_row, step_col, end.getRow(), end.getCol(), res);
        }

        return res;
    }

    /**
     * Aggiunge i passi intermedi tra una posizione di partenza e una posizione di arrivo lungo una direzione valida.
     * @param i      Riga di partenza
     * @param j      Colonna di partenza
     * @param step_i Passo nella direzione della riga
     * @param step_j Passo nella direzione della colonna
     * @param end_r  Riga di arrivo
     * @param end_c  Colonna di arrivo
     * @param res    Lista in cui aggiungere i passi intermedi
     */
    private void addSteps(int i, int j, int step_i, int step_j, int end_r, int end_c, ArrayList<Move> res) {
        i += step_i;
        j += step_j;

        // Aggiunge tutte le posizioni intermedie fino a raggiungere la destinazione
        while (i != end_r || j != end_c) {
            res.add(new Move(i, j, MoveType.NORMAL));
            i += step_i;
            j += step_j;
        }
    }
}
