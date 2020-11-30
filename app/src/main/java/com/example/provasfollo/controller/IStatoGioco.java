package com.example.provasfollo.controller;
import com.example.provasfollo.model.Carta;

import java.util.ArrayList;

// Interface che specifica le operazioni necessarie per mostrare l'avanzamento globale del turno
// a tutti i giocatori.
public interface IStatoGioco
{

    /// <summary>
    /// segnala la carta chiamata dall'ultimo giocatore e il punteggio
    /// </summary>
    /// <param name="ID_giocatore"></param>
    /// <param name="indiceCarta"></param>
    /// <param name="punteggioVittoria"></param>
    void updateCartaChiamata(int ID_giocatore, int indiceCarta, int punteggioVittoria);


    /// <summary>
    /// segnala che un certo giocatore ha abbandonato l'asta
    /// </summary>
    /// <param name="ID_giocatore"></param>
    void updateGiocatoreLasciaAsta(int ID_giocatore);


    /// <summary>
    /// comunica a tutti i giocatori che il giocatore che ha vinto l'asta sta scegliendo il seme della briscola
    /// </summary>
    /// <param name="ID_giocatore"></param>
    void updateGiocatoreSceglieSeme(int ID_giocatore);


    /// <summary>
    /// comunica a tutti i giocatori che è il turno del giocatore ID_giocatore
    /// </summary>
    /// <param name="ID_giocatore"></param>
    void updateGiocatoreStaGiocando(int ID_giocatore);


    /// <summary>
    /// segnala la fine della fase dell'asta, comunicando chi l'ha vinta, con quale carta e con che punteggio
    /// minimo per la vittoria
    /// </summary>
    /// <param name="ID_giocatoreChiamante"></param>
    /// <param name="indiceCarta"></param>
    /// <param name="punteggioVittoria"></param>
    void updateFineChiamate(int ID_giocatoreChiamante, int indiceCarta, int punteggioVittoria);


    /// <summary>
    /// segnala che un giocatore ha giocato una carta
    /// ed ha terminato il suo turno
    /// </summary>
    /// <param name="ID_giocatore"></param>
    /// <param name="giocata"></param>
    void updateCartaGiocata(int ID_giocatore, Carta giocata);


    /// <summary>
    /// segnala che il turno è finito
    /// </summary>
    /// <param name="ID_giocatoreVincente"></param>
    /// <param name="punteggioTurno"></param>
    void updateTurnoFinito(int ID_giocatoreVincente, int punteggioTurno);

    // comunica la squadra vincitrice della partita
    void updateFinePartita(boolean vinceSquadraChiamante, String nomeChiamante, String nomeSocio,
                           String[] nomiPopolo, int punteggioSquadraChiamante);

    // comunica i dati dei giocatori agli altri
    void comunicaDatiGiocatori(IGiocatore[] giocatori);


    // comunica qual è il seme di briscola a tutti i giocatori
    void setBriscola(String seme);

    // comunica l'inizio di una nuova partita
    void updateNuovaPartita();

    // segnala che uno o più giocatori hanno abbandonato (chiuso l'app)
    void updateConnessioneInterrotta();


    void updateContinuaPartiteSiNo(boolean continua);

}
