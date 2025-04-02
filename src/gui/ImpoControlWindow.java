package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Finestra di dialogo che offre all'utente due opzioni: riprendere il gioco o uscire.
 * Questa finestra viene utilizzata come menu delle impostazioni e fornisce un'interfaccia
 * minimale con due pulsanti chiaramente distinguibili per facilitare la navigazione.
 */
public class ImpoControlWindow  extends JFrame {
    private static final Color RESUME_COLOR = new Color(34, 139, 34);  // Verde per il "Resume"
    private static final Color QUIT_COLOR = new Color(255, 69, 0);    // Rosso per il "Quit Game"
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);

    /**
     * Inizializza la finestra del menu impostazioni con due opzioni:
     * - "Riprendi" per chiudere la finestra e tornare al gioco.
     * - "Esci dalla partita" per terminare l'applicazione.
     * La finestra è impostata con un layout a griglia per una disposizione verticale dei pulsanti.
     */
    public ImpoControlWindow() {
        setTitle("Menu Impostazioni");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Aggiunge i pulsanti e il layout direttamente
        setLayout(new GridLayout(2, 1, 10, 10));  // Layout a griglia con 2 righe

        // Crea e aggiungi i pulsanti
        add(createButton("Riprendi", RESUME_COLOR, e -> dispose()));  // Chiude il menu delle impostazioni
        add(createButton("Esci dalla partita", QUIT_COLOR, e -> System.exit(0)));  // Termina il programma

        // Rende visibile la finestra
        setVisible(true);
    }

    /**
     * Crea un pulsante con stile personalizzato.
     * Il pulsante avrà un colore di sfondo specifico, testo bianco, bordo nero
     * e non mostrerà il focus quando selezionato.
     *
     * @param text     Il testo visualizzato sul pulsante.
     * @param color    Il colore di sfondo del pulsante.
     * @param listener L'ActionListener che gestisce il comportamento del pulsante quando premuto.
     * @return         Un'istanza di JButton configurata con le proprietà specificate.
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
