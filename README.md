# Java Chess Game

Un'applicazione di scacchi sviluppata in Java che supporta sia modalità di gioco offline che online. Il progetto offre funzionalità per giocare contro un avversario anche la possibilità di caricare posizioni personalizzate tramite la notazione FEN. Le mosse vengono salvate in formato CSV, permettendo di rivedere e rigiocare le partite.

## Caratteristiche principali

- **Gioco offline**: Gioca contro un altro giocatore sulla stessa macchina. La partita viene salvata in un file CSV con la notazione FEN per ogni mossa.
- **Gioco online**: Gioca contro un altro giocatore su una rete locale tramite un server. Le mosse vengono sincronizzate tra i dispositivi, e ogni partita è salvata in un file CSV.
- **Salvataggio delle partite**: Ogni mossa è salvata in tempo reale in un file CSV, che può essere utilizzato per rivedere o continuare la partita in un secondo momento.
- **Notazione FEN**: Supporta la possibilità di iniziare la partita da una configurazione personalizzata usando la notazione FEN.
- **Timer**: Ogni giocatore ha un timer che conta il tempo di gioco rimanente.
- **Supporto per diverse modalità di gioco**: Classica, con FEN personalizzata, e multiplayer online.

## Come avviare il gioco

### Requisiti

- Java 8 o superiore.
- Un editor di testo o IDE per modificare il codice (ad esempio, IntelliJ IDEA, Eclipse, etc.).

### Modalità Offline

1. Avvia il gioco in modalità offline passando il nome dei due giocatori e la notazione FEN (se desideri usare una configurazione personalizzata).
2. I giocatori possono muovere i pezzi sulla stessa macchina.
3. Le mosse vengono registrate in un file `.csv` per ogni partita.

### Modalità Online

1. Avvia il gioco in modalità online con due dispositivi connessi ad uno stesso server.
2. La connessione viene stabilita tra un client e un server.
3. Le mosse vengono inviate e ricevute in tempo reale tra i due dispositivi.
4. Ogni partita online viene salvata in una directory specifica con un file `.csv` che memorizza le mosse.

## Struttura delle cartelle

Le partite vengono salvate in due directory principali: `offline` e `online`. 

- **offline**: Contiene lo storico delle partite giocate in modalità offline. Ogni partita viene salvata in un file CSV che utilizza la notazione FEN per memorizzare le mosse.
  
  Esempio di percorso:  
  `src/resources/matches/offline/player1-player2.csv`

- **online**: Contiene le partite giocate in modalità online. Ogni partita viene salvata in una sottocartella numerata, e ogni mossa viene registrata in un file CSV simile a quello offline.

  Esempio di percorso:  
  `src/resources/matches/online/match-1/player1-0.csv`

## Funzionamento dei timer

Ogni giocatore ha un timer che conta il tempo rimanente. Se il tempo di un giocatore scade, il gioco termina e l'altro giocatore viene dichiarato vincitore. Il timer può essere configurato in base alle preferenze.

## Modalità di gioco

1. **Modalità Classica**: Gioca una partita standard con la disposizione iniziale dei pezzi.
2. **Modalità con FEN personalizzata**: Inizia una partita con una configurazione della scacchiera personalizzata, usando una notazione FEN.
3. **Modalità Multiplayer online**: Gioca con un altro giocatore sulla stessa rete, con la possibilità di salvare la partita in un file CSV.

## Istruzioni per l'utente

1. Avvia il gioco selezionando la modalità (Offline o Online).
2. Se scegli la modalità offline, inserisci i nomi dei due giocatori.
3. Se scegli la modalità online, avvia il server e collega i due dispositivi.
4. Usa i tasti di movimento per muovere i pezzi sulla scacchiera.
5. Le mosse verranno registrate in un file `.csv`.

## Contribuire

Se vuoi contribuire al progetto, sei il benvenuto! Puoi fare una pull request con modifiche, miglioramenti o nuove funzionalità. Assicurati di seguire lo stile di codifica esistente e di testare le modifiche prima di inviarle.

## Licenza

Questo progetto è sotto licenza MIT. Vedi il file [LICENSE](LICENSE) per maggiori dettagli.
