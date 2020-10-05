package com.example.provasfollo.model;
import android.util.Log;

import java.util.ArrayList;

public class Giocatore
{

    private String nome;

    // identificatore univoco del giocatore (viene assegnato dal server remoto)
    private int ID = -1;

    private final int NUM_MAX_CARTE_MANO = 8;

    // lista con i riferimenti alle carte che il giocatore ha in mano ad ogni turno (al primo turno sono 8)
    private Carta[] mano = new Carta[NUM_MAX_CARTE_MANO];

    int indiceProssimaCartaMano = 0;

    // lista con i riferimenti alle carte prese nei turni vinti dal giocatore
    private ArrayList<Carta> prese;

    // riferimento alla carta giocata nel turno corrente
    private Carta giocata;

    // punteggio attuale del giocatore
    private int punteggio;

    private boolean isSocio = false;
    private boolean isChiamante = false;

    private final String TAG = Giocatore.class.toString();

    public Giocatore(String nome)
    {
        Log.d(TAG, "costuttore giocatore");
        giocata = null;
        this.punteggio = 0;
        this.nome = nome;
        prese = new ArrayList<Carta>();
    }

    // reinizializza l'oggetto allo stato iniziale (ad es. per una nuova partita)
    public void reset()
    {
        indiceProssimaCartaMano = 0;

        // annullo tutte le carte eventualmente in mano (se la partita si conclude in modo corretto
        // non può succedere che vi siano ancora carte in mano
        for(int i = 0; i < mano.length; i++)
        {
            mano[i] = null;
        }

        prese.clear();
        giocata = null;
        punteggio = 0;
        isSocio = false;
        isChiamante = false;
    }

    public void giocaCarta(int indiceCartaScelta)
    {
        // sposto la carta dalla mano al tavolo
        giocata = mano[indiceCartaScelta];
        mano[indiceCartaScelta] = null;
    }


    public void aggiungiCartaMano(Carta c)
    {
        if(indiceProssimaCartaMano < NUM_MAX_CARTE_MANO)
        {
            mano[indiceProssimaCartaMano] = c;
            indiceProssimaCartaMano++;
        }
    }

    public void setSemeBriscola(String seme)
    {
        for(Carta c : mano)
        {
            // una carta sarà null, perchè è già in tavola
            if (c != null)
            {
                if (c.getSeme().equalsIgnoreCase(seme))
                {
                    c.setSemeBriscola();
                }
            }
        }

        // carta giocata
        if (giocata.getSeme().equalsIgnoreCase(seme))
        {
            giocata.setSemeBriscola();
        }
    }

    // verifica se il giocatore corrente possiede la carta chiamata (in mano o giocata),
    // in caso affermativo è il socio
    public boolean checkSeSocio(int indiceCartaChiamata, String seme)
    {
        for(Carta c : mano)
        {
            if(c != null)
            {
                if(c.getNome().equalsIgnoreCase(Carta.SCALA_CARTE_ASTA[indiceCartaChiamata]) &&
                        c.getSeme().equalsIgnoreCase(seme))
                {
                    isSocio = true;
                    break;
                }
            }
        }

        // controllo quella in tavola
        if (giocata.getNome().equalsIgnoreCase(Carta.SCALA_CARTE_ASTA[indiceCartaChiamata]) &&
                giocata.getSeme().equalsIgnoreCase(seme))
        {
            isSocio = true;
        }

        return isSocio;
    }

    public void rimuoviCartaGiocata()
    {
        giocata = null;
    }


    public void aggiungiCartaPrese(Carta presa)
    {
        prese.add(presa);
    }

    public void incrementaPunteggio(int puntiIncremento)
    {
        this.punteggio += puntiIncremento;
    }

    public Carta getCartaGiocata()
    {
        return giocata;
    }

    public int getPunteggio()
    {
        return punteggio;
    }

    public String getNome()
    {
        return nome;
    }

    public int getID()
    {
        return ID;
    }

    public void setID(int ID)
    {
        this.ID = ID;
    }

    public void stampaMano()
    {
        Log.d(TAG, "****** Mano giocatore: " + nome + " *******");
        int i = 0;
        for(Carta c : mano)
        {
            Log.d(TAG, "carta " + (++i) + ": ");
            c.stampaCarta();
        }
        Log.d(TAG, "*******************************************");
    }

    public void setIsChiamante()
    {
        isChiamante = true;
    }

    public void setIsSocio()
    {
        isSocio = true;
    }


    public boolean getIsChiamante()
    {
        return isChiamante;
    }

    public boolean getIsSocio()
    {
        return isSocio;
    }

    public String toJson()
    {
        StringBuilder json = new StringBuilder();
        json.append(" { " +
                "\"ID\": " + String.valueOf(ID) + ", " +
                "\"nome\": " + nome +
                " } ");
        return json.toString();
    }



}
