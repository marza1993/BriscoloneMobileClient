package com.example.provasfollo.controller;
import java.util.ArrayList;
import android.util.Log;

import com.example.provasfollo.model.Carta;
import com.example.provasfollo.network.IJsonType;
import com.example.provasfollo.network.IRemoteCallable;
import com.example.provasfollo.network.RemoteObject;
import com.example.provasfollo.network.TipoSempliceROC;
import com.example.provasfollo.view.PartitaActivity;

/// <summary>
/// Oggetto che rappresenta, lo stato corrente del turno (che è in remoto).
/// Permette di mostrate ai vari giocatori le carte giocate e il vincitore del turno
/// </summary>
public class StatoGiocoController implements IStatoGioco, IRemoteCallable
{

    // riferimento all'oggetto grafico
    private PartitaActivity view = null;

    private GiocatoreController giocatoreController = null;

    public final static String NOME_CLASSE = "StatoGiocoController";

    public StatoGiocoController(PartitaActivity partitaActivity, GiocatoreController giocatoreController)
    {
        this.view = partitaActivity;
        this.giocatoreController = giocatoreController;
    }

    public StatoGiocoController()
    {

    }

    public void setBriscola(String seme)
    {
        giocatoreController.setSemeBriscola(seme);
        view.setPanelBriscola(seme);
    }

    public void setBriscola(RemoteObject[] parametri)
    {
        String seme = (String)((TipoSempliceROC)parametri[0].getValori()[0]).getVal();
        setBriscola(seme);
    }

    public void comunicaDatiGiocatori(IGiocatore[] giocatori)
    {
        for (IGiocatore g : giocatori)
        {
            int ID = g.getID();
            String nome = g.getNome();
            view.aggiungiGiocatore(ID, nome);
        }
    }

    public void comunicaDatiGiocatori(RemoteObject[] parametri)
    {
        IGiocatore[] giocatori;

        IJsonType[] valori = parametri[0].getValori();

        giocatori = new IGiocatore[valori.length];

        for(int i = 0; i < valori.length; i++)
        {
            giocatori[i] = (IGiocatore)valori[i];
        }

        comunicaDatiGiocatori(giocatori);
    }

    public void updateCartaChiamata(int ID_giocatore, int indiceCarta, int punteggioVittoria)
    {
        view.updateCartaChiamata(ID_giocatore, indiceCarta, punteggioVittoria);
    }

    public void updateCartaChiamata(RemoteObject[] parametri)
    {
        int ID_giocatore = Integer.parseInt((String)((TipoSempliceROC)parametri[0].getValori()[0]).getVal());
        int indiceCarta = Integer.parseInt((String)((TipoSempliceROC)parametri[1].getValori()[0]).getVal());
        int punteggioVittoria = Integer.parseInt((String)((TipoSempliceROC)parametri[2].getValori()[0]).getVal());
        updateCartaChiamata(ID_giocatore, indiceCarta, punteggioVittoria);
    }

    public void updateCartaGiocata(int ID_giocatore, Carta giocata)
    {
        view.aggiungiCartaGiocataDaGiocatore(ID_giocatore, giocata);
    }

    public void updateCartaGiocata(RemoteObject[] parametri)
    {
        int ID_giocatore = Integer.parseInt((String) ((TipoSempliceROC)parametri[0].getValori()[0]).getVal());
        Carta giocata = (Carta) parametri[1].getValori()[0];
        updateCartaGiocata(ID_giocatore, giocata);
    }

    public void updateConnessioneInterrotta()
    {
        view.updatePartitaInterrotta();
    }

    public void updateFineChiamate(int ID_giocatoreChiamante, int indiceCarta, int punteggioVittoria)
    {
        view.updateFineChiamate(ID_giocatoreChiamante, indiceCarta, punteggioVittoria);
    }

    public void updateFineChiamate(RemoteObject[] parametri)
    {
        int ID_giocatoreChiamante = Integer.parseInt((String)((TipoSempliceROC)parametri[0].getValori()[0]).getVal());
        int indiceCarta = Integer.parseInt((String)((TipoSempliceROC)parametri[1].getValori()[0]).getVal());
        int punteggioVittoria = Integer.parseInt((String)((TipoSempliceROC)parametri[2].getValori()[0]).getVal());
        updateFineChiamate(ID_giocatoreChiamante, indiceCarta, punteggioVittoria);
    }

    public void updateFinePartita(boolean vinceSquadraChiamante, String nomeChiamante, String nomeSocio,
                                  String[] nomiPopolo, int punteggioSquadraChiamante)
    {
        view.mostraVincitoriPartita(vinceSquadraChiamante, nomeChiamante, nomeSocio, nomiPopolo, punteggioSquadraChiamante);
    }

