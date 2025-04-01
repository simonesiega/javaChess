package gui;

import org.json.JSONObject;

import utils.Move;
import utils.constant.MoveType;
import utils.Pair;
import utils.constant.ChessColor;
import utils.constant.ChessType;
import piece.*;
import player.Player;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static utils.constant.ChessPosition.*;
import static utils.constant.ChessImg.*;

public class Window extends JFrame {

    /* Client */
    Socket socket;
    BufferedReader input;
    PrintWriter output;

    /* Timer */
    private static int timeRemaining0; // Tempo rimanente per il giocatore 0 (bianco)
    private static int timeRemaining1; // Tempo rimanente per il giocatore 1 (nero)
    private static Timer timer0; // Timer per il giocatore 0
    private static Timer timer1; // Timer per il giocatore 1
    private static JLabel label_time0; // Etichetta per il tempo del giocatore 0
    private static JLabel label_time1; // Etichetta per il tempo del giocatore 1
    private static JLabel playerLabel0; // Etichetta per il nome del giocatore 0
    private static JLabel playerLabel1; // Etichetta per il nome del giocatore 1
    private static final Color color2 = new Color(115, 149, 82); // Colore della casella 1
    private static final Color color1 = new Color(235, 236, 208); // Colore della casella 2

    private static JPanel iconListPanel1; //Pannello icone per il giocatore 1
    private static JPanel iconListPanel2; //Pannello icone per il giocatore 2

    private final int WIDTH = 1000; // Larghezza della finestra
    private final int HEIGHT =  800; // Altezza della finestra

    private final int ROWS = 8; // Numero di righe sulla scacchiera
    private final int COLS = 8; // Numero di colonne sulla scacchiera

    private final ArrayList<ChessButton> matrix = new ArrayList<>(); // Matrimonio dei bottoni della scacchiera
    private final ArrayList<Move> movesMatch = new ArrayList<>(); // Lista delle mosse del match

    private ArrayList<Pair> INITIAL_BOARD; // La scacchiera iniziale
    private String path; // Percorso del file di salvataggio delle mosse

    private AudioInputStream audioInputStream; // Stream audio per la riproduzione di suoni
    private JPanel topPanel; // Pannello superiore
    private JPanel centerPanel; // Pannello centrale (scacchiera)
    private JPanel boardPanel; // Pannello della scacchiera
    private JPanel bottomPanel; // Pannello inferiore
    private JPanel movePanel; // Pannello delle mosse
    private ChessButton piece1, piece2; // Pulsanti associati ai pezzi

    private File file; // File per il salvataggio delle mosse
    private int cSubD; // Sottocartella per il salvataggio della partita

    /*
     * pos 0  "bianco" (ex player1)
     * pos 1  "nero" (ex player2)
     */
    private final ArrayList<Player> players = new ArrayList<>(); // Lista dei giocatori

    /*
     * 0 --> bianco
     * 1 --> nero
     */
    private int turno; // Il turno corrente
    private int ownColor;
    private int moveType; // Tipo di mossa

    private final boolean offlineGame; // true se la modalitá di gioco é offline, false altrimenti

    private final ComponentListener listener = new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
            int buttonWidth = boardPanel.getWidth() / COLS;
            int buttonHeight =boardPanel.getHeight() / ROWS;

