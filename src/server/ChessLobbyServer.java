package server;
import java.io.*;
import java.net.*;
import java.util.*;
import org.json.JSONObject;

import static utils.server.ServerSocketInit.*;

/**
 * Server per la gestione della lobby.
 * Accetta due giocatori, assegna loro un colore e avvia la partita.
 */
public class ChessLobbyServer {
    /*
     * Turno corrente:
     * "0" rappresenta il bianco,
     * "1" rappresenta il nero.
     */
    private static String currentTurn = "0";


    public static void main(String[] args) {
        // Crea il server
        try (ServerSocket serverSocket = new ServerSocket(PORT, 50, InetAddress.getByName(IP_ADDRESS));) {
            System.out.println("Server in attesa di due giocatori...");

            while (true) {
                List<Player> players = new ArrayList<>();

                // Attende la connessione di due giocatori
                while (players.size() < 2) {
                    Socket playerSocket = serverSocket.accept();
                    BufferedReader input = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));
                    PrintWriter output = new PrintWriter(playerSocket.getOutputStream(), true);

                    // Richiede il nome del giocatore
                    output.println(new JSONObject().put("action", "request_name").toString());
                    String name = new JSONObject(input.readLine()).getString("name");

                    // Assegna un colore al giocatore (bianco se primo, nero se secondo)
                    String color = (players.isEmpty()) ? "0" : "1";
                    JSONObject response = new JSONObject();
                    response.put("action", "assign_color");
                    response.put("color", color);
                    output.println(response.toString());

                    // Aggiunge il giocatore alla lista
                    players.add(new Player(name, color, playerSocket, output));
                    System.out.println(name + " si è connesso con il colore " + (color.equals("0") ? "bianco" : "nero"));
                }

                System.out.println("Due giocatori connessi, avvio della partita...");

                // Notifica entrambi i giocatori dell'inizio della partita
                for (Player p : players) {
                    JSONObject startMsg = new JSONObject();
                    startMsg.put("action", "start_game");
                    startMsg.put("current_turn", currentTurn);
                    p.output.println(startMsg);
                }

                // System.out.println("Numero giocatori: " + players.size());

                // Avvia il gestore della partita in un nuovo thread
                new Thread(new GameHandler(players.get(0), players.get(1))).start();
            }

        } catch (IOException e) {
            e.printStackTrace(); // Gestione dell'eccezione di I/O
        }
    }
}

/**
 * Rappresenta un giocatore nella partita di scacchi.
 * Contiene informazioni sul nome, colore, socket di connessione e flusso di output del giocatore.
 */
class Player {
    String name; // Nome del giocatore

    String color; // Colore del giocatore ("0" per bianco, "1" per nero)

    Socket socket; // Socket di connessione del giocatore

    PrintWriter output; // Flusso di output per inviare messaggi al giocatore

    /**
     * Costruttore per inizializzare un nuovo giocatore.
     * @param name Il nome del giocatore.
     * @param color Il colore del giocatore ("0" per bianco, "1" per nero).
     * @param socket La connessione socket del giocatore.
     * @param output Il flusso di output per inviare messaggi al giocatore.
     */
    public Player(String name, String color, Socket socket, PrintWriter output) {
        this.name = name;
        this.color = color;
        this.socket = socket;
        this.output = output;
    }
}


/**
 * Gestisce il flusso della partita di scacchi tra due giocatori.
 * Esegue la gestione dei turni, delle mosse e delle condizioni di fine partita.
 */
class GameHandler implements Runnable {
    private final Player whitePlayer; // Rappresenta il giocatore con i pezzi bianchi

    private final Player blackPlayer; // Rappresenta il giocatore con i pezzi neri

    private String currentTurn = "0"; // Turno corrente della partita ("0" per il bianco, "1" per il nero)

