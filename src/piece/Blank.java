package piece;

import utils.Move;
import utils.Pair;

import java.awt.*;
import java.util.ArrayList;

/**
 * Classe che rappresenta una casella vuota sulla scacchiera.
 * Un oggetto di questa classe non rappresenta un pezzo reale,
 * é utilizzato per gestire gli spazi vuoti nella UI e nella logica di gioco.
 */
public class Blank extends ChessButton {

    /**
     * Costruttore della casella vuota.
     * @param piece           Coppia di coordinate (tipo-colore)
     * @param backgroundColor Colore dello sfondo della casella
     * @param row             Riga della casella sulla scacchiera
     * @param col             Colonna della casella sulla scacchiera
     */
    public Blank(Pair piece, Color backgroundColor, int row, int col) {
        super(piece, backgroundColor, 0 ,row, col);
    }

    public Blank(ChessButton first) {
        super(first);
    }

    /**
     * Un pezzo vuoto non ha mosse disponibili, quindi restituisce `null`.
     * @return `null` perché un "pezzo vuoto" non può muoversi
     */
    @Override
    public ArrayList<Move> getAllPossibleMoves() {
        return null;
    }

    /**
     * Un pezzo vuoto non ha passi intermedi da calcolare, quindi restituisce `null`.
     * @param end   Posizione di arrivo
     * @return `null` perché un "pezzo vuoto" non si muove
     */
    @Override
    public ArrayList<Move> getStepForThisMove(Move end) {
        return null;
    }
}
