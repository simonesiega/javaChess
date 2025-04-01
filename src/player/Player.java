package player;

import piece.*;
import utils.Move;
import utils.Pair;
import utils.PairCheck;
import utils.constant.ChessColor;
import utils.constant.ChessType;
import utils.constant.MoveType;

import java.util.ArrayList;

/**
 * Classe che rappresenta un giocatore nel gioco degli scacchi.
 * Ogni giocatore ha un nome, un re, un insieme di pezzi e una lista di pezzi mangiati.
 * Inoltre, tiene traccia dei pezzi "pinnati" e dei pezzi che attaccano il re avversario.
 */
public class Player {
    public Move king;  // Posizione del re del giocatore
    private final ArrayList<ChessButton> pieces;  // Lista dei pezzi del giocatore
    private final String name;  // Nome del giocatore

    private final ArrayList<ChessButton> pieceEaten = new ArrayList<>();  // Lista dei pezzi mangiati dal giocatore
    private ArrayList<PairCheck> pinnedPieces;  // Lista dei pezzi che sono pinnati (bloccati) dal giocatore avversario

    // Lista dei pezzi che stanno attaccando il re avversario (per il checkmate)
    private ArrayList<ChessButton> activeCheckPieces = new ArrayList<>();

    /**
     * Costruttore del giocatore.
     * @param king  La posizione del re del giocatore
     * @param name  Il nome del giocatore
     */
    public Player(Move king, String name) {
        this.king = king;
        this.name = name;
        this.pieces = new ArrayList<>();
        this.pinnedPieces = new ArrayList<>();
    }

    /**
     * Costruttore di copia per creare un nuovo giocatore come copia di un altro.
     * @param player Il giocatore da copiare
     */
    public Player(Player player) {
        this.king = new Move(player.king);
        this.name = player.name;
        this.pieces = cloneArrayPieces(player.getPieces());
        this.pinnedPieces = cloneArrayPinnedPieces(player.getPinnedPieces());
    }

    /**
     * Restituisce il nome del giocatore.
     * @return Il nome del giocatore
     */
    public String getName() {
        return name;
    }

    /**
     * Aggiunge un pezzo mangiato alla lista dei pezzi mangiati dal giocatore.
     * @param button Il pezzo mangiato
     */
    public void addPieceEaten(ChessButton button) {
        this.pieceEaten.add(button);
    }

    /**
     * Calcola e restituisce il valore totale dei pezzi mangiati dal giocatore.
     * @return Il valore totale dei pezzi mangiati
     */
    public int getValuePieceEaten() {
        int res = 0;
        for (ChessButton button : pieceEaten) {
            res += button.getValue();
        }
        return res;
    }

    /**
     * Restituisce la lista dei pezzi mangiati dal giocatore.
     * @return Lista dei pezzi mangiati
     */
    public ArrayList<ChessButton> getPieceEaten() {
        return pieceEaten;
    }

    /**
     * Clona la lista dei pezzi pinnati.
     * @param input La lista di pezzi pinnati da clonare
     * @return Una nuova lista di pezzi pinnati clonati
     */
    private ArrayList<PairCheck> cloneArrayPinnedPieces(ArrayList<PairCheck> input) {
        ArrayList<PairCheck> res = new ArrayList<>();
        for (PairCheck pairCheck : input) {
            res.add(new PairCheck(pairCheck));
        }
        return res;
    }

    /**
     * Clona la lista dei pezzi del giocatore.
     * @param input La lista dei pezzi da clonare
     * @return Una nuova lista di pezzi clonati
     */
    private ArrayList<ChessButton> cloneArrayPieces(ArrayList<ChessButton> input) {
        ArrayList<ChessButton> res = new ArrayList<>();
        for (ChessButton b : input) {
            switch (b.getPiece().getChessType()) {
                case ChessType.TOWER -> res.add(new Tower(b));
                case ChessType.BISHOP -> res.add(new Bishop(b));
                case ChessType.KNIGHT -> res.add(new Knight(b));
                case ChessType.QUEEN -> res.add(new Queen(b));
                case ChessType.KING -> res.add(new King(b));
                case ChessType.PAWN -> res.add(new Pawn(b));
                default -> res.add(new Blank(b));
            }
        }
        return res;
    }

