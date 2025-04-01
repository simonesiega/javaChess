package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * La finestra che mostra il risultato del gioco, inclusi i nomi dei giocatori, il motivo della fine della partita,
 * e un pulsante per giocare di nuovo.
 */
public class ResultWindow extends JFrame {
    private static final Color WIN_COLOR = new Color(60, 179, 113);
    private static final Color LOSE_COLOR = new Color(255, 99, 71);
    private static final Color DRAW_COLOR = new Color(255, 165, 0);
    private static final Font TEXT_FONT = new Font("Arial", Font.BOLD, 18);
    private static final Font ICON_FONT = getCompatibleFont(); // Font per le icone
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);

    private Image backgroundImage;

    /**
     * Costruttore che crea la finestra con il risultato della partita.
     *
     * @param winner Il vincitore della partita (0 per nessuno, se è una patta)
     * @param loser Il perdente della partita (1 per il secondo giocatore)
     * @param winnerName Nome del vincitore
     * @param loserName Nome del perdente
     * @param reason Motivo per cui la partita è finita
     */
    public ResultWindow(Integer winner, Integer loser, String winnerName, String loserName, String reason) {
        try {
            // Carica l'immagine di sfondo
            backgroundImage = ImageIO.read(new File("src/resources/images/bg_menu.jpg"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Risultato del Gioco");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));

        // Pannello principale con layout GridBag
        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        if (winner == null && loser == null) { // Patta
            gbc.gridy = 0;
            gbc.weighty = 1;
            mainPanel.add(createPlayerPanel("     ⚖️", "Partita Patta", DRAW_COLOR), gbc);

            gbc.gridy = 1;
            gbc.weighty = 0;  // Reset weight
            mainPanel.add(createReasonPanel(reason), gbc);
        } else {
            gbc.gridy = 0;
            mainPanel.add(createPlayerPanel("🏆", winnerName, WIN_COLOR), gbc);

            gbc.gridy = 1;
            mainPanel.add(createReasonPanel(reason), gbc);

            gbc.gridy = 2;
            mainPanel.add(createPlayerPanel("🥈", loserName, LOSE_COLOR), gbc);
        }

        add(mainPanel, BorderLayout.CENTER);

        // Bottone per giocare ancora
        JButton playAgainButton = createPlayAgainButton(e -> {
            new Menu().setVisible(true);
            dispose();
        });

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcButton = new GridBagConstraints();
        gbcButton.insets = new Insets(10, 0, 10, 0);

        if (winner == null && loser == null) {
            // Centra il bottone in caso di patta
            gbcButton.gridy = 2;
            gbcButton.weighty = 1;
            gbcButton.anchor = GridBagConstraints.CENTER;
        } else {
            // Posiziona il bottone in basso nei casi normali
            gbcButton.gridy = 3;
            gbcButton.anchor = GridBagConstraints.PAGE_END;
        }

        buttonPanel.add(playAgainButton, gbcButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    /**
     * Crea un pannello che mostra un'icona (unicode) e il nome di un giocatore.
     *
     * @param unicodeIcon Il carattere Unicode dell'icona
     * @param name Il nome del giocatore
     * @param color Il colore di sfondo del pannello
     * @return Il pannello creato
     */
    private JPanel createPlayerPanel(String unicodeIcon, String name, Color color) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(color);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel iconLabel = new JLabel(unicodeIcon, SwingConstants.CENTER);
        iconLabel.setFont(ICON_FONT);
        iconLabel.setForeground(Color.WHITE);
        panel.add(iconLabel, gbc);

        gbc.gridy = 1;
        JLabel textLabel = new JLabel(name, SwingConstants.CENTER);
        textLabel.setFont(TEXT_FONT);
        textLabel.setForeground(Color.WHITE);
        panel.add(textLabel, gbc);

        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
        panel.setPreferredSize(new Dimension(400, 100));
        return panel;
    }

    /**
     * Crea un pannello che mostra il motivo della fine della partita.
     *
     * @param reason Il motivo della fine della partita
     * @return Il pannello creato
     */
    private JPanel createReasonPanel(String reason) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(1, 32, 64)); // Colore solido senza trasparenza
        panel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        JLabel reasonLabel = new JLabel("Motivo: " + reason, SwingConstants.CENTER);
        reasonLabel.setFont(new Font("Arial", Font.BOLD, 16));
        reasonLabel.setForeground(Color.WHITE);

        panel.add(reasonLabel);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
        panel.setPreferredSize(new Dimension(400, 50));
        return panel;
    }

    /**
     * Crea il bottone per giocare ancora.
     *
     * @param listener L'ascoltatore che gestisce l'azione del bottone
     * @return Il bottone creato
     */
    private JButton createPlayAgainButton(ActionListener listener) {
        JButton button = new JButton("Gioca ancora");
        button.setFont(BUTTON_FONT);
        button.setBackground(new Color(80, 150, 230));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 130, 210));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(80, 150, 230));
            }
        });

        button.addActionListener(listener);
        return button;
    }

    /**
     * Trova un font compatibile per le icone Unicode.
     * @return Il font compatibile
     */
    private static Font getCompatibleFont() {
        String[] preferredFonts = {"Segoe UI Emoji", "Apple Color Emoji", "Arial"};
        for (String fontName : preferredFonts) {
            Font font = new Font(fontName, Font.PLAIN, 30);
            if (font.canDisplayUpTo("🏆") == -1) {
                return font;
            }
        }
        return new Font("Arial", Font.PLAIN, 30);
    }
}
