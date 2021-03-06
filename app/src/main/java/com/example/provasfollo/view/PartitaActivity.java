package com.example.provasfollo.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.widget.TextView;
import java.util.Arrays;
import android.util.Log;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.text.InputFilter;
import android.widget.AdapterView;
import android.view.Window;
import android.view.WindowManager;

import com.example.provasfollo.controller.GiocatoreController;
import com.example.provasfollo.utility.IMsgBoxResponseCollector;
import com.example.provasfollo.utility.MathUtility;
import com.example.provasfollo.R;
import com.example.provasfollo.network.RemoteCallDispatcher;
import com.example.provasfollo.controller.StatoGiocoController;
import com.example.provasfollo.model.Carta;
import com.example.provasfollo.model.Giocatore;
import com.example.provasfollo.utility.MsgBoxClickListenerWithNotify;
import com.example.provasfollo.utility.MsgBoxOkCancel;
import com.example.provasfollo.utility.MsgBoxWithWaitOnUIThread;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import androidx.constraintlayout.widget.ConstraintLayout;

public class PartitaActivity extends AppCompatActivity {

    private final Object monitorTurno = new Object();


    // riferimento al singleton di tipo RemoteCallDispatcher (unica istanza in tutta l'app)
    RemoteCallDispatcher serverListener;
    GiocatoreController giocatoreController;
    StatoGiocoController statoController;


    private final static String TAG = "PartitaActivity";

    // TODO: vedere dove metterlo
    private final static String[] SCALA_CARTE_ASTA = { "asse", "tre", "re", "cavallo", "fante", "sette",
            "sei", "cinque", "quattro", "due"};

    private int ID_giocatoreLocale = -1;
    private final int NUM_MAX_CARTE_MANO = 8;
    private final int NUM_MAX_CARTE_TAVOLO = 5;


    private VistaGiocatore[] visteGiocatori = new VistaGiocatore[NUM_MAX_CARTE_TAVOLO];

    // rappresentazione grafica delle carte presenti in mano del giocatore
    private VistaCarta[] visteCarteMano = new VistaCarta[NUM_MAX_CARTE_MANO];

    // rappresentazione grafica delle carte giocate sul tavolo.
    // NB: è assunto che la carta giocata dal giocatore corrente sia con indice 0
    // private VistaCarta[] visteCarteTavolo = new VistaCarta[NUM_MAX_CARTE_TAVOLO];

    // indice in cui posizionare la prossima carta in mano (nella fase iniziale di distribuzione
    // delle carte)
    private int indiceNextCartaMano = 0;

    // indice della carta correntemente selezionata da giocare
    private int indiceCartaDaGiocare = -1;

    // indice della carta giocata dal giocatore (impostato alla fine del turno)
    private int indiceCartaGiocata = -1;

    // indice della carta chiamata durante l'asta (se -1 vuol dire che il giocatore ha lasciato)
    private int indiceCartaChiamata = -1;

    // seme della carta chiamata: viene scelto dal giocatore chiamante alla fine del primo turno
    private String semeCartaChiamata = "";

    // flag che permette di sapere se è attivo il turno del giocatore corrente
    private boolean isTurnoGiocatoreCorrente = false;

/*
    // evento che segnala che il giocatore (relativo a questo pc) ha giocato la carta, e di
    // conseguenza il suo turno è finito. Questo meccanismo permette di bloccare un thread di esecuzione
    // in attesa che si verifichi questo evento (vedi metodo "giocaCarta" di GiocatoreController)
    private ManualResetEvent eventoTurnoFinito = new ManualResetEvent(false);
*/

    // per sapere quando viene chiusa la finestra
    private boolean chiusa = false;

    // riferimenti veloci ai controlli nella activity (per evitare di fare semore findviewbyid)
    private Button btnGiocaCarta;
    private ConstraintLayout pannelloChiamata;
    private ConstraintLayout pannelloTavolo;
    private ConstraintLayout panelVincitoreTurno;
    private ConstraintLayout pannelloCarteMano;
    private ConstraintLayout panelSeme;
    private Button btnEffettuaChiamata;
    private Button btnLascia;
    //private EditText numPunteggioVittoria;
    private NumberPicker numPunteggioVittoria;
    private Spinner comboCartaChiamata;
    private Spinner comboSemeChiamata;
    private TextView txtBriscola;
    private TextView txtStatoTurno;


    private Handler handler;

