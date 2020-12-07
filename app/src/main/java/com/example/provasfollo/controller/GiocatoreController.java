package com.example.provasfollo.controller;

import android.os.Looper;
import android.util.Log;

import com.example.provasfollo.model.Carta;
import com.example.provasfollo.model.Giocatore;
import com.example.provasfollo.network.IJsonType;
import com.example.provasfollo.network.IRemoteCallable;
import com.example.provasfollo.network.RemoteObject;
import com.example.provasfollo.network.TipoSempliceROC;
import com.example.provasfollo.utility.MyReflectionUtility;
import com.example.provasfollo.view.PartitaActivity;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class GiocatoreController implements IGiocatore, IRemoteCallable
{
    // riferimento al giocatore
    private Giocatore giocatore;

    // riferimento all'oggetto grafico
    private PartitaActivity view;

    private Object monitorTurno;

    private int ID = -1;
    private String nome = "";

    private static final String Id = "ID";
    private static final String NOME = "nome";

    public final static String NOME_CLASSE = "GiocatoreController";



    public GiocatoreController(PartitaActivity partitaActivity, Giocatore giocatore)
    {
        this.view = partitaActivity;
        this.giocatore = giocatore;
        this.monitorTurno = partitaActivity.getMonitorTurno();
    }


    public GiocatoreController()
    {

    }



    public void aggiungiCartaMano(Carta c)
    {
        giocatore.aggiungiCartaMano(c);
        view.aggiungiCartaMano(c);
    }

    public void aggiungiCartaMano(RemoteObject[] parametri)
    {
        Carta c = (Carta) parametri[0].getValori()[0];
        aggiungiCartaMano(c);
    }


    public void setSemeBriscola(String seme)
    {
        giocatore.setSemeBriscola(seme);
    }

    public void setSemeBriscola(RemoteObject[] parametri)
    {
        String seme = (String) ((TipoSempliceROC)parametri[0].getValori()[0]).getVal();
        setSemeBriscola(seme);
    }

    public void aggiungiCartaPrese(Carta presa)
    {
        giocatore.aggiungiCartaPrese(presa);
        // TODO vista.. forse
    }

    public void aggiungiCartaPrese(RemoteObject[] parametri)
    {
        Carta c = (Carta)parametri[0].getValori()[0];
        aggiungiCartaPrese(c);
    }

    public String getNome()
    {
        if(giocatore != null)
        {
            return giocatore.getNome();
        }
        else{
            return this.nome;
        }
    }

    public void setNome(String nome)
    {
        // nulla
    }

    public int getPunteggio()
    {
        return giocatore.getPunteggio();
    }

    public Carta giocaCarta()
    {
        // segnalo al form l'inizio del turno del giocatore
        view.setTurnoGiocatoreCorrente();


        // aspetto la fine dell'interazione dell'utente con la grafica (turno passato)
        waitTurnoFinito();

        giocatore.giocaCarta(view.getIndiceCartaGiocata());
        return giocatore.getCartaGiocata();
    }

    public Carta getCartaGiocata()
    {
        return giocatore.getCartaGiocata();
    }



    public void incrementaPunteggio(int puntiIncremento)
    {
        giocatore.incrementaPunteggio(puntiIncremento);
    }

    public void incrementaPunteggio(RemoteObject[] parametri)
    {
        int puntiIncremento = Integer.parseInt((String)((TipoSempliceROC)parametri[0].getValori()[0]).getVal());
        incrementaPunteggio(puntiIncremento);
    }


    public void rimuoviCartaGiocata()
    {
        giocatore.rimuoviCartaGiocata();
    }

    public void setID(int ID)
    {
        giocatore.setID(ID);
        view.setID_giocatoreLocale(ID);
    }

    public void setID(RemoteObject[] parametri)
    {
        int ID = Integer.parseInt((String)((TipoSempliceROC)parametri[0].getValori()[0]).getVal());
        setID(ID);
    }

    public int getID()
    {
        if(giocatore != null)
        {
            return giocatore.getID();
        }
        else {
            return this.ID;
        }
    }

    public int[] chiamaCartaEPunteggio()
    {
        // segnalo al form l'inizio del turno del giocatore
        view.setChiamataCartaGiocatoreCorrente();

        // aspetto la fine dell'interazione dell'utente con la grafica (turno passato)
        waitTurnoFinito();

        int[] res = new int[2];
        res[0] = view.getCartaChiamata();
        res[1] = view.getPunteggioVittoria();

        return res;
    }

    public void setIsChiamante()
    {
        // TODO: vedere con la view
        giocatore.setIsChiamante();
    }


    public boolean checkSeSocio(int indiceCartaChiamata, String seme)
    {
        return giocatore.checkSeSocio(indiceCartaChiamata, seme);
    }

    public boolean checkSeSocio(RemoteObject[] parametri)
    {
        int indiceCartaChiamata = Integer.parseInt((String)((TipoSempliceROC)parametri[0].getValori()[0]).getVal());
        String seme = (String)((TipoSempliceROC)parametri[1].getValori()[0]).getVal();
        return checkSeSocio(indiceCartaChiamata, seme);
    }

    //public bool getIsChiamante()
    //{
    //    return giocatore.getIsChiamante();
    //}

    //public bool getIsSocio()
    //{
    //    return giocatore.getIsSocio();
    //}


    public String getSemeCartaChiamata()
    {
        // rendo attiva la selezione del seme della carta chiamante
        view.abilitaSceltaSemeChiamante();

        // aspetto la fine dell'interazione dell'utente con la grafica (turno passato)
        waitTurnoFinito();

        String semeCartaChiamata = view.getSemeCartaChiamata();
        return semeCartaChiamata;
    }

    private void waitTurnoFinito(){
        // attendo fino a che l'oggetto view non invoca il metodo notify su sè stesso (turno finito)

        synchronized (monitorTurno) {
            try{
                Log.d(NOME_CLASSE, " waiting to get notified at time:"+System.currentTimeMillis());

                if(Looper.myLooper() == Looper.getMainLooper()){
                    Log.d(NOME_CLASSE, "Sono nel thread UI");
                }
                else{
                    Log.d(NOME_CLASSE, "NON sono nel thread UI");
                }

                monitorTurno.wait();
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            Log.d(NOME_CLASSE, " waiter thread got notified at time:"+System.currentTimeMillis());
        };
    }

    public void reset()
    {
        giocatore.reset();
    }

    public void setPunteggio(int punteggio){
        giocatore.setPunteggio(punteggio);
    }

    public boolean vuoleIniziareNuovaPartita()
    {
        return view.vuoiGiocareNuovaPartita();
    }

    public String toJson()
    {
        return giocatore.toJson();
    }

    public void populateObjectFromJson(String json)
    {
        // ottengo gli elementi del json e li salvo in un dictionary
        // ottengo gli elementi del json
        JsonElement jsonElement = new JsonParser().parse(json);
        JsonObject giocatoreControllerJsonObject = jsonElement.getAsJsonObject();


        int ID = Integer.parseInt(giocatoreControllerJsonObject.get(Id).getAsString());
        String nome = giocatoreControllerJsonObject.get(NOME).getAsString();

        this.ID = ID;
        this.nome = nome;
    }

    public RemoteObject invokeMethodByName(String nomeMetodo, RemoteObject[] parametri)
    {
        IJsonType[] valori;

        switch (nomeMetodo)
        {
            case "aggiungiCartaMano":

                aggiungiCartaMano(parametri);
                return null;

            case "setSemeBriscola":

                setSemeBriscola(parametri);
                return null;

            case "aggiungiCartaPrese":

                aggiungiCartaPrese(parametri);
                return null;

            case "getNome":

                String nome = getNome();
                valori = new IJsonType[1];
                valori[0] = new TipoSempliceROC(nome);

                return new RemoteObject(String.class.getSimpleName(), true, false, valori);

            case "getPunteggio":

                int punteggio = getPunteggio();
                valori = new IJsonType[1];
                valori[0] = new TipoSempliceROC(punteggio);
                return new RemoteObject(int.class.getSimpleName(), true, false, valori);

            case "giocaCarta":

                valori = new IJsonType[1];
                valori[0] = giocaCarta();
                return new RemoteObject(Carta.class.getSimpleName(), false, false, valori);

            case "getCartaGiocata":

                valori = new IJsonType[1];
                valori[0] = getCartaGiocata();
                return new RemoteObject(Carta.class.getSimpleName(), false, false, valori);

            case "incrementaPunteggio":

                incrementaPunteggio(parametri);
                return null;

            case "rimuoviCartaGiocata":

                rimuoviCartaGiocata();
                return null;

            case "setID":

                setID(parametri);
                return null;

            case "getID":

                valori = new IJsonType[1];
                valori[0] = new TipoSempliceROC(getID());
                return new RemoteObject(int.class.getSimpleName(), true, false, valori);


            case "chiamaCartaEPunteggio":

                int[] cartaEPunteggio = chiamaCartaEPunteggio();
                valori = new IJsonType[2];
                valori[0] = new TipoSempliceROC(cartaEPunteggio[0]);
                valori[1] = new TipoSempliceROC(cartaEPunteggio[1]);
                return new RemoteObject(int.class.getSimpleName(), true, true, valori);

            case "setIsChiamante":

                setIsChiamante();
                return null;

            case "checkSeSocio":

                valori = new IJsonType[1];
                valori[0] = new TipoSempliceROC(checkSeSocio(parametri));
                return new RemoteObject(boolean.class.getSimpleName(), true, false, valori);

            case "getSemeCartaChiamata":

                valori = new IJsonType[1];
                valori[0] = new TipoSempliceROC(getSemeCartaChiamata());
                return new RemoteObject(String.class.getSimpleName(), true, false, valori);

            case "reset":

                reset();
                return null;

            case "vuoleIniziarePartita":

                valori = new IJsonType[1];
                valori[0] = new TipoSempliceROC(vuoleIniziareNuovaPartita());
                return new RemoteObject(boolean.class.getSimpleName(), true, false, valori);

            default:

                Log.d(NOME_CLASSE, "nessun metodo con questo nome");
                return null;
        }
    }
}
