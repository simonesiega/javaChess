package utils.constant;

import utils.Pair;

import java.util.HashMap;

import static utils.constant.ChessPath.*;
import static utils.constant.ChessUnicode.*;

public class ChessImg {
    public static final HashMap<Pair, String> pieceToImg = new HashMap<>() {{
        put(new Pair(ChessType.TOWER, ChessColor.WHITE), WHITE_TOWER_IMG);
        put(new Pair(ChessType.TOWER,ChessColor.BLACK), BLACK_TOWER_IMG);

        put(new Pair(ChessType.BISHOP, ChessColor.WHITE), WHITE_BISHOP_IMG);
        put(new Pair(ChessType.BISHOP,ChessColor.BLACK), BLACK_BISHOP_IMG);

        //piece.Knight
        put(new Pair(ChessType.KNIGHT,ChessColor.WHITE), WHITE_KNIGHT_IMG);
        put(new Pair(ChessType.KNIGHT,ChessColor.BLACK), BLACK_KNIGHT_IMG);

        //piece.Queen
        put(new Pair(ChessType.QUEEN,ChessColor.WHITE), WHITE_QUEEN_IMG);
        put(new Pair(ChessType.QUEEN,ChessColor.BLACK), BLACK_QUEEN_IMG);

        //piece.King
        put(new Pair(ChessType.KING,ChessColor.WHITE), WHITE_KING_IMG);
        put(new Pair(ChessType.KING,ChessColor.BLACK), BLACK_KING_IMG);

        //piece.Pawn
        put(new Pair(ChessType.PAWN,ChessColor.WHITE), WHITE_PAWN_IMG);
        put(new Pair(ChessType.PAWN,ChessColor.BLACK), BLACK_PAWN_IMG);
    }};

    public static final HashMap<Pair, Character> pieceToUnicode = new HashMap<>() {{
        put(new Pair(ChessType.TOWER, ChessColor.WHITE), WHITE_TOWER_UNICODE);
        put(new Pair(ChessType.TOWER,ChessColor.BLACK), BLACK_TOWER_UNICODE);

        put(new Pair(ChessType.BISHOP, ChessColor.WHITE), WHITE_BISHOP_UNICODE);
        put(new Pair(ChessType.BISHOP,ChessColor.BLACK), BLACK_BISHOP_UNICODE);

        //piece.Knight
        put(new Pair(ChessType.KNIGHT,ChessColor.WHITE), WHITE_KNIGHT_UNICODE);
        put(new Pair(ChessType.KNIGHT,ChessColor.BLACK), BLACK_KNIGHT_UNICODE);

        //piece.Queen
        put(new Pair(ChessType.QUEEN,ChessColor.WHITE), WHITE_QUEEN_UNICODE);
        put(new Pair(ChessType.QUEEN,ChessColor.BLACK), BLACK_QUEEN_UNICODE);

        //piece.King
        put(new Pair(ChessType.KING,ChessColor.WHITE), WHITE_KING_UNICODE);
        put(new Pair(ChessType.KING,ChessColor.BLACK), BLACK_KING_UNICODE);

        //piece.Pawn
        put(new Pair(ChessType.PAWN,ChessColor.WHITE), WHITE_PAWN_UNICODE);
        put(new Pair(ChessType.PAWN,ChessColor.BLACK), BLACK_PAWN_UNICODE);
    }};

    public static final HashMap<Integer, String> moveTypeToAudio = new HashMap<>() {{
        put(MoveType.CAPTURE, CAPTURE_AUDIO);
        put(MoveType.SHORT_CASTLE, CASTLE_AUDIO);
        put(MoveType.LONG_CASTLE, CASTLE_AUDIO);
        put(MoveType.MOVEMENT, MOVE_AUDIO);
        put(MoveType.PROMOTE, MOVE_AUDIO);
        put(MoveType.NORMAL, MOVE_AUDIO);
        put(MoveType.START_GAME, START_GAME_AUDIO);
        put(MoveType.CHECK, CHECK_AUDIO);
        put(MoveType.CHECKMATE, CHECKMATE_AUDIO);
        put(MoveType.ENPASSANT, CAPTURE_AUDIO);
    }};
}