    // TODO: spostare in statoGiocoController
    private String semeCartaChiamataFineAsta = null;
    private int indiceCartaChiamataFineAsta = -1;
    private int minPunteggioVittoria = -1;
    private int faseGioco = -1;

    private int ID_chiamante = -1;
    private int ID_socio = -1;

    private int ID_giocatoreGiocante = -1;


    private final String RICHIESTA_STATO = "GET_STATUS";
    private final String ACK_STATO = "ACK_STATUS";


    private int contResume = 0;
    private final static String[] FASI_GIOCO = {"none", "asta", "primoTurno", "sceltaSeme", "turni"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //Remove title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getSupportActionBar().hide();

        //Remove notification bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_partita);

        // ottengo i parametri provenienti dall'activity iniziale (nome, server, IP)
        Intent intent = getIntent();
        String nome = intent.getStringExtra(MainActivity.KEY_NOME);
        String IP = intent.getStringExtra(MainActivity.KEY_IP);
        String server = intent.getStringExtra(MainActivity.KEY_SERVER);


        // creo l'oggetto giocatore e il cotroller, e lo statocontroller
        Giocatore giocatore = new Giocatore(nome);
        giocatoreController = new GiocatoreController(this, giocatore);
        statoController = new StatoGiocoController(this, giocatoreController);


        // creo l'oggetto per la comunicazione in background con il server e gli aggiungo gli oggetti
        // a cui dovrà passare le chiamate remote
        serverListener = RemoteCallDispatcher.getInstance();
        serverListener.addOggettoRemoto(giocatoreController.NOME_CLASSE, giocatoreController);
        serverListener.addOggettoRemoto(statoController.NOME_CLASSE, statoController);


/*        // eseguo in bg
        Thread t = new Thread(serverListener);
        t.start();*/


        // imposto i riferimenti agli oggetti grafici della activity
        impostaOggettiGrafici();



        // carico e creo i vari pannelli
        caricaSlotManoGiocatore();
        caricaControlliChiamata();
        caricaSlotTavolo();
        visualizzaNuovaPartita(false);

        // all'inizio il bottone per giocare la carta è disabilitato
        btnGiocaCarta.setEnabled(false);

        txtStatoTurno.setText("Attesa giocatori...");

        handler = new Handler(this.getMainLooper());

        // eseguo in bg
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                getStatoDalServer();
            }
        });
        t.start();
    }



    private void getStatoDalServer() {

        serverListener.sendMessage(RICHIESTA_STATO);
        String json = null;

        // nel frattempo sta già ricevendo i ping dal server.. In questo caso il metodo .read()
        // restituisce null
        while(json == null){
            try{
                json = serverListener.read();
            }
            catch(Exception e){
                Log.d(TAG,e.toString());
            }
        }

        // faccio il parsing del json con lo stato corrente della partita
        if(json.contains("{")){
            String statoJson = json.substring(json.indexOf("{"));

            JsonElement jsonElement = new JsonParser().parse(statoJson);
            JsonObject jsonStato = jsonElement.getAsJsonObject();

            // scopro in quale fase del gioco siamo
            String faseGioco = jsonStato.get("faseGioco").getAsString();
            ID_giocatoreLocale = jsonStato.get("ID_giocatoreLocale").getAsInt();

            // ottengo i dati dei giocatori
            JsonArray giocatoriJsonElements = jsonStato.get("giocatori").getAsJsonArray();
            int numGiocatori = giocatoriJsonElements.size();

            // per ogni giocatore salvo l'eventuale carta giocata (se siamo oltre la fase dell'asta)
            Map<Integer, Carta> mapCartaGiocatore = new HashMap<Integer, Carta>();

            for(int i = 0; i < numGiocatori; i++){
                JsonObject jsonGiocatore = giocatoriJsonElements.get(i).getAsJsonObject();

                // ottengo i dati di questo giocatore
                int ID_giocatore = jsonGiocatore.get("ID").getAsInt();
                String nomeGiocatore = jsonGiocatore.get("nome").getAsString();

                // se è già finita l'asta devo segnare l'eventuale carta giocata da questo giocatore
                if(!(faseGioco.equalsIgnoreCase(getString(R.string.none)) ||
                        faseGioco.equalsIgnoreCase(getString(R.string.asta)))){

                    Carta cartaGiocatore = new Carta();

                    String cartaJson = jsonGiocatore.get("carta").toString();
                    if(!cartaJson.equalsIgnoreCase("\"none\"")){
                        cartaGiocatore.populateObjectFromJson(cartaJson);
                        mapCartaGiocatore.put(ID_giocatore, cartaGiocatore);
                    }
                }

                aggiungiGiocatore(ID_giocatore, nomeGiocatore);
            }

            // ottengo le carte della mano del giocatore locale
            JsonArray carteGiocatoreCorrente = jsonStato.get("carteGiocatoreCorrente").getAsJsonArray();
            int numCarteGiocatoreCorrente = carteGiocatoreCorrente.size();
            for(int i = 0; i < numCarteGiocatoreCorrente; i++){
                Carta c = new Carta();
                c.populateObjectFromJson(carteGiocatoreCorrente.get(i).toString());
                giocatoreController.aggiungiCartaMano(c);
            }

            // se è finita l'asta devo impostare il gicoatore chiamante
            if(!(faseGioco.equalsIgnoreCase(getString(R.string.none)) ||
                    faseGioco.equalsIgnoreCase(getString(R.string.asta)))){
                ID_chiamante = jsonStato.get("ID_chiamante").getAsInt();
            }

            // se è iniziato il 2o turno allora è stato scelto anche il seme e il socio
            if(faseGioco.equalsIgnoreCase(getString(R.string.turni))){
                semeCartaChiamataFineAsta = jsonStato.get("semeChiamata").getAsString();
                ID_socio = jsonStato.get("ID_socio").getAsInt();

                giocatoreController.setSemeBriscola(semeCartaChiamataFineAsta);
                setPanelBriscola(semeCartaChiamataFineAsta);
            }

            ID_giocatoreGiocante = jsonStato.get("ID_giocatoreCheStaGiocando").getAsInt();

            indiceCartaChiamata = jsonStato.get("indiceCartaChiamata").getAsInt();

            int punteggioGiocatore = jsonStato.get("punteggio").getAsInt();
            giocatoreController.setPunteggio(punteggioGiocatore);

            minPunteggioVittoria = jsonStato.get("punteggioMinVittoria").getAsInt();

            // se siamo nell'asta
            if(faseGioco.equalsIgnoreCase(getString(R.string.asta))){

                // mostro il pannello dell'asta e imposto l'ultima carta/punteggio chiamati
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pannelloChiamata.setVisibility(View.VISIBLE);
                        pannelloTavolo.setVisibility(View.INVISIBLE);
                    }
                });

                int ID_precedenteGiocatoreChiamata = jsonStato.get("ID_precedenteGiocatoreChiamata").getAsInt();
                if(indiceCartaChiamata != -1){
                    updateCartaChiamata(ID_precedenteGiocatoreChiamata, indiceCartaChiamata, minPunteggioVittoria);
                }



            }else if(faseGioco.equalsIgnoreCase(getString(R.string.primoTurno)) ||
                    faseGioco.equalsIgnoreCase(getString(R.string.turni))){

                indiceCartaChiamataFineAsta = indiceCartaChiamata;
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pannelloChiamata.setVisibility(View.INVISIBLE);
                        pannelloTavolo.setVisibility(View.VISIBLE);
                    }
                });


                for(Integer key : mapCartaGiocatore.keySet()){
                    aggiungiCartaGiocataDaGiocatore(key, mapCartaGiocatore.get(key));
                }
                updateGiocatoreStaGiocando(ID_giocatoreGiocante);

                String nomeChiamante = "";
                String nomeSocio = "";

                for(VistaGiocatore vg : visteGiocatori){
                    if(vg.getIDGiocatore() == ID_chiamante){
                        nomeChiamante = vg.getNomeGiocatore();
                    }

                    if(faseGioco.equalsIgnoreCase(getString(R.string.turni))){
                        if(vg.getIDGiocatore() == ID_socio){
                            nomeSocio = vg.getNomeGiocatore();
                        }
                    }
                }

                String msgTemp = "chiamante: " + nomeChiamante + "\n" +
                            "carta chiamata: " + SCALA_CARTE_ASTA[indiceCartaChiamataFineAsta] + "\n" +
                            "punteggio min. vittoria: " + String.valueOf(minPunteggioVittoria) + "\n";

                if(faseGioco.equalsIgnoreCase(getString(R.string.turni))){
                    msgTemp += "seme carta chiamata: " + semeCartaChiamataFineAsta + "\n";
                }

                final String msg = msgTemp;
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MsgBoxOkCancel msgBox = new MsgBoxOkCancel(msg, getString(R.string.msgOk));
                        msgBox.show(getSupportFragmentManager(),TAG);
                    }
                });

            }

        }
        serverListener.sendMessage(ACK_STATO);

        Thread t = new Thread(serverListener);
        t.start();

    }


    @Override
    protected void onResume(){
        super.onResume();

        Log.d(TAG, "sono nel resume!");



    }


    @Override
    protected void onPause(){
        super.onPause();
        Log.d(TAG, "sono nel Pause!");
    }

    @Override
    public void onBackPressed() {
        // non fare nulla, in modo che non torni all'activity precedente
    }


    private void impostaOggettiGrafici(){
        // assegno tutti gli oggetti della activity, per averli sempre a disposizione
        btnGiocaCarta = (Button) findViewById(R.id.btnGiocaCarta);
        btnGiocaCarta.setBackgroundColor(Color.parseColor(ColoriCodificheMie.GrigioCodifica));
        pannelloChiamata = (ConstraintLayout) findViewById(R.id.pannelloChiamata);
        pannelloTavolo = (ConstraintLayout) findViewById(R.id.pannelloTavolo);
        panelVincitoreTurno = (ConstraintLayout) findViewById(R.id.panelVincitoreTurno);
        pannelloCarteMano = (ConstraintLayout) findViewById(R.id.pannelloCarteMano);
        panelSeme = (ConstraintLayout) findViewById(R.id.panelSeme);
        btnEffettuaChiamata = (Button) findViewById(R.id.btnEffettuaChiamata);
        btnLascia = (Button) findViewById(R.id.btnLascia);
        numPunteggioVittoria = (NumberPicker) findViewById(R.id.numPunteggioVittoria);
        numPunteggioVittoria.setMinValue(61);
        numPunteggioVittoria.setMaxValue(120);
        numPunteggioVittoria.setValue(61);

        comboCartaChiamata = (Spinner) findViewById(R.id.comboCartaChiamata);
        comboCartaChiamata.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                comboCartaChiamata_SelectedIndexChanged(parentView);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        comboSemeChiamata = (Spinner) findViewById(R.id.comboSemeChiamata);
        txtBriscola = (TextView) findViewById(R.id.txtBriscola);
        txtStatoTurno = (TextView) findViewById(R.id.txtStatoTurno);

    }

    // rende visibile il pannello per la chiamata iniziale delle briscole e nasconde il pannello del tavolo
    public void visualizzaNuovaPartita(boolean async)
    {
        if(async){

            this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    pannelloTavolo.setVisibility(View.INVISIBLE);
                    pannelloChiamata.setVisibility(View.VISIBLE);
                    panelSeme.setVisibility(View.INVISIBLE);
                    txtStatoTurno.setText("");
                    txtBriscola.setText("");
                    caricaControlliChiamata();
                    indiceNextCartaMano = 0;
                }
            });
        }
        else{
            pannelloTavolo.setVisibility(View.INVISIBLE);
            pannelloChiamata.setVisibility(View.VISIBLE);
            panelSeme.setVisibility(View.INVISIBLE);
            txtStatoTurno.setText("");
            txtBriscola.setText("");
            caricaControlliChiamata();
            indiceNextCartaMano = 0;
        }

    }

    private void caricaSlotManoGiocatore()
    {
        // ottengo i riferimenti agli oggetti grafici e creo le viste associate
        String nomePck = getPackageName();
        for (int i = 0; i < NUM_MAX_CARTE_MANO; i++)
        {
            int id = getResources().getIdentifier("cartaMano" + String.valueOf(i), "id", nomePck);
            visteCarteMano[i] = new VistaCarta(this, pannelloCarteMano, id, i);
        }
    }


    private void caricaSlotTavolo()
    {
        // ottengo i riferimenti agli oggetti grafici e creo le viste associate
        String nomePck = getPackageName();
        for (int i = 0; i < NUM_MAX_CARTE_TAVOLO; i++)
        {
            int id = getResources().getIdentifier("cartaTavolo" + String.valueOf(i), "id", nomePck);
            VistaCarta vistaCarta = new VistaCarta(this, pannelloTavolo, id, -1);
            vistaCarta.getPictureBox().setBackgroundColor(Color.TRANSPARENT);
            id = getResources().getIdentifier("lblGiocatore" + String.valueOf(i), "id", nomePck);
            TextView lblGiocatore = (TextView) findViewById(id);
            visteGiocatori[i] = new VistaGiocatore(vistaCarta, lblGiocatore);
        }
    }


    public void btnGiocaCarta_click(View view){
        if(indiceCartaDaGiocare != -1)
        {
            // sposto la carta selezionata dalla mano al tavolo (nello slot del giocatore)
            visteGiocatori[0].GetVistaCarta().setCarta(visteCarteMano[indiceCartaDaGiocare].getCarta());
            visteCarteMano[indiceCartaDaGiocare].setCarta(null);
            visteCarteMano[indiceCartaDaGiocare].restorePosition();
            indiceCartaGiocata = indiceCartaDaGiocare;
            indiceCartaDaGiocare = -1;

            // disabilito il bottone per giocare la carta
            btnGiocaCarta.setEnabled(false);
            btnGiocaCarta.setBackgroundColor(Color.parseColor(ColoriCodificheMie.GrigioCodifica));

            // fine del turno: imposto a false il flag del turno corrente
            isTurnoGiocatoreCorrente = false;

            // imposto l'evento di fine turno. In questo modo eventuali thread in attesa di questo evento
            // (GiocatoreController) possono ripartire
            notificaTurnoFinito();

            // rendo non visibile il pannello del turno attivo
            // SetControlVisible(panelTurnoGiocatore, false);

        }
    }

    private void comboCartaChiamata_SelectedIndexChanged(View view)
    {
        // se la carta chiamata è il due mostro anche il punteggio
        if(comboCartaChiamata.getSelectedItem().toString().equalsIgnoreCase(SCALA_CARTE_ASTA[9]))
        {
            numPunteggioVittoria.setEnabled(true);
        }
        else
        {
            // TODO verificare quale va bene
            numPunteggioVittoria.setValue(numPunteggioVittoria.getMinValue());
            // numPunteggioVittoria.setText(((InputFilterMinMax) numPunteggioVittoria.getFilters()[0]).getMin());
            numPunteggioVittoria.setEnabled(false);
        }
    }


    private void notificaTurnoFinito(){

        synchronized (monitorTurno) {

            //this.notify();

            monitorTurno.notify();
            // msg.notifyAll();
        }

    }


    // TODO: spostare in vista Giocatore ?
    public void aggiungiCartaMano(final Carta c)
    {
        this.runOnUiThread(new Runnable(){

            @Override
            public void run(){
                if (indiceNextCartaMano < NUM_MAX_CARTE_MANO)
                {
                    if(indiceNextCartaMano == 0){
                        txtStatoTurno.setText("Asta");
                    }
                    visteCarteMano[indiceNextCartaMano].setCarta(c);
                    indiceNextCartaMano++;

                }
            }
        });

    }

    public void setTurnoGiocatoreCorrente()
    {
        this.runOnUiThread(new Runnable(){

            @Override
            public void run(){

                isTurnoGiocatoreCorrente = true;

                // rendo visibile il pannello del turno attivo
                // SetControlVisible(panelTurnoGiocatore, true);

                btnGiocaCarta.setBackgroundColor(Color.parseColor(ColoriCodificheMie.BeigeCodifica));
                // btnGiocaCarta.setEnabled(true);
            }
        });
    }


    public int getIndiceCartaGiocata()
    {
        return indiceCartaGiocata;
    }

    public void setID_giocatoreLocale(int ID_giocatoreLocale)
    {

        this.ID_giocatoreLocale = ID_giocatoreLocale;
    }

    // rende attivi i controlli per la chiamata della carta al giocatore corrente (fase asta)
    public void setChiamataCartaGiocatoreCorrente()
    {
        abilitaPannelloChiamata();
    }

    private void abilitaPannelloChiamata()
    {

        boolean res = handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d("PARTITA ACTIVITY", "menate");
                //pannelloChiamata.setEnabled(true);


                btnEffettuaChiamata.setBackgroundColor(Color.parseColor(ColoriCodificheMie.DarkOliveGreen));
                btnLascia.setBackgroundColor(Color.parseColor(ColoriCodificheMie.Firebrick));

                btnEffettuaChiamata.setEnabled(true);
                btnLascia.setEnabled(true);
            }
        });



