package utils.constant;

import utils.Move;

public class ChessPosition {
    public static final Move DEFAULT_WHITE_KING = new Move(7,4, MoveType.NORMAL);
    public static final Move DEFAULT_BLACK_KING = new Move(0, 4, MoveType.NORMAL);
    public static final Move DEFAULT_WHITE_ROOK_1 = new Move(7,0, MoveType.NORMAL);
    public static final Move DEFAULT_WHITE_ROOK_2 = new Move(7,7, MoveType.NORMAL);
    public static final Move DEFAULT_BLACK_ROOK_1 = new Move(0,0, MoveType.NORMAL);
    public static final Move DEFAULT_BLACK_ROOK_2 = new Move(0,7, MoveType.NORMAL);
    public static final int DEFAULT_WHITE_ROW_PAWN = 6;
    public static final int DEFAULT_BLACK_ROW_PAWN = 1;

}
