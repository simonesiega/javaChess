package utils.constant;

import utils.Move;

/**
 * Classe che definisce le posizioni iniziali dei pezzi sulla scacchiera.
 * Contiene costanti che rappresentano le mosse iniziali di alcuni pezzi (come il re e le torri) e la posizione dei pedoni.
 * Queste differenze sono dovute alle diverse mosse speciali che questi pezzi possono compiere.
 */
public class ChessPosition {
    /*
     * Posizione iniziale del re.
     * Nota: L'arrocco è un comportamento speciale che coinvolge questa posizione e può essere effettuato solo se
     * il re e la torre non sono stati mossi, e non ci sono pezzi tra di loro.
     */
    public static final Move DEFAULT_WHITE_KING = new Move(7,4, MoveType.NORMAL);
    public static final Move DEFAULT_BLACK_KING = new Move(0, 4, MoveType.NORMAL);

    /*
     * Posizione iniziale della torre.
     * Nota: Questa torre potrebbe essere coinvolta nell'arrocco, un movimento speciale che sposta il re e la torre contemporaneamente.
     */
    public static final Move DEFAULT_WHITE_ROOK_1 = new Move(7,0, MoveType.NORMAL);
    public static final Move DEFAULT_WHITE_ROOK_2 = new Move(7,7, MoveType.NORMAL);
    public static final Move DEFAULT_BLACK_ROOK_1 = new Move(0,0, MoveType.NORMAL);
    public static final Move DEFAULT_BLACK_ROOK_2 = new Move(0,7, MoveType.NORMAL);

    public static final int DEFAULT_WHITE_ROW_PAWN = 6;

    public static final int DEFAULT_BLACK_ROW_PAWN = 1;

}
