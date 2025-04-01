package gui;

import utils.Pair;
import utils.constant.ChessType;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import static utils.constant.ChessImg.*;

/**
 * Interfaccia per gestire il risultato selezionato dal popup.
 */
interface PopupResultListener {
    /**
     * Metodo chiamato quando un risultato viene selezionato.
     * @param result Il valore selezionato come stringa.
     */
    void onResult(String result);
}

/**
 * Classe MyPopup che rappresenta un popup per la selezione della promozione di un pedone.
 */
class MyPopup extends JDialog {
    private final int width = 300;
    private final int height = 150;
    private final ArrayList<Integer> values = new ArrayList<>();

    /**
     * Costruttore della classe MyPopup.
     * @param parent Finestra principale che richiama il popup.
     * @param listener Listener per restituire il valore selezionato.
     * @param color Colore del pezzo selezionato.
     */
    public MyPopup(JFrame parent, PopupResultListener listener, int color) {
        super(parent, "Popup", true);
        this.setSize(width, height);
        this.setLocationRelativeTo(null);

        // Inizializzo array con i tipi di pezzi disponibili per la promozione
        values.add(ChessType.QUEEN);
        values.add(ChessType.TOWER);
        values.add(ChessType.KNIGHT);
        values.add(ChessType.BISHOP);

        // Layout con quattro bottoni
        this.setLayout(new GridLayout(1, 4));

        for (int j = 0; j < 4; j++) {
            JButton button = new JButton();
            int finalJ = j;
            button.addActionListener(e -> {
                if (listener != null) {
                    listener.onResult(String.valueOf(values.get(finalJ)));
                }
                dispose();
            });

            setImageChessButton(button, 50, 50, color, values.get(j));
            this.add(button);
        }

        this.setLocationRelativeTo(parent);
    }

    /**
     * Imposta l'immagine di un pulsante con l'icona del pezzo corrispondente.
     * @param button Il pulsante su cui impostare l'icona.
     * @param buttonWidth Larghezza dell'icona.
     * @param buttonHeight Altezza dell'icona.
     * @param color Colore del pezzo.
     * @param type Tipo del pezzo.
     */
    private void setImageChessButton(JButton button, int buttonWidth, int buttonHeight, int color, int type) {
        ImageIcon img = new ImageIcon(pieceToImg.get(new Pair(type, color)));
        Image scaledImage = img.getImage().getScaledInstance(
                buttonWidth, buttonHeight, Image.SCALE_SMOOTH);
        button.setIcon(new ImageIcon(scaledImage));
    }
}