    /**
     * Costruttore per inizializzare un gestore della partita con i due giocatori.
     * @param whitePlayer Il giocatore con i pezzi bianchi.
     * @param blackPlayer Il giocatore con i pezzi neri.
     */
    public GameHandler(Player whitePlayer, Player blackPlayer) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
    }

    /**
     * Esegue il ciclo di gioco, gestendo il turno dei giocatori e le loro mosse.
     * Gestisce anche le condizioni di fine partita (scacco matto e limite di tempo).
     */
    @Override
    public void run() {
        try {
            // Lettura delle mosse dai giocatori
            BufferedReader whiteInput = new BufferedReader(new InputStreamReader(whitePlayer.socket.getInputStream()));
            BufferedReader blackInput = new BufferedReader(new InputStreamReader(blackPlayer.socket.getInputStream()));

            // Invia il nome dell'avversario a ciascun giocatore
            JSONObject whiteOpponentName = new JSONObject();
            whiteOpponentName.put("action", "get_opponent_name");
            whiteOpponentName.put("opponent_name", blackPlayer.name);
            whitePlayer.output.println(whiteOpponentName.toString());

            JSONObject blackOpponentName = new JSONObject();
            blackOpponentName.put("action", "get_opponent_name");
            blackOpponentName.put("opponent_name", whitePlayer.name);
            blackPlayer.output.println(blackOpponentName.toString());

            // Ciclo di gioco infinito, finché non termina
            while (true) {
                // Gestione del turno dei giocatori
                if (currentTurn.equals("0")) {
                    processMove(whitePlayer, blackPlayer, whiteInput);  // Elabora la mossa del bianco
                    JSONObject response = new JSONObject();
                    response.put("action", "update_turn");
                    whitePlayer.output.println(response.toString());
                    currentTurn = "1";  // Passa il turno al nero
                } else {
                    processMove(blackPlayer, whitePlayer, blackInput);  // Elabora la mossa del nero
                    JSONObject response = new JSONObject();
                    response.put("action", "update_turn");
                    blackPlayer.output.println(response.toString());
                    currentTurn = "0";  // Passa il turno al bianco
                }
            }
        } catch (IOException e) {
            System.out.println("Un giocatore si è disconnesso.");
        }
    }

    /**
     * Gestisce una mossa di un giocatore durante il suo turno.
     * Controlla le condizioni di fine partita, come il tempo limite o lo scacco matto.
     * @param currentPlayer Il giocatore che sta facendo la mossa.
     * @param opponent Il giocatore avversario.
     * @param input BufferedReader per leggere la mossa dal giocatore corrente.
     * @throws IOException Se si verifica un errore durante la lettura della mossa.
     */
    private void processMove(Player currentPlayer, Player opponent, BufferedReader input) throws IOException {
        // Avvisa il giocatore che è il suo turno
        JSONObject turnMsg = new JSONObject();
        turnMsg.put("action", "your_turn");
        currentPlayer.output.println(turnMsg.toString());

        // Legge la mossa dal giocatore
        String message = input.readLine();
        JSONObject moveJson = new JSONObject(message);

        // Se l'azione non è "move", esce senza fare nulla
        if (!moveJson.getString("action").equals("move")) return;

        // Controlla se il tempo è scaduto
        if (moveJson.optBoolean("time-limit", false)) {
            endGameTime(opponent, currentPlayer);  // Termina la partita per superamento del limite di tempo
            return;
        }

        // Controlla se la partita è finita con uno scacco matto
        if (moveJson.optBoolean("checkmate", false)) {
            endGameCheckMate(currentPlayer, opponent);  // Termina la partita per scacco matto
            return;
        }

        // Mostra la mossa effettuata
        System.out.println(currentPlayer.name + " (" + currentPlayer.color + ") ha mosso: " + moveJson.getString("move"));

        // Invia l'aggiornamento della scacchiera all'avversario
        JSONObject response = new JSONObject();
        response.put("action", "update_board");
        response.put("move", moveJson.getString("move"));
        response.put("moveType", moveJson.getInt("moveType"));
        opponent.output.println(response.toString());
    }

    /**
     * Termina la partita a causa di scacco matto e invia i messaggi di fine partita ai giocatori.
     * @param winner Il giocatore che ha vinto.
     * @param loser Il giocatore che ha perso.
     */
    private void endGameCheckMate(Player winner, Player loser) {
        try {
            // Messaggio per il vincitore
            JSONObject winnerMsg = new JSONObject();
            winnerMsg.put("action", "game_over");
            winnerMsg.put("result", "Hai vinto!");
            winner.output.println(winnerMsg.toString());

            // Messaggio per il perdente
            JSONObject loserMsg = new JSONObject();
            loserMsg.put("action", "game_over");
            loserMsg.put("result", "Hai perso! Scacco matto.");
            loser.output.println(loserMsg.toString());

            // Chiude le connessioni con i giocatori
            winner.socket.close();
            loser.socket.close();
            System.out.println("Partita terminata. " + winner.name + " ha vinto.");
        } catch (IOException e) {
            System.out.println("Errore durante la chiusura delle connessioni.");
        }
    }

    /**
     * Termina la partita a causa di superamento del limite di tempo e invia i messaggi di fine partita.
     * @param winner Il giocatore che ha vinto per tempo.
     * @param loser Il giocatore che ha perso per tempo.
     */
    private void endGameTime(Player winner, Player loser) {
        try {
            // Messaggio per il vincitore
            JSONObject winnerMsg = new JSONObject();
            winnerMsg.put("action", "game_over");
            winnerMsg.put("result", "Hai vinto!");
            winner.output.println(winnerMsg.toString());

            // Messaggio per il perdente
            JSONObject loserMsg = new JSONObject();
            loserMsg.put("action", "game_over");
            loserMsg.put("result", "Hai perso! Limite di tempo raggiunto.");
            loser.output.println(loserMsg.toString());

            // Chiude le connessioni con i giocatori
            winner.socket.close();
            loser.socket.close();
            System.out.println("Partita terminata. " + winner.name + " ha vinto.");
        } catch (IOException e) {
            System.out.println("Errore durante la chiusura delle connessioni.");
        }
    }
}