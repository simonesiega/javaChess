package utils.constant;

import utils.Pair;

import java.util.HashMap;

import static utils.constant.ChessPath.*;
import static utils.constant.ChessUnicode.*;

/**
 * Classe che mappa i tipi di pezzi di scacchi e le azioni associate a immagini, Unicode e suoni.
 * Utilizza delle mappe per associare i pezzi agli oggetti immagine o Unicode, e i tipi di mossa agli effetti sonori.
 */
public class ChessImg {

    /**
     * Mappa che associa una coppia (tipo di pezzo, colore) all'immagine del pezzo corrispondente.
     * La coppia è rappresentata dalla classe {@link Pair} contenente il tipo di pezzo e il colore del pezzo.
     */
    public static final HashMap<Pair, String> pieceToImg = new HashMap<>() {{
        // Torre
        put(new Pair(ChessType.TOWER, ChessColor.WHITE), WHITE_TOWER_IMG);
        put(new Pair(ChessType.TOWER, ChessColor.BLACK), BLACK_TOWER_IMG);

        // Alfiere
        put(new Pair(ChessType.BISHOP, ChessColor.WHITE), WHITE_BISHOP_IMG);
        put(new Pair(ChessType.BISHOP, ChessColor.BLACK), BLACK_BISHOP_IMG);

        // Cavallo
        put(new Pair(ChessType.KNIGHT, ChessColor.WHITE), WHITE_KNIGHT_IMG);
        put(new Pair(ChessType.KNIGHT, ChessColor.BLACK), BLACK_KNIGHT_IMG);

        // Regina
        put(new Pair(ChessType.QUEEN, ChessColor.WHITE), WHITE_QUEEN_IMG);
        put(new Pair(ChessType.QUEEN, ChessColor.BLACK), BLACK_QUEEN_IMG);

        // Re
        put(new Pair(ChessType.KING, ChessColor.WHITE), WHITE_KING_IMG);
        put(new Pair(ChessType.KING, ChessColor.BLACK), BLACK_KING_IMG);

        // Pedone
        put(new Pair(ChessType.PAWN, ChessColor.WHITE), WHITE_PAWN_IMG);
        put(new Pair(ChessType.PAWN, ChessColor.BLACK), BLACK_PAWN_IMG);
    }};

    /**
     * Mappa che associa una coppia (tipo di pezzo, colore) al carattere Unicode corrispondente per quel pezzo.
     * La coppia è rappresentata dalla classe {@link Pair} contenente il tipo di pezzo e il colore del pezzo.
     */
    public static final HashMap<Pair, Character> pieceToUnicode = new HashMap<>() {{
        // Torre
        put(new Pair(ChessType.TOWER, ChessColor.WHITE), WHITE_TOWER_UNICODE);
        put(new Pair(ChessType.TOWER, ChessColor.BLACK), BLACK_TOWER_UNICODE);

        // Alfiere
        put(new Pair(ChessType.BISHOP, ChessColor.WHITE), WHITE_BISHOP_UNICODE);
        put(new Pair(ChessType.BISHOP, ChessColor.BLACK), BLACK_BISHOP_UNICODE);

        // Cavallo
        put(new Pair(ChessType.KNIGHT, ChessColor.WHITE), WHITE_KNIGHT_UNICODE);
        put(new Pair(ChessType.KNIGHT, ChessColor.BLACK), BLACK_KNIGHT_UNICODE);

        // Regina
        put(new Pair(ChessType.QUEEN, ChessColor.WHITE), WHITE_QUEEN_UNICODE);
        put(new Pair(ChessType.QUEEN, ChessColor.BLACK), BLACK_QUEEN_UNICODE);

        // Re
        put(new Pair(ChessType.KING, ChessColor.WHITE), WHITE_KING_UNICODE);
        put(new Pair(ChessType.KING, ChessColor.BLACK), BLACK_KING_UNICODE);

        // Pedone
        put(new Pair(ChessType.PAWN, ChessColor.WHITE), WHITE_PAWN_UNICODE);
        put(new Pair(ChessType.PAWN, ChessColor.BLACK), BLACK_PAWN_UNICODE);
    }};

    /**
     * Mappa che associa un tipo di mossa (ad esempio cattura, arrocco) all'effetto sonoro corrispondente.
     * La mappa è utilizzata per riprodurre un suono specifico per ogni tipo di mossa.
     */
    public static final HashMap<Integer, String> moveTypeToAudio = new HashMap<>() {{
        // Associa il tipo di mossa alla rispettiva traccia audio
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
