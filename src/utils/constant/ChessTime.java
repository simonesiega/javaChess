package utils.constant;

/**
 * Classe che definisce i tempi di gioco per diverse modalità di scacchi.
 * Ogni modalità ha un tempo di gioco specifico per ogni giocatore, espresso in millisecondi.
 */
public class ChessTime {

    /* Tempo per la modalità Bullet (1 minuto per ogni giocatore). */
    public static final int BULLET = 60000; // 1 Minuto

    /* Tempo per la modalità Rapid (5 minuti per ogni giocatore). */
    public static final int RAPID = 300000; // 5 Minuti

    /* Tempo per la modalità Normal (10 minuti per ogni giocatore). */
    public static final int NORMAL = 600000; // 10 Minuti

    /* Tempo per la modalità Long (30 minuti per ogni giocatore). */
    public static final int LONG = 1800000; // 30 Minuti
}
