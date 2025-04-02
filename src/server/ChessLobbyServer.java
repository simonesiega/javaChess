package server;
import java.io.*;
import java.net.*;
import java.util.*;
import org.json.JSONObject;

import static utils.server.ServerSocketInit.*;

public class ChessLobbyServer {
    /*
     * 0 --> bianco
     * 1 --> nero
     */
    private static String currentTurn = "0";

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT, 50, InetAddress.getByName(IP_ADDRESS));) {
            System.out.println("Server in attesa di due giocatori...");

            while(true){
                List<Player> players = new ArrayList<>();
                while (players.size() < 2) {
                    Socket playerSocket = serverSocket.accept();
                    BufferedReader input = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));
                    PrintWriter output = new PrintWriter(playerSocket.getOutputStream(), true);

                    output.println(new JSONObject().put("action", "request_name").toString());
                    String name = new JSONObject(input.readLine()).getString("name");

                    String color = (players.isEmpty()) ? "0" : "1";
                    JSONObject response = new JSONObject();
                    response.put("action", "assign_color");
                    response.put("color", color);
                    output.println(response.toString());

                    players.add(new Player(name, color, playerSocket, output));
                    System.out.println(name + " si è connesso con il colore " + color);
                }

                System.out.println("Due giocatori connessi, avvio della partita...");

                for (Player p : players) {
                    JSONObject startMsg = new JSONObject();
                    startMsg.put("action", "start_game");
                    startMsg.put("current_turn", currentTurn);
                    p.output.println(startMsg.toString());
                }

                System.out.println("NUmero giocatori: "+ players.size());
                new Thread(new GameHandler(players.get(0), players.get(1))).start();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Player {
    String name;
    String color;
    Socket socket;
    PrintWriter output;

    public Player(String name, String color, Socket socket, PrintWriter output) {
        this.name = name;
        this.color = color;
        this.socket = socket;
        this.output = output;
    }
}

class GameHandler implements Runnable {
    private final Player whitePlayer;
    private final Player blackPlayer;

    /*
     * 0 --> bianco
     * 1 --> nero
     */
    private String currentTurn = "0";

    public GameHandler(Player whitePlayer, Player blackPlayer) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
    }

    @Override
    public void run() {
        try {
            BufferedReader whiteInput = new BufferedReader(new InputStreamReader(whitePlayer.socket.getInputStream()));
            BufferedReader blackInput = new BufferedReader(new InputStreamReader(blackPlayer.socket.getInputStream()));

            JSONObject whiteOpponentName = new JSONObject();
            whiteOpponentName.put("action", "get_opponent_name");
            whiteOpponentName.put("opponent_name", blackPlayer.name);
            whitePlayer.output.println(whiteOpponentName.toString());

            JSONObject blackOpponentName = new JSONObject();
            blackOpponentName.put("action", "get_opponent_name");
            blackOpponentName.put("opponent_name", whitePlayer.name);
            blackPlayer.output.println(blackOpponentName.toString());

            while (true) {
                if (currentTurn.equals("0")) {
                    processMove(whitePlayer, blackPlayer, whiteInput);
                    JSONObject response = new JSONObject();
                    response.put("action", "update_turn");
                    whitePlayer.output.println(response.toString());
                    currentTurn = "1";
                } else {
                    processMove(blackPlayer, whitePlayer, blackInput);
                    JSONObject response = new JSONObject();
                    response.put("action", "update_turn");
                    blackPlayer.output.println(response.toString());
                    currentTurn = "0";
                }
            }
        } catch (IOException e) {
            System.out.println("Un giocatore si è disconnesso.");
        }
    }

    private void processMove(Player currentPlayer, Player opponent, BufferedReader input) throws IOException {
        JSONObject turnMsg = new JSONObject();
        turnMsg.put("action", "your_turn");
        currentPlayer.output.println(turnMsg.toString());

        String message = input.readLine();
        JSONObject moveJson = new JSONObject(message);
        if (!moveJson.getString("action").equals("move")) return;

        if (moveJson.optBoolean("time-limit", false)) {
            // System.out.println("reached time limit");
            endGameTime(opponent, currentPlayer);
            return;
        }

        if (moveJson.optBoolean("checkmate", false)) {
            // System.out.println("reached check mate");
            endGameCheckMate(currentPlayer, opponent);
            return;
        }

        System.out.println(currentPlayer.name + " (" + currentPlayer.color + ") ha mosso: " + moveJson.getString("move"));

        JSONObject response = new JSONObject();
        response.put("action", "update_board");
        response.put("move", moveJson.getString("move"));
        response.put("moveType", moveJson.getInt("moveType"));
        opponent.output.println(response.toString());
    }

    private void endGameCheckMate(Player winner, Player loser) {
        try {
            // Messaggio di fine partita per entrambi i giocatori
            JSONObject winnerMsg = new JSONObject();
            winnerMsg.put("action", "game_over");
            winnerMsg.put("result", "Hai vinto!");
            winner.output.println(winnerMsg.toString());

            JSONObject loserMsg = new JSONObject();
            loserMsg.put("action", "game_over");
            loserMsg.put("result", "Hai perso! Scacco matto.");
            loser.output.println(loserMsg.toString());

            // Chiudi connessioni
            winner.socket.close();
            loser.socket.close();
            System.out.println("Partita terminata. " + winner.name + " ha vinto.");
        } catch (IOException e) {
            System.out.println("Errore durante la chiusura delle connessioni.");
        }
    }

    private void endGameTime(Player winner, Player loser) {
        try {
            // Messaggio di fine partita per entrambi i giocatori
            JSONObject winnerMsg = new JSONObject();
            winnerMsg.put("action", "game_over");
            winnerMsg.put("result", "Hai vinto!");
            winner.output.println(winnerMsg.toString());

            JSONObject loserMsg = new JSONObject();
            loserMsg.put("action", "game_over");
            loserMsg.put("result", "Hai perso! Scacco matto.");
            loser.output.println(loserMsg.toString());

            // Chiudi connessioni
            winner.socket.close();
            loser.socket.close();
            System.out.println("Partita terminata. " + winner.name + " ha vinto.");
        } catch (IOException e) {
            System.out.println("Errore durante la chiusura delle connessioni.");
        }
    }
}