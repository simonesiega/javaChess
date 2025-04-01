package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Finestra separata con i bottoni "Resume" e "Quit Game" per il menu delle impostazioni.
 */
public class ImpoControlWindow  extends JFrame {
    private static final Color RESUME_COLOR = new Color(34, 139, 34);  // Verde per il "Resume"
    private static final Color QUIT_COLOR = new Color(255, 69, 0);    // Rosso per il "Quit Game"
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);

    /**
     * Crea una finestra con i bottoni per riprendere il gioco o uscire.
     */
    public ImpoControlWindow() {
        setTitle("Menu Impostazioni");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Aggiungi i pulsanti e il layout direttamente
        setLayout(new GridLayout(2, 1, 10, 10));  // Layout a griglia con 2 righe

        // Crea e aggiungi i pulsanti
        add(createButton("Riprendi", RESUME_COLOR, e -> dispose()));  // Chiude il menu delle impostazioni
        add(createButton("Esci dalla partita", QUIT_COLOR, e -> System.exit(0)));  // Termina il programma

        // Rende visibile la finestra
        setVisible(true);
    }

    /**
     * Crea e personalizza un pulsante.
     * @param text Il testo del pulsante.
     * @param color Il colore di sfondo del pulsante.
     * @param listener L'ascoltatore dell'evento del pulsante.
     * @return Il pulsante creato e personalizzato.
     */
    private JButton createButton(String text, Color color, ActionListener listener) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        button.addActionListener(listener);
        return button;
    }
}
