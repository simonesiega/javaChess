package utils.constant;

/**
 * Classe che definisce i tipi di mosse possibili in una partita di scacchi.
 * Ogni tipo di mossa è rappresentato da un intero che indica una specifica azione che può essere eseguita durante la partita.
 */
public class MoveType {
    public static final int NORMAL = 0;
    public static final int CAPTURE = 1; // Catturato un pezzo
    public static final int MOVEMENT = 2; // Movimento classico
    public static final int SHORT_CASTLE = 3; // Arrocco corto
    public static final int LONG_CASTLE = 4; // Arrocco lungo
    public static final int PROMOTE = 5; // PROMOZIONE
    public static final int CHECK = 6; // Scacco
    public static final int START_GAME = 7; // Game iniziato
    public static final int CHECKMATE = 8; // Scacco matto
    public static final int ENPASSANT = 9; // Pedone passante (mossa speciale)
}
