package com.example.provasfollo.controller;

import com.example.provasfollo.model.Carta;
import com.example.provasfollo.network.IJsonType;

/// <summary>
/// interface che specifica le operazioni possibili con un oggetto giocatore. Questa interface viene utilizzata
/// secondo il pattern Proxy
/// </summary>
public interface IGiocatore extends IJsonType
{
    // chiamata della prossima carta (numero) ed eventualmente del prossimo punteggio (se l'asta scende sotto il 2)
    int[] chiamaCartaEPunteggio();

    // metodo per impostare il giocatore remoto come vincitore dell'asta
    void setIsChiamante();

    // se il giocatore corrente ha la carta che è stata chiamata, allora viene impostato come socio
    boolean checkSeSocio(int indiceCartaChiamata, String semeCartaChiamata);

    // metodo per richiedere al chiamante il seme della briscola (alla fine del primo turno)
    String getSemeCartaChiamata();

    Carta giocaCarta();

    void aggiungiCartaMano(Carta c);

    void rimuoviCartaGiocata();

    void aggiungiCartaPrese(Carta presa);

    void incrementaPunteggio(int puntiIncremento);

    Carta getCartaGiocata();

    int getPunteggio();

    String getNome();

    void setNome(String nome);

    int getID();

    void setID(int ID);

    //bool getIsChiamante();

    //bool getIsSocio();

    void reset();

    boolean vuoleIniziareNuovaPartita();
}
