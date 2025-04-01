package piece;

import utils.Move;
import utils.Pair;
import utils.constant.MoveType;
import java.awt.*;
import java.util.ArrayList;

/**
 * Classe che rappresenta la Torre.
 * La Torre si muove in linee rette (orizzontale e verticale) senza limiti di distanza,
 * finché non incontra un ostacolo.
 */
public class Tower extends ChessButton {

    /**
     * Costruttore della Torre.
     * @param piece           Coppia di coordinate che rappresenta torre, tipo - colore
     * @param backgroundColor Colore dello sfondo del pulsante associato alla torre
     * @param row             Riga della torre sulla scacchiera
     * @param col             Colonna della torre sulla scacchiera
     */
    public Tower(Pair piece, Color backgroundColor, int value, int row, int col) {
        super(piece, backgroundColor, value, row, col);
    }

    public Tower(ChessButton first) {
        super(first);
    }

    /**
     * Restituisce tutte le mosse teoricamente possibili della Torre,
     * ignorando la presenza di altri pezzi sulla scacchiera.
     * @return Lista delle mosse teoriche disponibili
     */
    @Override
    public ArrayList<Move> getAllPossibleMoves() {
        ArrayList<Move> res = new ArrayList<>();

        // Direzioni della Torre: sinistra, sopra, destra, sotto
        int[][] directions = {{0, -1}, {-1, 0}, {0, 1}, {1, 0}};

        // Itera su tutte le direzioni e aggiunge le mosse possibili
        for (int[] dir : directions) {
            addMoves(row, col, dir[0], dir[1], res);
        }

        return res;
    }

    /**
     * Aggiunge le mosse teoriche lungo una direzione specifica finché non si esce dalla scacchiera.
     * @param i     Riga di partenza
     * @param j     Colonna di partenza
     * @param step_i Passo nella direzione della riga (-1 su, +1 giù, 0 invariato)
     * @param step_j Passo nella direzione della colonna (-1 sinistra, +1 destra, 0 invariato)
     * @param res   Lista in cui aggiungere le mosse valide
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
     * @param end   Posizione di arrivo della Torre
     * @return Lista di mosse intermedie tra il punto di partenza e quello di arrivo
     */
    @Override
    public ArrayList<Move> getStepForThisMove( Move end) {
        ArrayList<Move> res = new ArrayList<>();

        // Se la mossa è orizzontale
        if (row == end.getRow()) {
            int step_col = (col > end.getCol()) ? -1 : 1;
            addSteps(row, col, 0, step_col, end.getCol(), res);
        }
        // Se la mossa è verticale
        else if (col == end.getCol()) {
            int step_row = (row > end.getRow()) ? -1 : 1;
            addSteps(row, col, step_row, 0, end.getRow(), res);
        }

        return res;
    }

    /**
     * Aggiunge i passi intermedi tra una posizione di partenza e una posizione di arrivo lungo una direzione valida.
     * @param i      Riga di partenza
     * @param j      Colonna di partenza
     * @param step_i Passo nella direzione della riga (-1 su, +1 giù, 0 invariato)
     * @param step_j Passo nella direzione della colonna (-1 sinistra, +1 destra, 0 invariato)
     * @param end    Valore della riga o della colonna di arrivo
     * @param res    Lista in cui aggiungere i passi intermedi
     */
    private void addSteps(int i, int j, int step_i, int step_j, int end, ArrayList<Move> res) {
        i += step_i;
        j += step_j;

        // Aggiunge tutte le posizioni intermedie fino a raggiungere la destinazione
        while ((step_i != 0 && i != end) || (step_j != 0 && j != end)) {
            res.add(new Move(i, j, MoveType.NORMAL));
            i += step_i;
            j += step_j;
        }
    }
}