/*
        pannelloChiamata.invalidate();
        btnEffettuaChiamata.invalidate();
        btnLascia.invalidate();*/

    }

    public int getCartaChiamata()
    {
        return indiceCartaChiamata;
    }

    public int getPunteggioVittoria()
    {
        // TODO è il numeric up-down
        return numPunteggioVittoria.getValue();
    }

    public String getSemeCartaChiamata()
    {
        return semeCartaChiamata;
    }

    public void abilitaSceltaSemeChiamante()
    {
        final AppCompatActivity partitaActivity = this;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MyGraphicsUtility.clearElementsSpinner(comboSemeChiamata);
                MyGraphicsUtility.addItemsSpinner(comboSemeChiamata, Arrays.asList(Carta.SEMI), partitaActivity);
                comboSemeChiamata.setSelection(0);
                panelSeme.setVisibility(View.VISIBLE);
                panelSeme.bringToFront();

            }
        });
    }

    // visualizza qual è il seme della briscola
    public void setPanelBriscola(final String seme)
    {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtBriscola.setText("BRISCOLA: \n" + seme);
                txtBriscola.setVisibility(View.VISIBLE);
                txtBriscola.bringToFront();
                panelSeme.setVisibility(View.INVISIBLE);

            }
        });
    }

    public void aggiungiGiocatore(final int ID_giocatore, final String nomeGiocatore)
    {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int indiceSlotTavolo = MathUtility.mod(ID_giocatore - ID_giocatoreLocale, NUM_MAX_CARTE_TAVOLO);
                visteGiocatori[indiceSlotTavolo].setDatiGiocatore(ID_giocatore, nomeGiocatore);
            }
        });

    }


    public void updateCartaChiamata(final int ID_giocatoreRemoto, final int numeroCartaCorrente, final int punteggioMinCorrente)
    {
        final AppCompatActivity partitaActivity = this;

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // rimuovo tutte le carte dal combobox fino a quella corrente (inclusa), in modo che
                // sia possibile selezionare solo una maggiore

                // devo fare questo cinema:
                List<String> elementiDaTenereComboCartaChiamata = new ArrayList<String>();


                boolean trovato = false;
                for(int i = 0; i < comboCartaChiamata.getAdapter().getCount(); i++){

                    // caso già trovata in un'iterazione precedente: va aggiunta sicuramente
                    if(trovato){
                        elementiDaTenereComboCartaChiamata.add((String) comboCartaChiamata.getAdapter().getItem(i));
                    }
                    else {
                        // caso trovata nell'iterazione corrente
                        if (((String) comboCartaChiamata.getAdapter().getItem(i)).equalsIgnoreCase(SCALA_CARTE_ASTA[numeroCartaCorrente])) {
                            trovato = true;

                            // se sono arrivato al "due" non devo rimuoverla, ma devo cambiare il punteggio minimo per la vittoria
                            // quindi devo aggiungerla alla lista delle carte da mettere
                            if (numeroCartaCorrente >= 9) {
                                elementiDaTenereComboCartaChiamata.add((String) comboCartaChiamata.getAdapter().getItem(i));
                                numPunteggioVittoria.setMinValue(punteggioMinCorrente + 1);
                            }
                        }
                    }
                }

                MyGraphicsUtility.addItemsSpinner(comboCartaChiamata, elementiDaTenereComboCartaChiamata, partitaActivity);
                comboCartaChiamata.setSelection(0);
                comboCartaChiamata.invalidate();

                for (VistaGiocatore g : visteGiocatori)
                {
                    // trovo il giocatore con l'ID passato
                    if (g.getIDGiocatore() == ID_giocatoreRemoto)
                    {
                        txtStatoTurno.setText("Il giocatore " + g.getNomeGiocatore() + " ha chiamato la carta: " + SCALA_CARTE_ASTA[numeroCartaCorrente]);
                    }
                }
            }
        });

    }


    public void updateFineChiamate(final int ID_giocatoreChiamante, final int indiceCarta, final int punteggioVittoria)
    {

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pannelloChiamata.setVisibility(View.INVISIBLE);
                try {
                    Thread.sleep(2000);
                }
                catch(InterruptedException e){
                    Log.d(TAG, e.toString());
                }
                pannelloTavolo.setVisibility(View.VISIBLE);


                for (VistaGiocatore g : visteGiocatori)
                {
                    // trovo il giocatore con l'ID passato
                    if (g.getIDGiocatore() == ID_giocatoreChiamante)
                    {

                        txtStatoTurno.setText(
                                "Il giocatore " + g.getNomeGiocatore() + " ha vinto l'asta, " +
                                        "chiamando la carta: " + SCALA_CARTE_ASTA[indiceCarta] +
                                        (indiceCarta == 9 ? "\nPunteggio vittoria: " + String.valueOf(punteggioVittoria) : ""));

                    }
                }
            }
        });


    }

    public void aggiungiCartaGiocataDaGiocatore(final int ID_giocatoreRemoto, final Carta giocata)
    {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int indiceSlotTavolo = MathUtility.mod(ID_giocatoreRemoto - ID_giocatoreLocale, NUM_MAX_CARTE_TAVOLO);
                visteGiocatori[indiceSlotTavolo].GetVistaCarta().setCarta(giocata);


            }
        });

    }

    public void updateGiocatoreLasciatoAsta(final int ID_giocatoreRemoto)
    {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (VistaGiocatore g : visteGiocatori)
                {
                    // trovo il giocatore con l'ID passato
                    if (g.getIDGiocatore() == ID_giocatoreRemoto)
                    {
                        txtStatoTurno.setText("Il giocatore " + g.getNomeGiocatore() + " ha lasciato l'asta");
                    }
                }
            }
        });

    }


    public void updatePartitaInterrotta()
    {
        final AppCompatActivity partitaActivity = this;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Log.d(TAG, "Uno o più giocatori sconnessi! Attesa rientro...");
                MsgBoxOkCancel msgBox = new MsgBoxOkCancel(getString(R.string.erroreGiocatoreSconnesso), getString(R.string.msgOk));
                msgBox.show(getSupportFragmentManager(), TAG);

            }
        });


    }


    public boolean vuoiGiocareNuovaPartita()
    {
        MsgBoxWithWaitOnUIThread msgBox = new MsgBoxWithWaitOnUIThread(getString(R.string.msgGiocaAncora),
                getString(R.string.si), getString(R.string.no), this);
        msgBox.showAndWait();


        if(msgBox.getResponse() == -1){
            try{
                throw new Exception("la risposta non è stata impsotata!");
            }
            catch(Exception e){
                Log.d(TAG, e.toString());
            }
            return false;
        }
        else{
            if(msgBox.getResponse() == 0){
                return false;
            }
            else{
                return true;
            }
        }

    }


    public void mostraVincitoriPartita(final boolean vinceSquadraChiamante, final String nomeChiamante, final String nomeSocio,
                                       final String[] nomiPopolo, final int punteggioSquadraChiamante)
    {

        String messaggioFinePartita = "CHIAMANTE: " + nomeChiamante + ", \nSOCIO: " + nomeSocio + "\n";
        messaggioFinePartita += "POPOLO: ";
        for(int i = 0; i < nomiPopolo.length; i++)
        {
            messaggioFinePartita += nomiPopolo[i];
            if(i < nomiPopolo.length - 1)
            {
                messaggioFinePartita += ", ";
            }
            else
            {
                messaggioFinePartita += "\n";
            }
        }
        messaggioFinePartita += "punti squadra chiamante: " + punteggioSquadraChiamante + "\n";
        messaggioFinePartita += "punti squadra popolo: " + String.valueOf(120 - punteggioSquadraChiamante) + "\n";
        messaggioFinePartita += "vince la squadra del " + (vinceSquadraChiamante ? "chiamante" : "popolo") + " !";

        MsgBoxWithWaitOnUIThread msgBox = new MsgBoxWithWaitOnUIThread(messaggioFinePartita, getString(R.string.msgOk), this);
        msgBox.showAndWait();

    }


    public void updateGiocatoreSceglieSeme(final int ID_GicoatoreChiamante)
    {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (VistaGiocatore g : visteGiocatori)
                {
                    // trovo il giocatore con l'ID passato
                    if (g.getIDGiocatore() == ID_GicoatoreChiamante)
                    {
                        txtStatoTurno.setText("Il giocatore " + g.getNomeGiocatore() + " sta scegliendo il seme della briscola ... ");
                    }
                }
            }
        });

    }

    public void updateGiocatoreStaGiocando(final int ID_GicoatoreChiamante)
    {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (VistaGiocatore g : visteGiocatori)
                {
                    // trovo il giocatore con l'ID passato
                    if (g.getIDGiocatore() == ID_GicoatoreChiamante)
                    {
                        txtStatoTurno.setText("Turno del giocatore " + g.getNomeGiocatore());
                    }
                }
            }
        });

    }


    public void caricaControlliChiamata()
    {
        // gestione controlli per l'asta

        MyGraphicsUtility.clearElementsSpinner(comboCartaChiamata);
        MyGraphicsUtility.addItemsSpinner(comboCartaChiamata, Arrays.asList(SCALA_CARTE_ASTA), this);
        comboCartaChiamata.setSelection(0);
        pannelloChiamata.bringToFront();
        disabilitaPannelloChiamata(false);
        numPunteggioVittoria.setEnabled(false);
    }

    private void disabilitaPannelloChiamata(boolean async)
    {

        if(async){
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //pannelloChiamata.setEnabled(false);
                    btnEffettuaChiamata.setBackgroundColor(Color.parseColor(ColoriCodificheMie.GrigioCodifica));
                    btnLascia.setBackgroundColor(Color.parseColor(ColoriCodificheMie.GrigioCodifica));

                    btnEffettuaChiamata.setEnabled(false);
                    btnLascia.setEnabled(false);
                }
            });
        }
        else{
            //pannelloChiamata.setEnabled(false);
            btnEffettuaChiamata.setBackgroundColor(Color.parseColor(ColoriCodificheMie.GrigioCodifica));
            btnLascia.setBackgroundColor(Color.parseColor(ColoriCodificheMie.GrigioCodifica));

            btnEffettuaChiamata.setEnabled(false);
            btnLascia.setEnabled(false);

        }

    }


    public void btnEffettuaChiamata_Click(View view)
    {
        for(int i = 0; i < SCALA_CARTE_ASTA.length; i++)
        {
            if (SCALA_CARTE_ASTA[i].equalsIgnoreCase((String) comboCartaChiamata.getSelectedItem().toString()))
            {
                indiceCartaChiamata = i;
                break;
            }
        }
        fineChiamata();
    }


    public void btnLascia_Click(View view){
        indiceCartaChiamata = -1;
        fineChiamata();
    }

    public void btnSceltaSeme_Click(View view)
    {
        semeCartaChiamata = comboSemeChiamata.getSelectedItem().toString();
        fineChiamata();
    }


    private void fineChiamata()
    {
        disabilitaPannelloChiamata(true);

        // notifico la fine del turno. In questo modo eventuali thread in attesa di questo evento
        // (GiocatoreController) possono ripartire
        notificaTurnoFinito();
    }



    public void mostraVincitoreTurno(final int ID_giocatoreVincente, final int punteggioTurno)
    {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for(VistaGiocatore g : visteGiocatori)
                {
                    // trovo il giocatore con l'ID passato
                    if(g.getIDGiocatore() == ID_giocatoreVincente)
                    {
                        txtStatoTurno.setText("Il giocatore " + g.getNomeGiocatore() + " vince il turno e prende " + punteggioTurno + " punti!");
                        txtStatoTurno.setVisibility(View.VISIBLE);
                        txtStatoTurno.bringToFront();
                    }
                }
            }
        });
    }



    public void updateGiocatoriMancantiInizioPartita(final int numGiocMancanti)
    {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                txtStatoTurno.setText("In attesa di " + numGiocMancanti + " giocatori per iniziare la partita...");
            }
        });
    }


    // TODO: spostare in vista tavolo?
    public void pulisciCarteTavolo()
    {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (VistaGiocatore giocatoreRemoto : visteGiocatori)
                {
                    giocatoreRemoto.GetVistaCarta().setCarta(null);
                }
            }
        });

    }


    public boolean IsTurnoGiocatoreCorrente()
    {
        return isTurnoGiocatoreCorrente;
    }

    public void selezionaDeselezionaCartaDaGiocare(int indiceCarta)
    {
        // se l'indice è lo stesso di quello corrente vuol dire che è stato fatto un secondo
        // click sulla stessa carta: in questo caso viene annullata la selezione
        if(indiceCarta == indiceCartaDaGiocare)
        {
            indiceCartaDaGiocare = -1;
            btnGiocaCarta.setEnabled(false);
        }
        else
        {
            // controllo se era già stata selezionata una carta da giocare: in tal caso
            // la deseleziono
            if (indiceCartaDaGiocare != -1)
            {
                visteCarteMano[indiceCartaDaGiocare].restorePosition();
            }

            indiceCartaDaGiocare = indiceCarta;

            // rendo attivo il bottone per giocare la carta
            btnGiocaCarta.setEnabled(true);
        }
    }



    public void updateContinuaPartiteSiNo(final boolean continua)
    {
        final AppCompatActivity partitaActivity = this;

        String msg = continua ? getString(R.string.msgNuovaPartita) : getString(R.string.msgFinePartite);
        MsgBoxWithWaitOnUIThread msgBox = new MsgBoxWithWaitOnUIThread(msg, getString(R.string.msgOk), this);
        msgBox.showAndWait();

        if(!continua) {
            partitaActivity.finish();
        }
    }


    public Object getMonitorTurno(){
        return monitorTurno;
    }

    public void onDestroy(){
        super.onDestroy();
        serverListener.stop();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


}
