package gui;

import player.Player;
import utils.Move;
import utils.constant.MoveType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Classe che rappresenta il menu principale del gioco di scacchi.
 */
public class Menu extends JFrame {

    /**
     * Costruttore della classe Menu. Inizializza e configura l'interfaccia grafica del menu.
     */
    public Menu() {
        setTitle("Scacchi");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        BackgroundPanel backgroundPanel = new BackgroundPanel("src/resources/images/bg_menu.jpg");
        backgroundPanel.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("SCACCHI", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(120, 0, 0));
        titleLabel.setPreferredSize(new Dimension(350, 60));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonPanel.setOpaque(false);

        JButton btnLocalGame = createStyledButton("Partita Locale (2 Giocatori)", new Color(180, 0, 0));
        JButton btnFenGame = createStyledButton("Partita Locale con FEN", new Color(180, 0, 0));
        JButton btnOnlineGame = createStyledButton("Partita Online con un Amico", new Color(180, 0, 0));

        btnLocalGame.addActionListener(e -> iniziaGiocoNormale());
        btnFenGame.addActionListener(e -> iniziaConFEN());
        btnOnlineGame.addActionListener(e -> iniziaGiocoOnline());

        buttonPanel.add(btnLocalGame);
        buttonPanel.add(btnFenGame);
        buttonPanel.add(btnOnlineGame);

        JPanel container = new JPanel(new GridBagLayout());
        container.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 0, 10, 0);
        container.add(titlePanel, gbc);
        gbc.gridy = 1;
        container.add(buttonPanel, gbc);

        mainPanel.add(container, BorderLayout.CENTER);
        backgroundPanel.add(mainPanel, BorderLayout.CENTER);

        setContentPane(backgroundPanel);
        setVisible(true);
    }

    /**
     * Crea e restituisce un JButton stilizzato.
     * @param text      Testo del pulsante.
     * @param baseColor Colore di base del pulsante.
     * @return JButton personalizzato.
     */
    private JButton createStyledButton(String text, Color baseColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 22));
        button.setBackground(baseColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(baseColor.darker());
            }
            @Override
            public void mouseExited(MouseEvent evt) {
                button.setBackground(baseColor);
            }
        });
        return button;
    }

    /**
     * Classe interna per la gestione dello sfondo personalizzato.
     */
    static class BackgroundPanel extends JPanel {
        private final Image backgroundImage;

        /**
         * Costruttore che carica l'immagine di sfondo.
         * @param filePath Percorso del file immagine.
         */
        public BackgroundPanel(String filePath) {
            backgroundImage = new ImageIcon(filePath).getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    /**
     * Mostra una finestra di dialogo per l'inserimento di dati.
     * @param messaggio Testo da visualizzare nella finestra di dialogo.
     * @return Il valore inserito dall'utente.
     */
    private String chiediInput(String messaggio) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JLabel label = new JLabel(messaggio);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        JTextField textField = new JTextField(15);
        textField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.requestFocusInWindow();
        panel.add(label, BorderLayout.NORTH);
        panel.add(textField, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(this, panel, "Inserisci Dati", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        return (result == JOptionPane.OK_OPTION) ? textField.getText().trim() : "";
    }

    /**
     * Avvia una partita online con un amico.
     */
    private void iniziaGiocoOnline() {
        this.dispose();
        Player player1 = new Player(new Move(0, 0, MoveType.NORMAL), "");
        Player player2 = new Player(new Move(0, 0, MoveType.NORMAL), "");
        try {
            Window w = new Window("Partita Online", player1, player2, null, false);
            w.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Avvia una partita locale con due giocatori.
     */
    private void iniziaGiocoNormale() {
        this.dispose();
        String nome1 = chiediInput("Nome Giocatore 1:");
        String nome2 = chiediInput("Nome Giocatore 2:");
        Player player1 = new Player(new Move(0, 0, MoveType.NORMAL), nome1);
        Player player2 = new Player(new Move(0, 0, MoveType.NORMAL), nome2);
        try {
            gui.Window w = new gui.Window("Partita normale", player1, player2, null, true);
            w.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Avvia una partita locale con una posizione FEN specificata.
     */
    private void iniziaConFEN() {
        this.dispose();
        String nome1 = chiediInput("Nome Giocatore 1:");
        String nome2 = chiediInput("Nome Giocatore 2:");
        String fen = chiediInput("Inserisci la FEN:");
        Player player1 = new Player(new Move(0, 0, MoveType.NORMAL), nome1);
        Player player2 = new Player(new Move(0, 0, MoveType.NORMAL), nome2);
        System.out.println(fen);
        try {
            gui.Window w = new Window("Partita con FEN modificata", player1, player2, fen, true);
            w.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}