    public void updateFinePartita(RemoteObject[] parametri)
    {
        boolean vinceSquadraChiamante = Boolean.parseBoolean((String)((TipoSempliceROC)parametri[0].getValori()[0]).getVal());
        String nomeChiamante = (String)((TipoSempliceROC)parametri[1].getValori()[0]).getVal();
        String nomeSocio = (String)((TipoSempliceROC)parametri[2].getValori()[0]).getVal();

        IJsonType[] valori = parametri[3].getValori();
        String[] nomiPopolo = new String[valori.length];
        for(int i = 0; i < valori.length; i++)
        {
            nomiPopolo[i] = (String) ((TipoSempliceROC)valori[i]).getVal();
        }

        int punteggioSquadraChiamante = Integer.parseInt((String)((TipoSempliceROC)parametri[4].getValori()[0]).getVal());

        updateFinePartita(vinceSquadraChiamante, nomeChiamante, nomeSocio, nomiPopolo, punteggioSquadraChiamante);
    }

    public void updateGiocatoreLasciaAsta(int ID_giocatore)
    {
        view.updateGiocatoreLasciatoAsta(ID_giocatore);
    }

    public void updateGiocatoreLasciaAsta(RemoteObject[] parametri)
    {
        int ID_giocatore = Integer.parseInt((String)((TipoSempliceROC)parametri[0].getValori()[0]).getVal());
        updateGiocatoreLasciaAsta(ID_giocatore);
    }

    public void updateGiocatoreSceglieSeme(int ID_giocatore)
    {
        view.updateGiocatoreSceglieSeme(ID_giocatore);
    }

    public void updateGiocatoreSceglieSeme(RemoteObject[] parametri)
    {
        int ID_giocatore = Integer.parseInt((String)((TipoSempliceROC)parametri[0].getValori()[0]).getVal());
        updateGiocatoreSceglieSeme(ID_giocatore);
    }

    public void updateGiocatoreStaGiocando(int ID_giocatore)
    {
        view.updateGiocatoreStaGiocando(ID_giocatore);
    }

    public void updateGiocatoreStaGiocando(RemoteObject[] parametri)
    {
        int ID_giocatore = Integer.parseInt((String)((TipoSempliceROC)parametri[0].getValori()[0]).getVal());
        updateGiocatoreStaGiocando(ID_giocatore);
    }

    public void updateNuovaPartita()
    {
        // TODO vedere la roba dell'async (tutto il meccanismo dell'invoke)
        view.visualizzaNuovaPartita(true);
    }

    public void updateTurnoFinito(int ID_giocatoreVincente, int punteggioTurno)
    {
        view.mostraVincitoreTurno(ID_giocatoreVincente, punteggioTurno);
        /*try{
            Thread.sleep(5000);
        }
        catch(InterruptedException e){
            Log.d(NOME_CLASSE, e.toString());
        }*/

        view.pulisciCarteTavolo();
    }

    public void updateTurnoFinito(RemoteObject[] parametri)
    {
        int ID_giocatoreVincente = Integer.parseInt((String)((TipoSempliceROC)parametri[0].getValori()[0]).getVal());
        int punteggioTurno = Integer.parseInt((String)((TipoSempliceROC)parametri[1].getValori()[0]).getVal());
        updateTurnoFinito(ID_giocatoreVincente, punteggioTurno);
    }

    public void updateContinuaPartiteSiNo(RemoteObject[] parametri)
    {
        boolean continua = Boolean.parseBoolean((String)((TipoSempliceROC)parametri[0].getValori()[0]).getVal());
        updateContinuaPartiteSiNo(continua);
    }


    public void updateContinuaPartiteSiNo(boolean continua)
    {
        view.updateContinuaPartiteSiNo(continua);
    }


    public RemoteObject invokeMethodByName(String nomeMetodo, RemoteObject[] parametri)
    {
        switch (nomeMetodo)
        {
            case "setBriscola":

                setBriscola(parametri);
                return null;

            case "comunicaDatiGiocatori":

                comunicaDatiGiocatori(parametri);
                return null;

            case "updateCartaChiamata":

                updateCartaChiamata(parametri);
                return null;

            case "updateCartaGiocata":

                updateCartaGiocata(parametri);
                return null;

            case "updateConnessioneInterrotta":

                updateConnessioneInterrotta();
                return null;

            case "updateFineChiamate":

                updateFineChiamate(parametri);
                return null;

            case "updateFinePartita":

                updateFinePartita(parametri);
                return null;


            case "updateGiocatoreLasciaAsta":

                updateGiocatoreLasciaAsta(parametri);
                return null;

            case "updateGiocatoreSceglieSeme":

                updateGiocatoreSceglieSeme(parametri);
                return null;

            case "updateGiocatoreStaGiocando":

                updateGiocatoreStaGiocando(parametri);
                return null;

            case "updateNuovaPartita":
                updateNuovaPartita();
                return null;

            case "updateTurnoFinito":

                updateTurnoFinito(parametri);
                return null;

            case "updateContinuaPartite":

                updateContinuaPartiteSiNo(parametri);
                return null;

            default:

                Log.d(NOME_CLASSE, "non esiste un metodo con quel nome.");
                return null;

        }
    }


}