    /**
     * Aggiunge un pezzo alla lista dei pezzi del giocatore.
     * @param chessButton Il pezzo da aggiungere
     */
    public void addPiece(ChessButton chessButton) {
        pieces.add(chessButton);
        if (chessButton instanceof King) {
            king = new Move(chessButton.getRow(), chessButton.getCol(), MoveType.NORMAL);
        }
    }

    /**
     * Aggiunge un pezzo che sta attaccando il re avversario alla lista dei pezzi che stanno dando scacco.
     * @param chessButton Il pezzo che sta attaccando il re avversario
     */
    public void addActiveCheckPiece(ChessButton chessButton) {
        this.activeCheckPieces.add(chessButton);
    }

    /**
     * Pulisce la lista dei pezzi che stanno dando scacco.
     */
    public void eraseActiveCheckPieces() {
        this.activeCheckPieces = new ArrayList<>();
    }

    /**
     * Pulisce la lista dei pezzi pinnati.
     */
    public void erasePinnedPieces() {
        pinnedPieces = new ArrayList<>();
    }

    /**
     * Aggiunge una relazione di "pinnaggio" tra due pezzi (un pezzo attivo e un pezzo passivo).
     * @param active   Il pezzo che sta pinnando
     * @param passive  Il pezzo pinnato
     */
    public void addPinnesPiece(ChessButton active, ChessButton passive) {
        pinnedPieces.add(new PairCheck(active, passive));
    }

    /**
     * Restituisce la lista dei pezzi pinnati.
     * @return Lista dei pezzi pinnati
     */
    public ArrayList<PairCheck> getPinnedPieces() {
        return pinnedPieces;
    }

    /**
     * Restituisce la lista dei pezzi che stanno dando scacco al re avversario.
     * @return Lista dei pezzi che danno scacco
     */
    public ArrayList<ChessButton> getActiveCheckPieces() {
        return activeCheckPieces;
    }

    /**
     * Restituisce la lista dei pezzi del giocatore.
     * @return Lista dei pezzi
     */
    public ArrayList<ChessButton> getPieces() {
        return this.pieces;
    }

    /**
     * Stampa i dettagli dei pezzi del giocatore.
     */
    public void printPieces() {
        System.out.println(pieces);
        for (ChessButton s : pieces) {
            System.out.println(s.getClass().getName());
        }
    }

    /**
     * Rimuove un pezzo dalla lista dei pezzi del giocatore.
     * @param input Il pezzo da rimuovere
     */
    public void removePiece(ChessButton input) {
        pieces.remove(input);
    }

    /**
     * Modifica la posizione di un pezzo esistente nella lista dei pezzi.
     * @param input   Il pezzo da modificare
     * @param second  Il nuovo pezzo con cui sostituire il precedente
     */
    public void modifyPiece(ChessButton input, ChessButton second) {
        for (ChessButton b : pieces) {
            // Trovato il pezzo da sostituire
            if (b.position() == input.position()) {
                b.move(second);
            }
        }
    }

    /**
     * Restituisce il pezzo che sta pinnando un dato pezzo.
     * @param input Il pezzo da verificare
     * @return Il pezzo che sta pinnando l'input, se esiste, altrimenti null
     */
    public ChessButton checkIfExists(ChessButton input) {
        for (PairCheck b : pinnedPieces) {
            if (b.getPassive().equals(input)) {
                // System.out.println(input + " è pinnato da: " + b.getActive());
                return b.getActive();
            }
        }
        return null;
    }

    /**
     * Stampa le informazioni sul re del giocatore.
     */
    public void printKing() {
        System.out.println(king);
    }
}
