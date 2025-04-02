package utils.server;

/**
 * Classe che contiene le configurazioni iniziali per l'IP e la porta del server.
 * Viene utilizzata per inizializzare il socket del server per la comunicazione di rete.
 */
public class ServerSocketInit {

    /**
     * L'indirizzo IP del server.
     * 
     * L'indirizzo IP può essere configurato in diversi modi a seconda del tipo di connessione:
     *     Indirizzo IP locale (LAN): Utilizza l'indirizzo 127.0.0.1 o 'localhost' per la comunicazione solo tra dispositivi sullo stesso computer.
     *     Indirizzo IP nella LAN: Un indirizzo IP privato come 192.168.x.x o 10.x.x.x, che permette la comunicazione tra dispositivi all'interno della stessa rete locale.
     *     Indirizzo IP pubblico (Internet): Un indirizzo IP globale, che permette la comunicazione da e verso dispositivi su Internet. L'IP pubblico viene assegnato dal provider di servizi Internet (ISP).
     *     
     * Nota: "Da Modificare" è un valore di esempio, bisogna sostituirlo con un indirizzo IP valido a seconda della configurazione di rete.
     */
    public static final String IP_ADDRESS = "Da Modificare";

    /**
     * La porta su cui il server ascolterà le connessioni in entrata.
     * 
     * I numeri di porta validi vanno da 0 a 65535, suddivisi in tre categorie:
     *     Porta Well-Known (0-1023): Queste porte sono riservate per protocolli standard (ad esempio, la porta 80 per HTTP, la porta 443 per HTTPS).
     *     Porta Registrata (1024-49151): Queste porte possono essere utilizzate per applicazioni o servizi che richiedono porte non standard. La porta 25565, ad esempio, è comunemente usata per server di Minecraft.
     *     Porta Dinamica o Privata (49152-65535): Queste porte sono generalmente utilizzate per connessioni temporanee e per applicazioni client-server personalizzate.
     * 
     * Nota: Assicurarsi che la porta scelta sia aperta nel firewall del server e che non venga utilizzata da altre applicazioni sullo stesso dispositivo.
     * Nota: -1 è un valore di esempio, bisogna sostituirlo con un numero di porta valido a seconda della configurazione.
     */
    public static final int PORT = -1;
}