            for (ChessButton button : matrix) {
                // Ridimensiona l'icona in base alla nuova dimensione dei bottoni
                setImageChessButton(button, buttonWidth, buttonHeight);
            }
        }
    };

    /*
     * MATTI POSSIBILI PER TESTING https://it.chesstempo.com/tactical-motifs#Mate%20di%20Morphy
     * fen iniziale rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w
     */

    /**
     * Costruttore della finestra di gioco.
     * @param title Titolo della finestra
     * @param player1 Il primo giocatore
     * @param player2 Il secondo giocatore
     * @param fen Notazione FEN per la scacchiera iniziale
     * @param offlineGame true se il game viene giocato in locale, false altrimenti
     */
    public Window(String title, Player player1, Player player2, String fen, boolean offlineGame) {
        super(title);
        players.add(player1);
        players.add(player2);
        this.offlineGame = offlineGame;
        if (offlineGame) initOffline(WIDTH, HEIGHT, fen);
        else initOnline(WIDTH, HEIGHT);
    }

    /**
     * Inizializza la finestra, i pannelli e la logica di gioco per una partita offline.
     * Configura la scacchiera iniziale utilizzando una notazione FEN, imposta i tempi di gioco per i giocatori
     * e prepara l'interfaccia utente per una partita in locale.
     * @param width  Larghezza della finestra
     * @param height Altezza della finestra
     * @param fen    Notazione FEN per la scacchiera iniziale
     */
    private void initOffline(int width, int height, String fen) {
        // Impostazioni della finestra: dimensione, posizione e comportamento alla chiusura
        this.setSize(width, height);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout(5, 5));

        try {
            // Ricostruisce la scacchiera dalla notazione FEN fornita, o usa una configurazione standard se il parametro è nullo
            // La notazione FEN di default descrive la configurazione iniziale degli scacchi
            INITIAL_BOARD = reBuildChessBoardFromFen(Objects.requireNonNullElse(fen, "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w"));
        } catch (Exception e) {
            // Gestisce eventuali errori durante la ricostruzione della scacchiera
            e.printStackTrace();
        }

        // Imposta il tempo iniziale di gioco per entrambi i giocatori (5 minuti per il giocatore nero e 1 minuto per il giocatore bianco)
        timeRemaining0 = 300000; // Tempo per il giocatore nero (5 minuti)
        timeRemaining1 = 300000;   // Tempo per il giocatore bianco (1 minuto)

        // Crea un file per memorizzare le informazioni sulla partita (come le mosse dei giocatori)
        path = "src/resources/matches/offline/" + players.get(0).getName() + "-" + players.get(1).getName() + ".csv";
        file = new File(path);
        createFileIfNotExists(file); // Crea il file se non esiste già

        // Inizializza i riferimenti ai pezzi selezionati, che saranno usati per gestire le mosse
        piece1 = null;
        piece2 = null;

        // Configura le etichette per il tempo rimanente di ciascun giocatore, con uno stile di font e allineamento
        label_time1 = new JLabel(formatTime(timeRemaining1), SwingConstants.CENTER);
        label_time1.setFont(new Font("Arial", Font.BOLD, 24));
        label_time0 = new JLabel(formatTime(timeRemaining0), SwingConstants.CENTER);
        label_time0.setFont(new Font("Arial", Font.BOLD, 24));

        // Crea i timer per ciascun giocatore (gestisce il countdown per ciascun giocatore)
        timer1 = createTimer(label_time1, 1, null);
        timer0 = createTimer(label_time0, 0, timer1);
        timer1 = createTimer(label_time1, 1, timer0);

        // Crea i pannelli per i giocatori: superiore (per il giocatore nero) e inferiore (per il giocatore bianco)
        topPanel = createPlayerPanel(players.get(1).getName(), ChessColor.BLACK, label_time0, playerLabel1); // Pannello per il giocatore nero
        centerPanel = createCenterPanel(); // Pannello centrale per la scacchiera
        bottomPanel = createPlayerPanel(players.get(0).getName(), ChessColor.WHITE, label_time1, playerLabel0); // Pannello per il giocatore bianco

        // Aggiunge i pannelli alla finestra principale in base alla disposizione
        this.add(topPanel, BorderLayout.NORTH);
        this.add(centerPanel);
        this.add(bottomPanel, BorderLayout.SOUTH);

        // Riproduce il suono di inizio partita
        playSound(MoveType.START_GAME);

        // Salva la posizione iniziale della scacchiera in formato FEN
        writeFen(buildFenFromChessBoard());
    }



    /**
     * Inizializza la finestra, i pannelli e la logica di gioco online.
     * Connette il client al server di gioco, gestisce l'inizializzazione della scacchiera
     * e configura l'interfaccia utente per il match online.
     * @param width  Larghezza della finestra
     * @param height Altezza della finestra
     */
    private void initOnline(int width, int height) {
        // Indirizzo IP del server di gioco
        String serverIP = "192.168.1.106";

        // Imposta il tempo iniziale di gioco per entrambi i giocatori (5 minuti per il giocatore nero e 1 minuto per il giocatore bianco)
        timeRemaining0 = 300000; // Tempo per il giocatore nero (5 minuti)
        timeRemaining1 = 300000;   // Tempo per il giocatore bianco (1 minuto)

        try {
            // Apertura della connessione al server
            socket = new Socket(serverIP, 25565);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

            // Lettura della richiesta del nome utente dal server
            JSONObject nameRequest = new JSONObject(input.readLine());
            Scanner scanner = new Scanner(System.in);

            String name = "";
            // Se il server richiede il nome del giocatore
            if (nameRequest.getString("action").equals("request_name")) {
                System.out.print("Inserisci il tuo nome: ");
                name = scanner.nextLine();

                // Creazione della risposta con il nome del giocatore
                JSONObject nameResponse = new JSONObject();
                nameResponse.put("action", "send_name");
                nameResponse.put("name", name);
                output.println(nameResponse);
            }

            // Ricezione del colore assegnato dal server
            JSONObject colorResponse = new JSONObject(input.readLine());
            System.out.println("Sei il giocatore con i pezzi " + ((colorResponse.getString("color").equals("0")) ? "bianchi!" : "neri!"));

            String color = colorResponse.getString("color");
            ownColor = (color.equals("0")) ? ChessColor.WHITE : ChessColor.BLACK;

            // Ricezione delle informazioni sul primo turno
            JSONObject startGame = new JSONObject(input.readLine());
            String turnoServer = startGame.getString("current_turn");
            // System.out.println("Il giocatore con il colore " + turnoServer + " inizia la partita");

            turno = (turnoServer.equals("0")) ? ChessColor.WHITE : ChessColor.BLACK;
            // System.out.println("Ho impostato turno a: " + turno);

            // Configurazione della finestra principale del gioco
            this.setSize(width, height);
            this.setLocationRelativeTo(null); // Centra la finestra sullo schermo
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setLayout(new BorderLayout(5, 5));

            try {
                // Impostazione iniziale della scacchiera con la posizione standard FEN
                INITIAL_BOARD = reBuildChessBoardFromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w");
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Creazione del file per il salvataggio delle mosse
            createDirIfNotExists(new File("src/resources/matches/online"));
            path = "src/resources/matches/online/match-" + cSubD + "/" + name + "-" + ownColor + ".csv";
            System.out.println(ownColor);
            file = new File(path);
            createFileIfNotExists(file);

            // Inizializzazione dei riferimenti ai pezzi selezionati
            piece1 = null;
            piece2 = null;

            // Configurazione dei timer e delle etichette dei giocatori
            label_time1 = new JLabel(formatTime(timeRemaining1), SwingConstants.CENTER);
            label_time1.setFont(new Font("Arial", Font.BOLD, 24));
            label_time0 = new JLabel(formatTime(timeRemaining0), SwingConstants.CENTER);
            label_time0.setFont(new Font("Arial", Font.BOLD, 24));

            // Creazione dei timer per il conteggio del tempo
            timer1 = createTimer(label_time1, 1, null);
            timer0 = createTimer(label_time0, 0, timer1);
            timer1 = createTimer(label_time1, 1, timer0);

            // Creazione dei pannelli superiori e inferiori con le informazioni sui giocatori
            bottomPanel = createPlayerPanel((ownColor == 1) ? "avversario" : name, ChessColor.WHITE, label_time1, playerLabel0);
            topPanel = createPlayerPanel((ownColor == 1) ? name : "avversario", ChessColor.BLACK, label_time0, playerLabel1);
            centerPanel = createCenterPanel();

            // Impostazione del tempo di gioco iniziale
            timeRemaining0 = 300000; // Tempo giocatore nero
            timeRemaining1 = 300000;   // Tempo giocatore bianco

            // Aggiunta dei pannelli alla finestra principale
            this.add(topPanel, BorderLayout.NORTH);
            this.add(centerPanel);
            this.add(bottomPanel, BorderLayout.SOUTH);

            // Riproduce il suono di inizio partita
            playSound(MoveType.START_GAME);

            // Salvataggio dello stato iniziale della scacchiera
            writeFen(buildFenFromChessBoard());

            // Thread per la gestione della comunicazione con il server
            new Thread(() -> {
                try {
                    String move;

                    // Ciclo di ricezione dei messaggi dal server
                    while ((move = input.readLine()) != null) {
                        JSONObject message = new JSONObject(move);

                        switch (message.getString("action")) {
                            case "get_opponent_name":
                                // Ricezione del nome dell'avversario
                                // System.out.println("Ricevuto nome avversario: " + message.getString("opponent_name"));
                                break;

                            case "update_board":
                                // Ricezione e gestione della mossa avversaria
                                // System.out.println("Mossa avversaria: " + message.getString("move"));

                                // Estrazione delle coordinate della mossa
                                String extractedMove = message.getString("move");
                                String move1 = extractedMove.split("-")[0];
                                String move2 = extractedMove.split("-")[1];

                                // Conversione delle coordinate della mossa
                                int int1 = (move1.charAt(0) - 97); // Colonna iniziale
                                int int2 = 8 - (move1.charAt(1) - 48); // Riga iniziale
                                piece1 = matrix.get(int2 * 8 + int1);

                                int1 = (move2.charAt(0) - 97); // Colonna finale
                                int2 = 8 - (move2.charAt(1) - 48); // Riga finale
                                piece2 = matrix.get(int2 * 8 + int1);

                                // Esecuzione della mossa ricevuta
                                getOrMakeMoveFromServer();

                                // Reset dei riferimenti ai pezzi
                                piece1 = null;
                                piece2 = null;
                                break;

                            default:
                                // Messaggio sconosciuto dal server
                                // System.out.println("Messaggio sconosciuto dal server: " + message);
                                break;
                        }
                    }

                    System.out.println("Connessione chiusa.");
                } catch (IOException b) {
                    System.out.println("Connessione al server persa.");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();

        } catch (IOException e) {
            System.out.println("Impossibile connettersi al server.");
        }
    }


    /**
     * Crea un timer per il giocatore specificato.
     * Il timer decrementa il tempo rimanente ogni 100 ms e aggiorna l'etichetta corrispondente.
     * Se il tempo scade, il timer si ferma e dichiara il vincitore e il perdente.
     * @param label  JLabel che mostra il tempo rimanente per il giocatore
     * @param player Identificatore del giocatore (0 per il primo, 1 per il secondo)
     * @param other  Il timer dell'altro giocatore, che verrà fermato se questo giocatore perde
     * @return Un'istanza di Timer che aggiorna il tempo rimanente e gestisce la fine della partita
     */
    private Timer createTimer(JLabel label, int player, Timer other) {
        return new Timer(100, (ActionEvent e) -> {
            // Controlla quale giocatore sta usando il timer e decrementa il tempo
            if (player == 1 && timeRemaining1 > 0) {
                timeRemaining1 -= 100;
                label.setText(formatTime(timeRemaining1));
            } else if (player == 0 && timeRemaining0 > 0) {
                timeRemaining0 -= 100;
                label.setText(formatTime(timeRemaining0));
            } else {
                // Se il tempo è esaurito, ferma entrambi i timer
                ((Timer) e.getSource()).stop();
                label.setText("HO PERSO");
                other.stop();

                // Dichiarazione del vincitore
                if (player == 1) {
                    label_time0.setText("HO VINTO!");
                } else {
                    label_time1.setText("HO VINTO!");
                }

                int otherPlayer = player == 1 ? 0 : 1;
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }

                this.dispose();

                JSONObject moveJson = new JSONObject();
                moveJson.put("action", "move");
                moveJson.put("checkmate", true);
                output.println(moveJson);
                try {
                    socket.close();
                } catch (IOException c) {
                    c.printStackTrace();
                }

                new ResultWindow(player, otherPlayer, players.get(player).getName(), players.get(otherPlayer).getName(), "Tempo esaurito!");
            }
        });
    }

    /**
     * Verifica se il file esiste, e in caso contrario, lo crea.
     * Se il file esiste già, lo elimina e lo ricrea.
     * @param file Il file da verificare e creare se necessario.
     */
    private void createFileIfNotExists(File file) {
        try {
            // Se il file non esiste, crealo
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    throw new IOException("Impossibile creare il file.");
                }
            } else {
                // Se il file esiste, eliminiamo e ricreiamo
                Path filePath = Path.of(file.getAbsolutePath());
                Files.delete(filePath);  // Elimina il file esistente
                if (!file.createNewFile()) {
                    throw new IOException("Impossibile ricreare il file.");
                }
            }
        } catch (IOException e) {
            // In caso di errore, stampa il messaggio di errore e termina l'applicazione
            System.err.println("Errore durante la creazione del file: " + e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private void createDirIfNotExists(File dir) {
        countSubD(dir);

        System.out.println("dentro il metodo " + cSubD);
        String nuovoPercorso = dir + "/" + "match-" + cSubD;
        System.out.println(nuovoPercorso);

        File nuovaCartella = new File(nuovoPercorso);
        if (!nuovaCartella.exists()) {
            nuovaCartella.mkdirs(); // Crea la cartella con il nuovo numero
        }
    }

    private void countSubD(File cartella) {
        if (!cartella.exists() || !cartella.isDirectory()) {
            System.out.println("Percorso non valido: " + path);
            System.exit(-1);
        }

        System.out.println(cartella.listFiles().length);

        cSubD = Objects.requireNonNull(cartella.listFiles()).length;
    }

    /**
     * Crea un pannello per un giocatore, con nome, timer e pulsante impostazioni.
     * @param name        Nome del giocatore.
     * @param color       Colore del giocatore (bianco o nero).
     * @param timerLabel  Etichetta per il timer del giocatore.
     * @param playerLabel Etichetta per il nome del giocatore.
     * @return Il pannello migliorato per il giocatore.
     */
    private static JPanel createPlayerPanel(String name, int color, JLabel timerLabel, JLabel playerLabel) {
        JPanel panel = new JPanel(new BorderLayout(4, 4));
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
                BorderFactory.createEmptyBorder(1, 8, 1, 8)
        ));

        // Sezione sinistra: Nome giocatore e icone catturate
        JPanel leftSection = new JPanel(new BorderLayout());
        leftSection.setOpaque(false);

        // Crea un'etichetta per il nome del giocatore.
        playerLabel = new JLabel(name);
        playerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        playerLabel.setForeground(new Color(40, 40, 40));
        playerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Crea il pannello per le icone catturate (disposte orizzontalmente).
        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        iconPanel.setPreferredSize(new Dimension(500, 20));
        iconPanel.setOpaque(false);

        // A seconda del colore, imposta il pannello delle icone catturate.
        if (color == ChessColor.WHITE) {
            iconListPanel1 = iconPanel;
        } else {
            iconListPanel2 = iconPanel;
        }

        leftSection.add(playerLabel, BorderLayout.NORTH);
        leftSection.add(iconPanel, BorderLayout.CENTER);

        // Sezione destra: Timer e bottone impostazioni
        JPanel rightSection = new JPanel();
        rightSection.setLayout(new BoxLayout(rightSection, BoxLayout.Y_AXIS));
        rightSection.setOpaque(false);
        rightSection.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Crea il pulsante per le impostazioni
        JButton impButton = new JButton("Impostazioni");
        impButton.setFont(new Font("Arial", Font.BOLD, 14));
        impButton.setBackground(new Color(80, 150, 230));
        impButton.setForeground(Color.WHITE);
        impButton.setFocusPainted(false);
        impButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Effetto hover per il pulsante
        impButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                impButton.setBackground(new Color(70, 130, 210));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                impButton.setBackground(new Color(80, 150, 230));
            }
        });

        // Evento per aprire la finestra delle impostazioni
        impButton.addActionListener(e -> new ImpoControlWindow());

        // Imposta l'estetica del timer
        timerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        timerLabel.setOpaque(true);
        timerLabel.setBackground(new Color(245, 245, 245));
        timerLabel.setForeground(new Color(50, 50, 50));
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timerLabel.setPreferredSize(new Dimension(140, 25));
        timerLabel.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 2));

        // Aggiungi il pulsante delle impostazioni e il timer alla sezione destra.
        rightSection.add(impButton);
        rightSection.add(Box.createRigidArea(new Dimension(0, 3)));
        rightSection.add(timerLabel);

        // Aggiungi le sezioni sinistra e destra al pannello principale.
        panel.add(leftSection, BorderLayout.CENTER);
        panel.add(rightSection, BorderLayout.EAST);

        return panel;
    }


    /**
     * Crea e restituisce il pannello centrale dell'interfaccia grafica del gioco.
     * Il pannello centrale è composto da due sezioni:
     * Una scacchiera (a sinistra), che contiene i pulsanti per ogni posizione sulla scacchiera di gioco.
     * Un pannello delle mosse (a destra), che visualizza la lista delle mosse effettuate durante la partita.
     * Il pannello delle mosse è contenuto in uno JScrollPane che permette lo scroll verticale se il numero di mosse supera lo spazio disponibile.
     * @return il pannello centrale creato.
     */
    private JPanel createCenterPanel() {
        // Creazione del pannello principale
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1; // La scacchiera occupa tutto lo spazio verticale disponibile

        // Pannello della scacchiera
        boardPanel = new JPanel(new GridLayout(ROWS, COLS));
        boardPanel.setPreferredSize(new Dimension(300, 300));
        boardPanel.setMinimumSize(new Dimension(250, 250));  // Imposta la dimensione minima della scacchiera.

        // Aggiungi i pulsanti per ogni posizione sulla scacchiera
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                // Determina il colore della casella (bianca o nera)
                Color c = ((i + j) % 2 == 0) ? color1 : color2;

                // Crea il pulsante per la casella della scacchiera
                ChessButton button = createChessButton(INITIAL_BOARD.get(i * 8 + j), c, i, j);
                matrix.add(button);

                // Aggiunge il pezzo all'elenco del giocatore, se il pezzo non è vuoto
                if (!button.getPiece().getChessColor().equals(ChessColor.BLANK))
                    players.get(button.getPiece().getChessColor()).addPiece(button);

                boardPanel.add(button); // Aggiunge il pulsante alla scacchiera
            }
        }

        boardPanel.addComponentListener(listener);

        // Pannello per le mosse a destra
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        movePanel = new JPanel();
        movePanel.setLayout(new BoxLayout(movePanel, BoxLayout.Y_AXIS));
        movePanel.setAlignmentY(Component.TOP_ALIGNMENT);

        // Imposta la dimensione minima per il pannello delle mosse, per evitare che si ridimensioni troppo quando vuoto
        movePanel.setPreferredSize(new Dimension(120, 250));
        movePanel.setMinimumSize(new Dimension(120, 250));

        // Crea uno JScrollPane per permettere lo scrolling delle mosse
        JScrollPane scrollPane = new JScrollPane(movePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        rightPanel.add(scrollPane, BorderLayout.CENTER);

        // Aggiunge il pannello della scacchiera e il pannello delle mosse al pannello principale
        gbc.gridx = 0;
        gbc.weightx = 0.6;
        panel.add(boardPanel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.4;
        panel.add(rightPanel, gbc);

        return panel;
    }

    /**
     * Aggiunge una mossa al pannello delle mosse visualizzato nell'interfaccia grafica.
     * La mossa viene formattata in notazione scacchistica e inserita in un pannello a seconda della posizione
     * (sinistra per le mosse del bianco, destra per quelle del nero).
     * @param move  la mossa da visualizzare
     * @param first il ChessButton relativo al pezzo che ha effettuato la mossa, utilizzato per determinare l'icona e la posizione
     */
    private void addMoveToLabel(Move move, ChessButton first) {
        // Converte la mossa in una stringa in notazione scacchistica, ad esempio "e4" o "Nf3"
        String printMove = move.printPosition(first.getPiece(), first.getCol());

        // Verifica la dimensione della lista delle mosse per determinare la disposizione nel pannello
        if ((movesMatch.size() - 1) % 2 == 0) {
            // Crea un nuovo pannello per visualizzare la mossa su due colonne
            JPanel itemPanel = new JPanel(new GridLayout(1, 2));
            itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            itemPanel.setPreferredSize(new Dimension(100, 40));
            itemPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

            // Crea l'etichetta con la mossa e la aggiunge al pannello
            JLabel leftLabel = new JLabel(printMove, SwingConstants.CENTER);
            itemPanel.add(leftLabel);
            movePanel.add(itemPanel);
        } else {
            // Se la mossa è quella del nero, aggiunge l'etichetta alla seconda colonna dell'ultimo pannello aggiunto
            JLabel rightLabel = new JLabel(printMove, SwingConstants.CENTER);
            JPanel g = (JPanel) movePanel.getComponent(movePanel.getComponentCount() - 1);
            g.add(rightLabel);
        }

        // Aggiunge una mossa al pannello delle mosse e forziamo il ridimensionamento
        movePanel.revalidate();
        movePanel.repaint();
    }

    /**
     * Scrive una stringa FEN (Forsyth-Edwards Notation) in un file, aggiungendola in coda.
     * Questo metodo viene usato per registrare lo stato della partita dopo ogni mossa.
     * @param fen la notazione FEN da scrivere sul file
     */
    private void writeFen(String fen) {
        try {
            // Apre il file in modalità append
            FileWriter writer = new FileWriter(file, true); // Scrive la FEN seguita da un'interruzione di riga
            writer.write(fen + "\n");
            writer.close();
        } catch (IOException ioe) {
            System.out.println("Errore durante la lettura del file:");
            System.out.println(ioe.getMessage());
        }
    }

    /**
     * Converte il tempo rimanente in millisecondi in un formato leggibile.
     * @param milliseconds Il tempo rimanente in millisecondi
     * @return Il tempo nel formato MM:SS
     */
    private static String formatTime(int milliseconds) {
        int minutes = (milliseconds / 1000) / 60;
        int seconds = (milliseconds / 1000) % 60;
        int millis = (milliseconds % 1000) / 100;

        if (milliseconds < 60000) {
            return String.format("%02d.%d", seconds, millis); // MM:SS.mmm
        } else {
            return String.format("%02d:%02d", minutes, seconds); // MM:SS
        }
    }


    /**
     * Metodo per la creazione dinamica di un bottone a seconda del tipo di pezzo.
     * Gestisce la creazione dei bottoni per i vari pezzi.
     * @param piece coppia ChessType, ChessColor che rappresenta il pezzo da creare
     * @param c colore di background del bottone
     * @param i riga sulla scacchiera
     * @param j colonna sulla scacchiera
     * @return il bottone creato con le opportune caratteristiche
     */
    private ChessButton createChessButton(Pair piece, Color c, int i, int j) {
        ChessButton button;

        // Creazione del bottone in base al tipo del pezzo
        button = createPieceButton(piece, c, i, j);

        // Aggiunge il listener per la gestione degli eventi
        addActionListenerToButton(button);

        // Impostazione della dimensione e dell'immagine per il bottone
        setButtonSizeAndImage(button);

        return button;
    }

    /**
     * Metodo che gestisce la creazione del bottone in base al tipo di pezzo.
     * Gestisce la logica per ogni tipo di pezzo (torre, cavallo, alfiere, ecc.).
     * @param piece coppia ChessType, ChessColor che rappresenta il pezzo da creare
     * @param c colore di background del bottone
     * @param i riga sulla scacchiera
     * @param j colonna sulla scacchiera
     * @return il bottone creato
     */
    private ChessButton createPieceButton(Pair piece, Color c, int i, int j) {
        ChessButton button;

        switch(piece.getChessType()){
            case ChessType.TOWER -> {
                button = new Tower(piece,c, 5,i,j);

                // Controllo se torre bianca è già stata mossa
                if(piece.getChessColor().equals(ChessColor.WHITE)){
                    if(!new Move(i,j,MoveType.NORMAL).equals(DEFAULT_WHITE_ROOK_1) && !new Move(i,j,MoveType.NORMAL).equals(DEFAULT_WHITE_ROOK_2))
                        button.setAlreadyMoved();
                }
                // Controllo se torre nera è già stata mossa
                else{
                    if(!new Move(i,j,MoveType.NORMAL).equals(DEFAULT_BLACK_ROOK_1) && !new Move(i,j,MoveType.NORMAL).equals(DEFAULT_BLACK_ROOK_2))
                        button.setAlreadyMoved();
                }
            }
            case ChessType.BISHOP -> button = new Bishop(piece,c, 3, i,j);
            case ChessType.KNIGHT -> button = new Knight(piece,c, 3,i,j);
            case ChessType.QUEEN -> button = new Queen(piece,c,10, i,j);
            case ChessType.KING -> {
                button = new King(piece,c,i,j);

                // Controllo se il re nero è già stato mosso
                if(piece.getChessColor().equals(ChessColor.BLACK)){
                    if(i != DEFAULT_BLACK_KING.getRow() || j != DEFAULT_BLACK_KING.getCol()){
                        button.setAlreadyMoved();
                    }
                }
                // Controllo se il re bianco è già stato mosso
                else{
                    if(i != DEFAULT_WHITE_KING.getRow() || j != DEFAULT_WHITE_KING.getCol()){
                        button.setAlreadyMoved();
                    }
                }
            }
            case ChessType.PAWN -> {
                button = new Pawn(piece,c, 1, i,j);

                // Verifica se il pedone è già stato mosso per entrambi i colori
                if(piece.getChessColor().equals(ChessColor.BLACK)){
                    if(i != DEFAULT_BLACK_ROW_PAWN)
                        button.setAlreadyMoved();
                }else{
                    if(i != DEFAULT_WHITE_ROW_PAWN)
                        button.setAlreadyMoved();
                }
            }

            default -> button = new Blank(piece,c,i,j);
        }

        // Impostazione degli attributi base del bottone
        setupButtonAttributes(button);

        return button;
    }

    /**
     * Aggiunge un listener per gestire gli eventi sui bottoni della scacchiera.
     * Il listener verifica se il pezzo selezionato è valido e gestisce la logica del movimento del pezzo.
     * Controlla anche se è il turno del giocatore e se il movimento del pezzo è legale.
     * @param button il bottone (ChessButton) su cui aggiungere il listener
     */
    private void addActionListenerToButton(ChessButton button) {
        button.addActionListener(e -> {

            // Verifica se il gioco è online (non offline)
            if (!offlineGame) {
                // Controllo se è il turno del giocatore
                if (piece1 == null && turno != (ownColor)) {
                    System.out.println("Non puoi muoverti, aspetta la mossa dell'avversario.");
                    return; // Se non è il turno del giocatore, il movimento è ignorato
                }
            }

            // Controlla se il primo pezzo selezionato è vuoto
            if (piece1 == null && button.getPiece().getChessType().equals(ChessType.BLANK)) {
                // Se il pezzo è vuoto, ignora il click
                return;
            }

            // Controlla se il primo pezzo appartiene all'avversario
            if (piece1 == null && !button.getPiece().getChessColor().equals(turno)) {
                // Se il pezzo appartiene all'avversario, non puoi selezionarlo
                return;
            }

            // Se il primo pezzo non è stato ancora selezionato, seleziona il pezzo
            if (piece1 == null) {
                piece1 = button; // Imposta il primo pezzo selezionato
            }

            // Se il secondo pezzo non è ancora stato selezionato, seleziona il secondo pezzo
            else if (piece2 == null) {
                piece2 = button; // Imposta il secondo pezzo selezionato

                // Verifica se la mossa è valida (usando la logica di movimento degli scacchi)
                if (isValidMove(piece1, piece2)) {

                    // Se il gioco è online, invia la mossa al server
                    if (!offlineGame) {
                        JSONObject moveJson = new JSONObject();
                        moveJson.put("action", "move");
                        moveJson.put("move", piece1.printPosition() + "-" + piece2.printPosition());
                        output.println(moveJson);
                    }

                    // Gestisci la mossa, inclusa l'eventuale cattura
                    try {
                        getOrMakeMoveFromServer();
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }

                // Svuota le variabili dei pezzi una volta che il movimento è stato eseguito
                piece1 = null;
                piece2 = null;
            }
        });
    }

    /**
     * Gestisce il movimento del pezzo, verifica se la mossa genera scacco matto o stalemate,
     * e aggiorna il file FEN con la posizione corrente della scacchiera.
     * Viene anche verificato se il movimento ha causato uno scacco matto e, in tal caso,
     * termina il gioco mostrando una finestra di risultato.
     */
    public void getOrMakeMoveFromServer() throws InterruptedException {
        // Esegue il movimento del pezzo (inclusa l'eventuale cattura)
        movePiece(piece1, piece2);

        // Verifica se la mossa ha causato uno scacco
        findPossibleChecks();

        // Scrive la posizione attuale della scacchiera in formato FEN nel file CSV
        writeFen(buildFenFromChessBoard());

        // Verifica se la mossa ha causato uno scacco matto
        if (isCheckMate(piece1)) {
            // Suona il suono di scacco matto
            playSound(MoveType.CHECKMATE);

            // Ferma i timer del gioco (per entrambi i giocatori)
            timer0.stop();
            timer1.stop();

            Thread.sleep(2000);
            this.dispose();

            JSONObject moveJson = new JSONObject();
            moveJson.put("action", "move");
            moveJson.put("checkmate", true);
            output.println(moveJson);
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Determina il giocatore perdente e mostra una finestra di risultato
            int otherPlayer = (turno == 1) ? 0 : 1;
            new ResultWindow(turno, otherPlayer, players.get(turno).getName(), players.get(otherPlayer).getName(), players.get(otherPlayer).getName() + " ha subito scacco matto!");

            return; // Termina la funzione se c'è scacco matto
        }

        // Se non c'è scacco matto, cambia il turno
        changeTurn();

        // Avvia e ferma i timer per il giocatore successivo
        startAndStopClock();
    }

    /**
     * Imposta la dimensione e l'immagine per il bottone.
     * Se la larghezza della scacchiera è zero, usa dimensioni predefinite.
     * @param button il bottone da configurare
     */
    private void setButtonSizeAndImage(ChessButton button) {
        if (boardPanel.getWidth() == 0) {
            // Se la larghezza del pannello è zero, usa dimensioni fisse
            setImageChessButton(button, 50, 50);
        } else {
            // Imposta la dimensione in base alla dimensione del pannello
            button.setPreferredSize(new Dimension(boardPanel.getWidth() / 8, boardPanel.getHeight() / 8));
            setImageChessButton(button, boardPanel.getWidth() / 8, boardPanel.getHeight() / 8);
        }
    }

    /**
     * Imposta gli attributi base per il bottone (stile visivo).
     * @param button il bottone da configurare
     */
    private void setupButtonAttributes(ChessButton button) {
        button.setFocusPainted(true);
        button.setBorderPainted(true);
        button.setContentAreaFilled(true);
    }

    /***
     * Metodo per far partire il clock dell'avversario alla fine del turno.
     * Il timer dell'avversario viene avviato mentre il timer del giocatore corrente viene fermato.
     */
    private void startAndStopClock(){
        if(turno == ChessColor.WHITE){
            timer0.stop();
            timer1.start();
        }else{
            timer0.start();
            timer1.stop();
        }
    }

    /***
     * Metodo che imposta l'immagine di sfondo corrispettiva per ogni ChessButton.
     * La dimensione dell'immagine viene adattata alla dimensione del bottone.
     * @param button il bottone a cui impostare l'immagine
     * @param buttonWidth larghezza del bottone
     * @param buttonHeight altezza del bottone
     */
    private void setImageChessButton(ChessButton button, int buttonWidth, int buttonHeight){
        ImageIcon img = new ImageIcon(pieceToImg.get(button.getPiece()));

        Image scaledImage = img.getImage().getScaledInstance(
                buttonWidth, buttonHeight, Image.SCALE_SMOOTH);

        button.setIcon(new ImageIcon(scaledImage));
    }

    /***
     * Metodo che controlla se è il turno del bianco o nero e ritorna l'opposto.
     * @return il colore opposto (bianco o nero)
     */
    private int getOppositeColor(){
        return turno == ChessColor.WHITE ? ChessColor.BLACK : ChessColor.WHITE;
    }

    /***
     * Metodo per il controllo del checkmate (scacco matto).
     * Verifica se il giocatore è in scacco matto o se ci sono altre condizioni di fine partita (come stalemate).
     * @param chessButton il bottone del pezzo che sta cercando di verificare il checkmate
     * @return true se scacco matto, false altrimenti
     */
    private boolean isCheckMate(ChessButton chessButton){

        //1. Verifico che il player sia sotto scacco
        boolean found = false;

        // System.out.println("TEST COLORE: " + chessButton.getPiece().getChessColor() + ", " + turno);

        // Cerco i pezzi che danno scacco, controllo tutti i pezzi del colore del pezzo che sta dando scacco
        for(ChessButton button : players.get(turno).getPieces()){

            //Per ogni pezzo, controllo se posso raggiungere il re avversario senza controllare la disposizione degli altri pezzi sulla scacchiera
            out : for(Move m : button.getAllPossibleMoves()){

                //Il pezzo raggiunge il re, controllo se non si interpongono altri pezzi tra button e il re avversario
                if(m.equals(players.get(getOppositeColor()).king)){

                    // Verifico che non ci siano pezzi tra il pezzo che dà scacco e il re
                    for(Move inner : button.getStepForThisMove(players.get(getOppositeColor()).king)){
                        /*
                         * Se un qualsiasi pezzo si interpone tra button e il re avversario, allora non può essere scacco
                         * Posso interrompere la ricerca per button e procedere con il pezzo successivo
                         */
                        if(!matrix.get(inner.position()).getPiece().getChessType().equals(ChessType.BLANK)){
                            break out;
                        }
                    }

                    //getActiveCheckPieces: ritorna tutti i pezzi che danno attivamente scacco oppure che controllano una casella dove il re non può muoversi
                    players.get(turno).addActiveCheckPiece(button);
                    found = true;
                }
            }
        }

        /*
        //Se found è impostato a false, allora non c'è alcun scacco
        if(!found){
            System.out.println("non sono in scacco, possibile stalemate");
        }
        */

        //2. Verifico se il re si può spostare su altre caselle senza finire in scacco
        boolean canMove = searchSquareCheckMate(matrix.get(players.get(getOppositeColor()).king.position()).getAllPossibleMoves(), chessButton, getOppositeColor());

        /*
         * Se il re non si può spostare su altre caselle senza finire in scacco e, contemporaneamente, non è in scacco
         * Allora potrebbe esserci una condizione di stalemate
         * */

        if(!canMove && !found){
            // System.out.println("STALEMATE!!!");

            //Verifico se il player avversario può effettuare una qualsiasi mossa legale
            return !helperStaleMate(players.get(getOppositeColor()).getPieces());
        }

        /*
         * Se il re avversario può muoversi senza finire in scacco, allora non c'è scacco matto
         * */
        if(canMove){
            // System.out.println("POSSO MUOVERMI SENZA FINIRE IN SCACCO");
            players.get(turno).eraseActiveCheckPieces();
            players.get(getOppositeColor()).eraseActiveCheckPieces();
            return false;
        }

        /*
        System.out.println("PASSATO IL SECONDO TEST");
        System.out.println(players.get(turno).getActiveCheckPieces());
        System.out.println(players.get(getOppositeColor()).getActiveCheckPieces());
        */

        //3. Verifico se un pezzo avversario può sovrapporsi allo scacco oppure mangia un pezzo che da scacco, se ci sono due o più attacchi allora è matto
        //3.1 Controllo se posso mangiare il pezzo che da scacco

        //player1: uguale turno, player2: opposite, player.king: opposite
        return helperIsCheckMate(players.get(turno), players.get(getOppositeColor()), chessButton, players.get(getOppositeColor()).king);
    }

    /***
     * Metodo che verifica se un giocatore avversario può effettuare una qualsiasi mossa legale.
     * @param pieces i pezzi del giocatore avversario
     * @return true se c'è almeno una mossa legale, false altrimenti
     */
    private boolean helperStaleMate(ArrayList<ChessButton> pieces){

        // Verifica ogni pezzo per eventuali mosse legali
        for(ChessButton chessButton : pieces){

            //Non posso muovere il king, controllo già effettuato con la variabile canMove
            if(chessButton instanceof King)
                continue;

            //Faccio un controllo più specifico per il pedone
            if(chessButton instanceof Pawn){
                out : for(Move outer: chessButton.getAllPossibleMoves()){
                    if(outer.getMoveType() == MoveType.MOVEMENT){
                        for(Move inner : chessButton.getStepForThisMove(outer)){
                            // se trovo una casella occupata
                            if(matrix.get(inner.position()).getPiece().getChessType() != ChessType.BLANK){
                                continue out;
                            }
                        }

                        if(matrix.get(outer.position()).getPiece().getChessType() != ChessType.BLANK)
                            continue;

                        //Posso muovere il pedone
                        return true;
                    }else if(outer.getMoveType() == MoveType.CAPTURE){
                        if(matrix.get(outer.position()).getPiece().getChessType() != getOppositeColor())
                            continue;

                        //Posso mangiare
                        return true;
                    }
                }
                continue;
            }

            // Verifica per ogni altro pezzo
            out : for(Move outer : chessButton.getAllPossibleMoves()){
                for(Move inner : chessButton.getStepForThisMove(outer)){
                    // se trovo una casella occupata
                    if(matrix.get(inner.position()).getPiece().getChessType() != ChessType.BLANK){
                        continue out;
                    }
                }

                // Ho trovato una mossa valida
                // Controllo se il pezzo non é pinnato
                return players.get(getOppositeColor()).checkIfExists(chessButton) == null;

            }
        }

        return false;

    }

    /**
     * Metodo di backup per verificare se dopo una mossa il re è ancora sotto scacco.
     * @param first Il primo ChessButton (pezzo che si sta muovendo)
     * @param second Il secondo ChessButton (destinazione del movimento)
     * @return true se dopo il movimento il re è ancora sotto scacco, altrimenti false
     */
    private boolean checkAfterMoveIsStillCheckBackup(ChessButton first, ChessButton second){
        // Crea una copia della matrice di gioco
        ArrayList<ChessButton> copy = new ArrayList<>(matrix);
        ChessButton firstCopy = copyChessButton(first);

        // Crea una copia dei giocatori
        Player copyPlayer1 = new Player(players.get(0));
        Player copyPlayer2 = new Player(players.get(1));

        // Salva la posizione e il colore del primo pezzo
        int oldP = firstCopy.position();
        int rowP = firstCopy.getRow();
        int colP = firstCopy.getCol();
        Color oldC = firstCopy.getBackgroundColor();

        // Sposta i pezzi all'interno dell'arraylist dei giocatori, con il controllo per la rimozione di un pezzo catturato
        if(firstCopy.getPiece().getChessColor().equals(ChessColor.WHITE)){
            if(second.getPiece().getChessColor().equals(ChessColor.BLACK)){
                copyPlayer2.removePiece(second);
            }
            copyPlayer1.modifyPiece(firstCopy, second);
        }else if(firstCopy.getPiece().getChessColor().equals(ChessColor.BLACK)){
            if(second.getPiece().getChessColor().equals(ChessColor.WHITE)){
                copyPlayer1.removePiece(second);
            }
            copyPlayer2.modifyPiece(firstCopy, second);
        }

        // Sposta il pezzo nella copia e aggiorna la matrice
        firstCopy.move(second);
        firstCopy.setAlreadyMoved();
        copy.set(second.position(), firstCopy);
        copy.set(oldP, new Blank(new Pair(-1,-1), oldC, rowP, colP));

        //Controllo se muovo il re, devo assumere la sua posizione fittizia
        Move fakeKing = null;
        if(firstCopy instanceof King){
            fakeKing = new Move(second.getRow(), second.getCol(),MoveType.NORMAL);
        }

        //Il pezzo è stato spostato, verifico se first è ancora pinnato da active. Se questo è vero la mossa è valida altrimenti ritorno false
        if(firstCopy.getPiece().getChessColor().equals(ChessColor.BLACK)){
            return helperCheckAfterMoveIsStillCheck(copy, copyPlayer1.getPieces(), fakeKing, players.get(1).king);
        }
        else{
            return helperCheckAfterMoveIsStillCheck(copy, copyPlayer2.getPieces(), fakeKing, players.get(0).king);
        }
    }

    /**
     * Verifica se una mossa potrebbe portare al checkmate, considerando la possibilità di mangiare il pezzo che da scacco
     * o di intraprendere una mossa per bloccare lo scacco.
     * @param active Il giocatore che sta dando lo scacco
     * @param passive Il giocatore sotto scacco
     * @param chessButton La casella attuale del pezzo che sta tentando di risolvere lo scacco
     * @param realKing La mossa reale del re avversario
     * @return true se il checkmate è possibile, false se ci sono mosse che evitano lo scacco
     */
    private boolean helperIsCheckMate(Player active, Player passive, ChessButton chessButton, Move realKing ){
        // Se due pezzi stanno dando scacco contemporaneamente, è checkmate
        if(active.getActiveCheckPieces().size() > 1)
            return true;

        ChessButton checkPiece = active.getActiveCheckPieces().get(0);

        // Verifica se è possibile mangiare il pezzo che sta dando lo scacco, il pezzo che mangia non deve essere pinnato
        if(!checkIfCanEatActivePiece(passive, active, chessButton, checkPiece))
            return false;

        // Verifica se è possibile bloccare lo scacco mettendo un pezzo tra il re e il pezzo che da scacco
        return checkIfCanObstacleTheCheck(passive, active, checkPiece, realKing);
    }

    /**
     * Verifica se un pezzo può mangiare il pezzo che sta dando scacco al re. Il pezzo che mangia non deve essere pinnato.
     * @param passive Il giocatore sotto scacco
     * @param active Il giocatore che sta dando lo scacco
     * @param chessButton La casella del pezzo che sta cercando di mangiare l'avversario
     * @param checkPiece Il pezzo che sta dando scacco
     * @return true se non è possibile mangiare il pezzo, false altrimenti
     */
    private boolean checkIfCanEatActivePiece(Player passive, Player active, ChessButton chessButton, ChessButton checkPiece){
        out : for(ChessButton button : passive.getPieces()){

            // Verifica se il pezzo è pinnato
            if(passive.checkIfExists(chessButton) != null){
                // System.out.println("il pezzo è pinnato, non posso mangiare");
                continue;
            }

            // Verifica se il pezzo può raggiungere il pezzo che dà scacco
            for(Move move : button.getAllPossibleMoves()){

                //Controllo per il pedone, non posso catturare con una mossa di movimento
                if(move.getMoveType() == MoveType.MOVEMENT)
                    continue;

                // Verifica se il percorso è ostacolato da altri pezzi
                if(move.position() == checkPiece.position()){

                    for(Move inner : button.getStepForThisMove(move)){
                        if(!matrix.get(inner.position()).getPiece().getChessType().equals(ChessType.BLANK)) {
                            // É presente un ostacolo, quindi non posso mangiare il pezzo
                            continue out;
                        }
                    }

                    // Se non ci sono ostacoli, verifica se il re è ancora sotto scacco dopo la mossa
                    if(checkAfterMoveIsStillCheckBackup(button, checkPiece))
                        continue out;

                    // Dopo aver mangiato il pezzo, rimuove i pezzi attivi
                    passive.eraseActiveCheckPieces();
                    active.eraseActiveCheckPieces();

                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Verifica se è possibile bloccare lo scacco mettendo un pezzo tra il re e il pezzo che dà scacco.
     * @param passive Il giocatore sotto scacco
     * @param active Il giocatore che sta dando lo scacco
     * @param checkPiece Il pezzo che sta dando lo scacco
     * @param realKing La mossa del re avversario
     * @return true se è possibile bloccare lo scacco, false altrimenti
     */
    private boolean checkIfCanObstacleTheCheck(Player passive, Player active, ChessButton checkPiece, Move realKing){
        for(Move move : checkPiece.getStepForThisMove(realKing)){
            // System.out.println("controllo mossa: " + move);
            out : for(ChessButton button : passive.getPieces()){

                //Il king non può sovrapporsi allo scacco perché finirebbe in scacco
                if(button instanceof King)
                    continue;

                // Verifica se il pezzo è pinnato
                if(passive.checkIfExists(piece1) != null){
                    continue;
                }

                for(Move inner : button.getAllPossibleMoves()){
                    // Verifica se il pezzo può raggiungere la casella che blocca lo scacco
                    if(inner.equals(move)){

                        //Controllo del pedone, non posso sovrappormi a uno scacco in diagonale se la casella di destinazione è vuota (quindi non c'è un pezzo da mangiare)
                        if(inner.getMoveType() == MoveType.CAPTURE && !matrix.get(move.position()).getPiece().getChessColor().equals(ChessColor.BLACK)){
                            continue;
                        }

                        for(Move step : button.getStepForThisMove(move)){
                            if(!matrix.get(step.position()).getPiece().getChessType().equals(ChessType.BLANK)) {
                                continue out;
                            }
                        }

                        passive.eraseActiveCheckPieces();
                        active.eraseActiveCheckPieces();
                        // System.out.println("POSSO SOVRAPPORMI con mossa: " + move + " e pezzo: " + button);
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Metodo che verifica se un re è in scacco su ogni casella disponibile per il movimento del re.
     * Se almeno una casella non è sotto scacco, il re può muoversi senza entrare in scacco.
     * @param kingMoves Le possibili mosse del re
     * @param first Il ChessButton che rappresenta il re
     * @param color Il colore del giocatore
     * @return true se il re può muoversi senza entrare in scacco, false altrimenti
     */
    private boolean searchSquareCheckMate(ArrayList<Move> kingMoves, ChessButton first, int color){

        ArrayList<Move> possibleCheckObstacled = new ArrayList<>();

        // Verifica per ogni mossa se il re finisce in scacco
        for(Move m : kingMoves){

            // Se sono in scacco, non posso arroccare anche se il re si deve ancora muovere
            if(m.getMoveType() == MoveType.LONG_CASTLE || m.getMoveType() == MoveType.SHORT_CASTLE)
                continue;

            // Crea una copia della matrice per simulare la mossa
            ArrayList<ChessButton> copy = cloneArrayPieces(matrix);
            ChessButton firstCopy = copyChessButton(first);

            int rowP = firstCopy.getRow();
            int colP = firstCopy.getCol();
            Color oldC = firstCopy.getBackgroundColor();

            ArrayList<Player> copyPlayer = new ArrayList<>();
            copyPlayer.add(new Player(players.get(0))); // Giocatore bianco
            copyPlayer.add(new Player(players.get(1)));  // Giocatore nero

            // Verifica se la casella è occupata da un pezzo dello stesso colore
            if(copy.get(m.position()).getPiece().getChessColor().equals(color)){
                continue;
            }

            // Verifica se il re può mangiare un pezzo avversario
            if(copy.get(m.position()).getPiece().getChessColor().equals(getOppositeColor())){
                copyPlayer.get(getOppositeColor()).removePiece(copy.get(m.position()));
            }

            if(m.position() > 63 || m.position() < 0){
                continue;
            }

            // Verifica se la mossa è legale per evitare scacco
            if (helperSearchSquareCheckMate(m,copy, copyPlayer.get(turno).getPieces(),possibleCheckObstacled , players.get(getOppositeColor()).king, oldC, rowP, colP)) return possibleCheckObstacled.isEmpty();

        }
        return false;
    }

    /***
     * Metodo di ricerca per verificare se una mossa lascia il re in scacco o meno.
     * Se la mossa lascia il re in scacco, continua la ricerca.
     * Altrimenti, restituisce false se la mossa è valida.
     * @param m Mossa che il re ha appena fatto.
     * @param copyMatrix Copia della matrice di gioco già modificata.
     * @param piecesPlayer Pezzi del giocatore che sta dando scacco.
     * @param possibleCheckObstacled Elenco dei movimenti possibili con ostacoli.
     * @param realKing Posizione del re nella matrice originale (non nella copia).
     * @param oldC Colore del pezzo che si stava muovendo prima di essere spostato.
     * @param rowP Riga originale del pezzo che si stava muovendo.
     * @param colP Colonna originale del pezzo che si stava muovendo.
     * @return boolean Indica se la mossa ha generato scacco o meno.
     */
    private boolean helperSearchSquareCheckMate(Move m,ArrayList<ChessButton> copyMatrix, ArrayList<ChessButton> piecesPlayer, ArrayList<Move> possibleCheckObstacled, Move realKing,
                                                Color oldC, int rowP, int colP){

        // Spostamento temporaneo del re sulla copia della matrice
        copyMatrix.set(copyMatrix.get(m.position()).position(), new King(matrix.get(realKing.position()).getPiece(), matrix.get(realKing.position()).getBackgroundColor(), m.getRow(), m.getCol()));
        copyMatrix.set(realKing.position(), new Blank(new Pair(-1,-1), oldC, rowP, colP));

        // Ciclo attraverso i pezzi del giocatore per verificare se sono in grado di minacciare il re
        piece : for(ChessButton p : piecesPlayer){
            // Ciclo su tutti i possibili movimenti di ogni pezzo
            in : for(Move step : p.getAllPossibleMoves()){

                if(step.equals(m)){

                    // Verifica se il pedone vuole "catturare" il re
                    if(step.getMoveType() == MoveType.CAPTURE){
                        return false;
                    }

                    if(p instanceof King){
                        if(possibleCheckObstacled.contains(m))
                            possibleCheckObstacled.remove(m);
                        else
                            possibleCheckObstacled.add(m);
                        continue piece;
                    }

                    // Analizzo i possibili passi di ciascun pezzo per verificare ostacoli
                    for(Move inner : p.getStepForThisMove(m)){

                        if(!copyMatrix.get(inner.position()).getPiece().getChessColor().equals(ChessColor.BLANK) && !copyMatrix.get(inner.position()).getPiece().getChessColor().equals(getOppositeColor())){
                            continue in;
                        }

                        if(!copyMatrix.get(inner.position()).getPiece().getChessColor().equals(ChessColor.BLANK)){
                            // Trovato un ostacolo per quel movimento
                            if(possibleCheckObstacled.contains(m))
                                possibleCheckObstacled.remove(m);
                            else
                                possibleCheckObstacled.add(m);

                            continue piece;
                        }
                    }

                    // Se raggiungiamo questo punto, significa che è scacco
                    return false;
                }
            }
        }

        // Se nessuna delle condizioni ha causato scacco, restituisco true
        return true;
    }

    /***
     * Metodo per cambiare il turno dopo una mossa valida.
     * Alterna tra il colore bianco e il colore nero.
     */
    private void changeTurn(){
        turno = (turno == ChessColor.WHITE) ? ChessColor.BLACK : ChessColor.WHITE;
    }

    /***
     * Metodo per trovare possibili scacchi e pezzi pinnati.
     */
    private void findPossibleChecks(){
        //Svuoto l'array perché sono pronto a trovare dei nuovi pezzi pinnati
        players.get(getOppositeColor()).erasePinnedPieces();

        // Controllo ogni pezzo del giocatore per verificare se può dare scacco al re avversario
        for(ChessButton chessButton : players.get(turno).getPieces()){
            out : for(Move m : chessButton.getAllPossibleMoves()){
                // Se il pezzo minaccia il re avversario
                if(m.equals(players.get(getOppositeColor()).king)){

                    /*
                    Metodo di verifica per la ricerca dei pezzi pinnati
                    1. se pinnedPiece rimane null, allora non c'è un pezzo pinnato
                    2. se pinnedPiece non è null e non trovo nessun altro pezzo allora lo aggiungo all'array
                    3. se pinnedPiece non è null e non trovo un altro pezzo qualsiasi, non è un pezzo pinnato
                     */
                    ChessButton pinnedPiece = null;

                    /*
                    In questo controllo vengono verificate 3 cose
                    1. p da effettivamente scacco al re
                    2. p è ostacolato da UN solo pezzo di colore uguale al re, quindi quel pezzo diventa pinnato
                    3. p è ostacolato da più di un pezzo, quindi non ci sono pezzi pinnati ne scacco
                     */

                    // Verifica se ci sono ostacoli lungo il percorso per vedere se il pezzo è pinnato
                    for(Move inner : chessButton.getStepForThisMove(players.get(getOppositeColor()).king)){
                        if(!matrix.get(inner.position()).getPiece().getChessColor().equals(ChessColor.BLANK)){

                            //Non è presente un pezzo pinnato
                            if(pinnedPiece != null){
                                // System.out.println(" ho trovato due o più pezzi");
                                break out;
                            }

                            // Se trovo un pezzo valido che potrebbe essere pinnato
                            if(!matrix.get(inner.position()).getPiece().getChessColor().equals(ChessColor.BLANK)){
                                pinnedPiece = matrix.get(inner.position());
                            }
                        }
                    }

                    // Se il pezzo trovato è dello stesso colore del re avversario, è un pezzo pinnato
                    if(pinnedPiece != null && pinnedPiece.getPiece().getChessColor().equals(getOppositeColor())){
                        // System.out.println("trovato un pezzo pinnato:" + pinnedPiece);
                        players.get(getOppositeColor()).addPinnesPiece(chessButton, pinnedPiece);
                    }

                    // Se non ci sono pezzi pinnati, imposta lo stato di scacco
                    if(pinnedPiece == null) moveType = MoveType.CHECK;
                }
            }
        }

        // Riproduce il suono associato al tipo di mossa
        playSound(moveType);
    }


    /***
     * Metodo per verificare se una mossa è valida senza modificare la scacchiera.
     * @param first Casella di partenza del pezzo.
     * @param second Casella di destinazione del pezzo.
     * @return boolean Restituisce true se la mossa è valida, altrimenti false.
     */
    private boolean isValidMove(ChessButton first, ChessButton second){

        // Verifica se le caselle di partenza e destinazione sono uguali
        if(first.equals(second)){
            return false;
        }

        // Verifica se il pezzo sta cercando di mangiare un pezzo dello stesso colore
        if(first.getPiece().getChessColor().equals(second.getPiece().getChessColor())){
            return false;
        }

        // Verifica se si sta cercando di muovere un pezzo pinnato
        ChessButton active = players.get(turno).checkIfExists(first);
        if(active != null){
            ArrayList<ChessButton> copy = new ArrayList<>(matrix);
            if(!search(copy, active, first, second)) return false;
        }

        ArrayList<Move> moves = first.getAllPossibleMoves();

        // Tipo di mossa di default
        moveType = MoveType.NORMAL;
        boolean isEnPassant = false;

        // Se il pezzo è un pedone, gestiamo i vari casi speciali per i pedoni
        if(first.getPiece().getChessType().equals(ChessType.PAWN)){

            //Se la casella di destinazione è blank, allora voglio per forza muovermi SENZA mangiare
            if(second.getPiece().getChessColor().equals(ChessColor.BLANK)) {
                int step = turno == ChessColor.WHITE ? 1 : -1;

                // Verifica se il pedone sta cercando di mangiare in diagonale
                if(Math.abs(first.getCol() - second.getCol()) == 1 && (first.getRow() - second.getRow()) == step){

                    // Verifica se il pedone sta cercando di catturare un altro pedone tramite "En Passant"
                    if(matrix.get((second.getRow()+step) * 8 + second.getCol()) instanceof Pawn){

                        //Controllo se l'ultima mossa del pedone coincide con l'ultima mossa della partita
                        if(((Pawn) matrix.get((second.getRow()+step) * 8 + second.getCol())).getWhenFirstTwoStep() == movesMatch.size()){
                            isEnPassant = true;
                            moveType = MoveType.ENPASSANT;
                        }
                    }
                }
                if(!isEnPassant)
                    moveType = MoveType.MOVEMENT;
            } else {
                // Se la destinazione non è vuota, deve essere una cattura
                if (first.getCol() == second.getCol()) return false;

                moveType = MoveType.CAPTURE;
            }
        }

        // Verifica se la mossa è consentita per il pezzo
        Move secondMove = new Move(second.getRow(), second.getCol(), moveType);

        //Cerco se la mossa effettuata è consentita per il pezzo, NON controlla la scacchiera
        boolean found = false;
        Move searchedMove = null;

        // Verifica se la mossa è valida tra le mosse disponibili
        for(Move move : moves){
            //First TEORICAMENTE può raggiungere second senza controllare la scacchiera
            if(move.equals(secondMove)){
                found = true;
                searchedMove = move;
                break;
            }
        }

        if(!found) return false;


        // Verifica se la cattura è legittima
        if(isOppositeColor(first.getPiece().getChessColor(), second.getPiece().getChessColor()) || isEnPassant){
            if(isEnPassant){
                searchedMove.setMoveType(MoveType.ENPASSANT);
            }else{
                searchedMove.setMoveType(MoveType.CAPTURE);
            }
        }

        //Controllo se il player vuole arroccare
        if(first instanceof King  && (searchedMove.getMoveType() == MoveType.SHORT_CASTLE || searchedMove.getMoveType() == MoveType.LONG_CASTLE)){

            // Controllo che il re non si sia già mosso
            if(matrix.get(first.position()).isAlreadyMoved()) return false;

            moveType = searchedMove.getMoveType();

            Move checkTowerPosition;
            boolean isLong = false;

            // arrocco lungo
            if(first.getCol() > second.getCol()){
                checkTowerPosition = new Move(second.getRow(), 0, MoveType.MOVEMENT);
                // controllo se la torre non si è mai mossa
                if(!matrix.get(checkTowerPosition.position()).getPiece().getChessType().equals(ChessType.TOWER) || matrix.get(checkTowerPosition.position()).isAlreadyMoved()){
                    return false;
                }
                isLong = true;
            }

            // arrocco corto
            else{
                checkTowerPosition = new Move(second.getRow(), 7, MoveType.MOVEMENT);
                if(!matrix.get(checkTowerPosition.position()).getPiece().getChessType().equals(ChessType.TOWER) || matrix.get(checkTowerPosition.position()).isAlreadyMoved()){
                    return false;
                }
            }

            ArrayList<Move> kingMoves = ((King) first).getStepForThisMoveCastle(isLong);

            //controllo se i alcuni pezzi del colore avversario controllano le case intermedie
            return searchCheckSquare(kingMoves);
        }

        // Controllo se la cattura del pedone è valida
        if(first.getPiece().getChessType().equals(ChessType.PAWN) && searchedMove.getMoveType() != MoveType.NORMAL && ((secondMove.getMoveType() != MoveType.CAPTURE && searchedMove.getMoveType() == MoveType.CAPTURE))){
            if(!isEnPassant)return false;
        }

        // Verifica se ci sono ostacoli tra le caselle di partenza e destinazione
        for(Move move : first.getStepForThisMove(searchedMove)){
            if(!matrix.get(move.position()).getPiece().getChessColor().equals(ChessColor.BLANK)) return false;
        }

        // Verifica se la mossa non mette il re in scacco
        return !checkAfterMoveIsStillCheck(first, second);
    }

    /***
     * Verifica se due colori sono opposti (bianco/nero).
     * @param color1 Primo colore.
     * @param color2 Secondo colore.
     * @return boolean Ritorna true se i colori sono opposti, altrimenti false.
     */
    private boolean isOppositeColor(int color1, int color2){
        if(color1 == ChessColor.BLANK || color2 == ChessColor.BLANK) return false;

        return ((color1 == ChessColor.BLACK && color2 == ChessColor.WHITE) || (color1 == ChessColor.WHITE && color2 == ChessColor.BLACK));
    }

    /***
     * Metodo che verifica se, dopo una mossa, il re è ancora in scacco.
     * @param first Il pezzo che sta effettuando la mossa.
     * @param second La casella di destinazione per il primo pezzo.
     * @return boolean Restituisce true se il re è ancora in scacco, false altrimenti.
     */
    private boolean checkAfterMoveIsStillCheck(ChessButton first, ChessButton second){
        ArrayList<ChessButton> copy = new ArrayList<>(matrix);
        ChessButton firstCopy = copyChessButton(first);

        // Crea una copia dei giocatori per verificare se il re è ancora in scacco
        ArrayList<Player> copyPlayer = new ArrayList<>();
        copyPlayer.add(new Player(players.get(0))); // Giocatore bianco
        copyPlayer.add(new Player(players.get(1))); // Giocatore nero

        // Salvo la posizione originale del pezzo che si sta spostando
        int oldP = firstCopy.position();
        int rowP = firstCopy.getRow();
        int colP = firstCopy.getCol();
        Color oldC = firstCopy.getBackgroundColor();

        // Se il pezzo sta catturando un altro pezzo, rimuoviamo il pezzo catturato
        if(second.getPiece().getChessColor().equals(getOppositeColor())){
            copyPlayer.get(getOppositeColor()).removePiece(second);
        }

        // Aggiorno i pezzi dei giocatori nella copia
        copyPlayer.get(turno).modifyPiece(firstCopy, second);
        firstCopy.move(second);
        firstCopy.setAlreadyMoved();

        // Aggiorno la matrice con il pezzo spostato
        copy.set(second.position(), firstCopy);
        copy.set(oldP, new Blank(new Pair(-1,-1), oldC, rowP, colP));

        // Verifico se la mossa lascia il re in scacco
        Move fakeKing = null;
        if(firstCopy instanceof King){
            fakeKing = new Move(second.getRow(), second.getCol(),MoveType.NORMAL);
        }

        //Il pezzo è stato spostato, verifico se first è ancora pinnato da active. Se questo è vero la mossa è valida altrimenti ritorno false
        return helperCheckAfterMoveIsStillCheck(copy, copyPlayer.get(getOppositeColor()).getPieces(), fakeKing, players.get(turno).king);
    }

    /**
     * Metodo universale per bianco e nero
     * Verifica se il re è ancora sotto scacco dopo una mossa.
     * @param copyMatrix copia della vera matrice di gioco
     * @param piecesPlayer lista dei pezzi del giocatore sotto scacco
     * @param fakeKing se il pezzo che sto spostando è il re
     * @param realKing posizione del re nella vera matrice
     * @return true se il re è ancora sotto scacco, altrimenti false
     */
    private boolean helperCheckAfterMoveIsStillCheck(ArrayList<ChessButton> copyMatrix, ArrayList<ChessButton> piecesPlayer, Move fakeKing, Move realKing){
        for(ChessButton chessButton : piecesPlayer){
            // Per ogni pezzo controllo se può dare scacco
            out : for(Move m : chessButton.getAllPossibleMoves()){
                if(m.getMoveType() == MoveType.MOVEMENT) continue;

                fakeKing = (fakeKing != null) ? fakeKing : realKing;

                // Verifica se il movimento corrisponde alla posizione del re
                if(m.equals(fakeKing)){
                    // Controllo se un pezzo si trova sulla via del re
                    for(Move inner : chessButton.getStepForThisMove(fakeKing)){
                        if(!copyMatrix.get(inner.position()).getPiece().getChessColor().equals(ChessColor.BLANK)){
                            break out;
                        }
                    }
                    // Se il re è ancora sotto scacco, ritorno true
                    return true;
                }
            }
        }
        // Se il re non è più sotto scacco
        return false;
    }

    /**
     * Metodo per effettuare una deep copy (contenuto e non puntatore) di un'istanza di ChessButton.
     * Crea una nuova istanza di ChessButton basata sul tipo di pezzo.
     * @param chessButton il ChessButton da copiare
     * @return una nuova istanza di ChessButton
     */
    private ChessButton copyChessButton(ChessButton chessButton){
        switch(chessButton.getPiece().getChessType()){
            case ChessType.TOWER -> {
                return new Tower(chessButton);
            }
            case ChessType.BISHOP -> {
                return new Bishop(chessButton);
            }
            case ChessType.KNIGHT -> {
                return new Knight(chessButton);
            }
            case ChessType.QUEEN -> {
                return new Queen(chessButton);
            }
            case ChessType.KING -> {
                return new King(chessButton);
            }
            case ChessType.PAWN -> {
                return new Pawn(chessButton);
            }

            default -> {return new Blank(chessButton);}
        }
    }

    /**
     * Metodo usato per eseguire una deep copy di ogni singolo elemento nell'array chessButtons.
     * @param chessButtons lista di ChessButton da copiare
     * @return una nuova lista di ChessButton
     */
    private ArrayList<ChessButton> cloneArrayPieces(ArrayList<ChessButton> chessButtons){
        ArrayList<ChessButton> res = new ArrayList<>();
        for(ChessButton button : chessButtons){
            res.add(copyChessButton(button)); // Aggiunge la copia di ogni ChessButton
        }
        return res;
    }

    /**
     * Metodo che cerca se il re può muoversi senza entrare in scacco.
     * Se trova una mossa che lascia il re sotto scacco, ritorna false.
     * @param kingMoves lista delle possibili mosse del re
     * @return true se tutte le mosse sono sicure per il re, altrimenti false
     */
    private boolean searchCheckSquare(ArrayList<Move> kingMoves){
        // System.out.println(kingMoves);
        for(Move move : kingMoves){

            // Verifica se la mossa è dentro i limiti della scacchiera
            if(move.position() > 63 || move.position() < 0) continue;


            // Verifica se la casella è occupata da un pezzo dello stesso colore
            if(!matrix.get(move.position()).getPiece().getChessType().equals(ChessType.BLANK) ){
                // System.out.println("problema metodo searchCheckSquare");
                return false;
            }

            // Verifica se un pezzo avversario può dare scacco alla posizione del re
            if(!helperSearchCheckSquare(move, players.get(getOppositeColor()).getPieces())) return false;
        }

        // Il re può muoversi senza entrare in scacco
        return true;
    }

    /**
     * Metodo di supporto per verificare se una casella è sotto scacco.
     * @param end la mossa effettuata dal re
     * @param piecesPlayer lista dei pezzi del giocatore avversario
     * @return true se la casella non è sotto scacco, altrimenti false
     */
    private boolean helperSearchCheckSquare(Move end, ArrayList<ChessButton> piecesPlayer){
        for(ChessButton button : piecesPlayer){
            // Verifica le mosse di ogni pezzo del giocatore avversario
            out : for(Move inner : button.getAllPossibleMoves()){
                if(inner.equals(end)){

                    // Verifica se non ci sono pezzi sulla linea tra il pezzo e il re
                    for(Move check : button.getStepForThisMove(inner)){

                        //Controllo se un qualsiasi pezzo si interpone tra button e il king
                        if(!matrix.get(check.position()).getPiece().getChessType().equals(ChessType.BLANK)){
                            break out;
                        }
                    }

                    // Se un pezzo può dare scacco, ritorna false
                    return false;
                }
            }
        }
        return true; // Se nessun pezzo dà scacco
    }


    /**
     * Metodo che gestisce lo spostamento dei pezzi nella scacchiera.
     * Gestisce anche le situazioni di arrocco e promozione dei pedoni.
     * @param first il primo ChessButton (pezzo da spostare)
     * @param second il secondo ChessButton (destinazione)
     */
    private void movePiece(ChessButton first, ChessButton second){

        // Arrocco corto
        if(moveType == MoveType.SHORT_CASTLE){

            //Muovo il re
            helperMovePiece(matrix.get(first.position()), second);

            //Muovo la torre
            helperMovePiece(matrix.get(first.position()+1), matrix.get(first.position()-1));
            return;
        }

        // Arrocco lungo
        if(moveType == MoveType.LONG_CASTLE){
            //Muovo il re
            helperMovePiece(matrix.get(first.position()), second);

            //Muovo la torre
            helperMovePiece(matrix.get(first.position()-2), matrix.get(first.position()+1));

            return;
        }

        //sposto il re (se mosso)
        if(first instanceof King){
            players.get(turno).king = new Move(second.getRow(), second.getCol(), MoveType.NORMAL);
        }

        // Esegue la mossa
        helperMovePiece(first, second);
    }

    /**
     * Metodo di debug per stampare la matrice della scacchiera.
     */
    public void printMatrix(){
        int count = 0;
        int row = 1;
        System.out.println("Riga 1 -------------------");
        for(ChessButton b : matrix){
            if(count == 8){
                row++;
                count = 0;
                System.out.println("riga: " + row +"-------------------");
            }
            System.out.println(b.getPiece());
            count++;
        }
    }

    /**
     * Metodo di supporto che effettua lo spostamento fisico dei pezzi sulla scacchiera.
     * Gestisce anche le situazioni speciali come la promozione dei pedoni e en passant.
     * @param first il primo ChessButton (pezzo da spostare)
     * @param second il secondo ChessButton (destinazione)
     */
    private void helperMovePiece(ChessButton first, ChessButton second){
        int oldP = first.position();
        int rowP = first.getRow();
        int colP = first.getCol();
        int oldChessColor = first.getPiece().getChessColor();

        Color oldC = first.getBackgroundColor();

        boolean isPromote = false;
        JLabel lblResult = new JLabel();

        // Verifica se il pedone ha fatto il primo passo di due case
        if(first instanceof Pawn){
            if(Math.abs(first.getRow() - second.getRow()) == 2){
                ((Pawn) first).setWhenFirstTwoStep(movesMatch.size()+1);
            }

            // Gestisce la promozione del pedone
            if(((Pawn) first).isOnPromote()){
                isPromote = true;
                moveType = MoveType.PROMOTE;
                MyPopup popup = new MyPopup(this, lblResult::setText, first.getPiece().getChessColor());
                popup.setVisible(true);
            }
        }

        //Se sto spostando il re, devo cambiare anche le sue coordinate memorizzate dentro a player
        if(first instanceof King){
            players.get(turno).king = new Move(second.getRow(), second.getCol(), MoveType.NORMAL);
        }

        // Gestisce la cattura dei pezzi
        if(second.getPiece().getChessColor().equals(getOppositeColor())){
            moveType = MoveType.CAPTURE;
            players.get(getOppositeColor()).removePiece(second);

            //Aggiungo il pezzo catturato
            players.get(turno).getPieceEaten().add(second);

            //modifico GUI
            ImageIcon icon = new ImageIcon(new ImageIcon(pieceToImg.get(second.getPiece())).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
            JLabel t = new JLabel();
            t.setIcon(icon);

            if(turno == ChessColor.WHITE){
                iconListPanel1.add(t);
                //System.out.println("VALORE 1 " + (players.get(turno).getValuePieceEaten() - players.get(getOppositeColor()).getValuePieceEaten()));
            }else{
                iconListPanel2.add(t);
                //System.out.println("VALORE 2 " + (players.get(turno).getValuePieceEaten() - players.get(getOppositeColor()).getValuePieceEaten()));
            }
        }

        // Mossa pedone en passant
        if(moveType == MoveType.ENPASSANT)
            players.get(getOppositeColor()).removePiece(matrix.get(movesMatch.get(movesMatch.size()-1).position()));

        //sto promuovendo, devo rimuovere il pedone
        if(isPromote) players.get(turno).removePiece(first);

        else players.get(turno).modifyPiece(first, second);

        // Gestisce la promozione del pedone
        if(isPromote){
            int piece = Integer.parseInt(lblResult.getText());

            first = createChessButton(new Pair(piece, oldChessColor), oldC , second.getRow(), second.getCol());
            players.get(turno).addPiece(first);
        }

        // Esegue il movimento effettivo nella matrice
        first.move(second);
        first.setAlreadyMoved();

        matrix.set(second.position(), first);
        matrix.set(oldP, new Blank(new Pair(-1,-1), oldC, rowP, colP));

        ChessButton b = null;
        if(moveType == MoveType.ENPASSANT){
            b = matrix.get(movesMatch.get(movesMatch.size()-1).position());
            matrix.set(b.position(), new Blank(new Pair(-1,-1), b.getBackgroundColor(), b.getRow(), b.getCol()));
        }

        movesMatch.add(new Move(second.getRow(), second.getCol(), moveType));

        // Aggiorna la GUI
        addMoveToLabel(new Move(second.getRow(), second.getCol(), moveType), first);
        boardPanel.remove(oldP);
        boardPanel.add(createChessButton(new Pair(-1,-1),oldC, rowP, colP), oldP);
        boardPanel.remove(second.position());
        boardPanel.add(createChessButton(first.getPiece(), second.getBackgroundColor(), first.getRow(), first.getCol()),second.position());

        //Se si è verificato una situazione di ENPASSANT, devo rimuovere il pedone mangiato
        if(b != null){
            boardPanel.remove(b.position());
            boardPanel.add(createChessButton(new Pair(-1,-1),b.getBackgroundColor(), b.getRow(), b.getCol()),b.position());
        }
        boardPanel.revalidate();
        boardPanel.repaint();
    }

    /**
     * Metodo che gestisce i suoni per ogni evento sulla scacchiera.
     * Viene eseguito un suono specifico in base al tipo di mossa effettuata.
     * @param moveType tipo di mossa effettuata (ad esempio, mossa normale, cattura, arrocco, ecc.)
     */
    private void playSound(int moveType){
        // Suono associato al tipo di mossa
        try{
            // Carica il suono corrispondente al tipo di mossa
            audioInputStream = AudioSystem.getAudioInputStream(new File(moveTypeToAudio.get(moveType)));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        }catch(Exception e){
            e.printStackTrace(); // Stampa l'errore se il suono non viene caricato correttamente
        }
    }


    static class InvalidFormatFenException extends Exception{}
    static class InvalidPlayerFenException extends Exception{}
    static class InvalidFenType extends Exception{}

    /**
     * Metodo che ricostruisce la scacchiera a partire da una notazione FEN.
     * La notazione FEN viene analizzata per ottenere la posizione iniziale dei pezzi sulla scacchiera.
     * @param fen la notazione FEN che descrive lo stato della partita
     * @return una lista di oggetti Pair che rappresentano la scacchiera
     * @throws InvalidFormatFenException se il formato della notazione FEN non è valido
     * @throws InvalidPlayerFenException se non è stato specificato il turno del giocatore
     * @throws InvalidFenType se viene rilevato un tipo di pezzo non valido nella FEN
     */
    private ArrayList<Pair> reBuildChessBoardFromFen(String fen) throws InvalidFormatFenException, InvalidPlayerFenException, InvalidFenType {
        ArrayList<Pair> res = new ArrayList<>();

        // Splitta la notazione FEN in componenti (scacchiera e turno)
        String[] fenSplitted = fen.split(" ");
        String[] inizialBoard = fenSplitted[0].split("/");

        // Se non ci sono 8 righe la fen é mal formata
        if (inizialBoard.length != 8) throw new InvalidFormatFenException();

        for (String s : inizialBoard) {
            // Converte la riga in un array di caratteri
            String[] inner = s.split("");
            int count = 0;


            for (String value : inner) {
                // Se il carattere è una lettera minuscola (pezzo nero)
                if (value.matches("[a-z]+")) {
                    res.add(new Pair(convertStringToTypeLower(value), ChessColor.BLACK));
                    count += 1;
                }
                // Se il carattere è una lettera maiuscola (pezzo bianco)
                else if (value.matches("[A-Z]+")) {
                    res.add(new Pair(convertStringToTypeUpper(value), ChessColor.WHITE));
                    count += 1;
                }
                // Se il carattere è un numero (indica il numero di spazi vuoti)
                else if (value.matches("[0-9]+")) {
                    int n_blank = Integer.parseInt(value);
                    for (int k = 0; k < n_blank; k++) {
                        res.add(new Pair(ChessType.BLANK, ChessColor.BLANK)); // Spazio vuoto
                    }
                    count += n_blank;
                }
            }

            // Se la riga non ha esattamente 8 elementi, la FEN è mal formata
            if (count != 8)  throw new InvalidFormatFenException();

        }

        // Controlla se la parte che indica il turno del giocatore è presente
        if(fenSplitted[1].isEmpty()) throw new InvalidPlayerFenException();

        // Determina il colore del giocatore che ha il turno, basato sulla lettera "w" (bianco) o "b" (nero)
        turno = (fenSplitted[1].equals("w")) ? ChessColor.WHITE : ChessColor.BLACK;

        return res; // Restituisce la lista dei pezzi ricostruiti
    }

    /**
     * Metodo che costruisce una stringa FEN a partire dalla scacchiera attuale.
     * La scacchiera è rappresentata come una matrice di pezzi e la stringa FEN viene generata per descrivere
     * la posizione attuale dei pezzi sulla scacchiera.
     * @return la notazione FEN che rappresenta lo stato della scacchiera
     */
    private String buildFenFromChessBoard(){
        int count = 0;
        StringBuilder sb = new StringBuilder();

        // Converte la matrice della scacchiera in formato FEN
        for (ChessButton c : matrix) {
            switch (c.getPiece().getChessType()) {
                case ChessType.TOWER -> sb.append((c.getPiece().getChessColor().equals(ChessColor.BLACK)) ? "r" : "R");
                case ChessType.BISHOP -> sb.append((c.getPiece().getChessColor().equals(ChessColor.BLACK)) ? "b" : "B");
                case ChessType.KNIGHT -> sb.append((c.getPiece().getChessColor().equals(ChessColor.BLACK)) ? "n" : "N");
                case ChessType.QUEEN -> sb.append((c.getPiece().getChessColor().equals(ChessColor.BLACK)) ? "q" : "Q");
                case ChessType.KING -> sb.append((c.getPiece().getChessColor().equals(ChessColor.BLACK)) ? "k" : "K");
                case ChessType.PAWN -> sb.append((c.getPiece().getChessColor().equals(ChessColor.BLACK)) ? "p" : "P");
                case ChessType.BLANK -> sb.append("1"); // Rappresenta uno spazio vuoto
            }

            count ++;
            if (count == 8) {
                sb.append("/"); // Fine della riga della scacchiera
                count = 0;
            }

        }

        // Rimuove l'ultima barra ("/")
        sb.deleteCharAt(sb.length()-1);

        String res = sb.toString();
        sb = new StringBuilder();
        count = 0;

        // Compatta la notazione FEN rimuovendo gli spazi vuoti consecutivi
        for(int i = 0; i < res.length(); i++){

            if(res.charAt(i) != '1'){

                // Aggiunge il numero di spazi vuoti consecutivi
                if(count > 0) sb.append(count);

                sb.append(res.charAt(i));

                count = 0;
            }

            else if(res.charAt(i) == '1') count ++; // Conta gli spazi vuoti
        }

        if(count > 0) sb.append(count);  // Aggiunge gli spazi vuoti finali

        // Aggiunge il turno
        sb.append(turno == ChessColor.WHITE ? " w" : " b");

        return sb.toString();  // Restituisce la notazione FEN finale
    }

    /**
     * Metodo di supporto per la verifica se una mossa è valida rispetto al "pinned piece" (pezzo intrappolato).
     * Esamina se un pezzo che si sta spostando è ancora sotto il controllo di un altro pezzo.
     * @param copy matrice della scacchiera
     * @param active il pezzo che sta "pinnando"
     * @param first il pezzo che sta cercando di muoversi
     * @param second la posizione di destinazione del pezzo
     * @return true se la mossa è valida, false altrimenti
     */
    private boolean search(ArrayList<ChessButton> copy, ChessButton active, ChessButton first, ChessButton second){

        int oldP = first.position();
        int rowP = first.getRow();
        int colP = first.getCol();
        Color oldC = first.getBackgroundColor();
        ChessButton firstCopy = copyChessButton(first);

        // Esegui il movimento del pezzo
        firstCopy.move(second);

        // Aggiorna la matrice con la nuova posizione del pezzo
        copy.set(second.position(), firstCopy);
        copy.set(oldP, new Blank(new Pair(-1,-1), oldC, rowP, colP));

        // Verifica se il pezzo è "pinned" (intrappolato) da un altro pezzo
        if(second.equals(active)) return true;


        //Verifico se first è ancora pinnato da active. Se questo è vero la mossa è valida altrimenti ritorno false
        out : for(Move m : active.getAllPossibleMoves()){

            //player.king: stesso colore del turno
            if(m.equals(players.get(turno).king)){
                //System.out.println("ho trovato uno scacco: " + m + ", piece " + p.piece);
                //Verifico che ci posso arrivare

                ChessButton pinnedPiece = null;

                for(Move inner : active.getStepForThisMove(players.get(turno).king)){
                    if(!copy.get(inner.position()).getPiece().getChessColor().equals(ChessColor.BLANK)){
                        if(pinnedPiece != null){
                            break out;
                        }

                        pinnedPiece = copy.get(inner.position());
                    }
                }

                if(pinnedPiece != null){
                    return true;  // La mossa è valida
                }
            }
        }

        // La mossa non è valida
        return false;
    }

    /**
     * Metodo di supporto per reBuildChessBoardFromFen.
     * Converte una stringa rappresentante un tipo di pezzo in minuscolo (FEN) in un tipo di pezzo interno.
     * @param s la stringa del tipo di pezzo in minuscolo
     * @return il tipo di pezzo corrispondente
     * @throws InvalidFenType se il tipo di pezzo non è valido
     */
    private static int convertStringToTypeLower(String s) throws InvalidFenType {
        switch (s){
            case "r" ->{return ChessType.TOWER;}
            case "n" ->{return ChessType.KNIGHT;}
            case "b" ->{return ChessType.BISHOP;}
            case "q" ->{return ChessType.QUEEN;}
            case "k" ->{return ChessType.KING;}
            case "p" ->{return ChessType.PAWN;}
            default -> throw new InvalidFenType(); // Se il tipo di pezzo non è valido

        }
    }

    /**
     * Metodo di supporto per reBuildChessBoardFromFen.
     * Converte una stringa rappresentante un tipo di pezzo in maiuscolo (FEN) in un tipo di pezzo interno.
     * @param s la stringa del tipo di pezzo in maiuscolo
     * @return il tipo di pezzo corrispondente
     * @throws InvalidFenType se il tipo di pezzo non è valido
     */
    private static int convertStringToTypeUpper(String s) throws InvalidFenType {
        switch (s){
            case "R" ->{return ChessType.TOWER;}
            case "N" ->{return ChessType.KNIGHT;}
            case "B" ->{return ChessType.BISHOP;}
            case "Q" ->{return ChessType.QUEEN;}
            case "K" ->{return ChessType.KING;}
            case "P" ->{return ChessType.PAWN;}
            //blank
            default -> throw new InvalidFenType(); // Se il tipo di pezzo non è valido
        }
    }
}
