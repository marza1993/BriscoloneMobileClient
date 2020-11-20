package com.example.provasfollo.model;


import android.util.Log;

import com.example.provasfollo.network.IJsonType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;

public class Carta implements IJsonType
{

    public final static String[] SCALA_CARTE_ASTA = { "asse", "3", "re", "cavallo", "fante", "7",
        "6", "5", "4", "2"};

    public final static String[] SEMI = { "bastoni", "spade", "coppe", "denari" };

    public final static String SEME = "seme";
    public final static String NUMERO = "numero";
    public final static String Id = "ID";
    public final static String IS_SEME_BRISCOLA = "isSemeBriscola";

    // uno dei possibili
    private String seme = "";
    // da 1 a 10
    private int numero = -1;
    // calcolato a seconda del numero
    private int punti = -1;

    // nome della carta (senza seme)
    private String nome = "";

    // descrizione testuale della carta (es: fante di denari)
    private String descrizione = "";

    // è true se il seme della carta corrente è di briscola
    private boolean isSemeBriscola = false;

    // identificatore univoco della carta (da 0 a 39)
    private int ID = -1;

    private final static String NOME_CLASSE = "Carta";

    // precondizione: seme e numero sono passati correttamente
    public Carta(String seme, int numero, int ID) throws Exception
    {
        init(seme, numero, ID);
    }

    public Carta()
    {

    }

    private void init(String seme, int numero, int ID)// throws Exception
    {
        this.ID = ID;
        if (numero < 1 || numero > 10)
        {
            //throw new Exception("il seme passato non è corretto\n");
        }

        this.seme = seme;
        this.numero = numero;

        switch (numero)
        {
            // asse
            case 1:
                this.punti = 11;
                this.nome = "asse";
                break;
            case 3:
                this.punti = 10;
                this.nome = String.valueOf(numero);
                break;
            // figure
            case 8:
                this.punti = 2;
                this.nome = "fante";
                break;
            case 9:
                this.punti = 3;
                this.nome = "cavallo";
                break;
            case 10:
                this.punti = 4;
                this.nome = "re";
                break;
            // scarti
            default:
                this.punti = 0;
                this.nome = String.valueOf(numero);
                break;
        }

        this.descrizione = this.nome + " di " + seme;
        this.isSemeBriscola = false;
    }

    /// <summary>
    /// Confronta la carta corrente (this) con un'altra passata come parametro e determina quale delle due vince.
    /// PRECONDIZIONE: la carta corrente (this) è assunta giocata per prima, perciò è necessario che il metodo
    /// venga chiamato sulla prima carta giocata delle due
    /// - a parità di seme vince la carta con valore/numero maggiore.
    /// - con semi diversi e nessuna briscola vince la carta attuale (si suppone che la carta this sia la prima giocata)
    /// - con una briscola vince la carta che ha briscola
    /// </summary>
    /// <param name="confronto"></param>
    /// <returns></returns>
    public boolean vinceSu(Carta confronto)
    {
        // se le due carte hanno lo stesso seme:
        if (this.seme.equals(confronto.seme))
        {
            // se confronto scartini (somma dei punti è zero) allora vince la carta
            // con numero più alto
            if (this.punti + confronto.punti == 0)
            {
                return (this.numero > confronto.numero);
            }
            // se confronto carte che valgono punti oppure una che vale punti e uno scartino
            // allora vince la carta con punteggio più alto
            else
            {
                return (this.punti > confronto.punti);
            }
        }
        // semi diversi
        else
        {
            // se nessuna delle due carte è briscola vince la prima delle due (this)
            if (!this.isBriscola() && !confronto.isBriscola())
            {
                return true;
            }
            // se una e una sola delle due carte è briscola allora vince la carta di briscola;
            // il caso entrambe briscola rientra nella condizione "stesso seme" gestita prima
            else
            {
                return (this.isBriscola());
            }
        }
    }


    public void stampaCarta()
    {
/*        Console.WriteLine("--------------------");
        Console.WriteLine("|  " + nome + " |");
        Console.WriteLine("|------------------|");*/
    }

    public String toJson()
    {
        StringBuilder json = new StringBuilder();
        json.append(" { " +
                "\"" + SEME + "\": \"" + seme + "\", " +
                "\"" + NUMERO + "\": \"" + String.valueOf(numero) + "\", " +
                "\"" + Id + "\": \"" + String.valueOf(ID) + "\", " +
                "\"" + IS_SEME_BRISCOLA + "\": \"" + String.valueOf(isSemeBriscola) + "\"" +
                " } ");
        return json.toString();

    }

    public void populateObjectFromJson(String json)
    {
        // ottengo gli elementi del json
        JsonElement jsonElement = new JsonParser().parse(json);
        JsonObject cartaJsonObject = jsonElement.getAsJsonObject();

        String seme = cartaJsonObject.get(SEME).getAsString();
        int numero = Integer.parseInt(cartaJsonObject.get(NUMERO).getAsString());
        int ID = Integer.parseInt(cartaJsonObject.get(Id).getAsString());
        boolean isSemeBriscola = Boolean.parseBoolean(cartaJsonObject.get(IS_SEME_BRISCOLA).getAsString());

        init(seme, numero, ID);

        if (isSemeBriscola)
        {
            this.setSemeBriscola();
        }
    }

    public String ToString()
    {
        return descrizione;
    }


    public String getSeme()
    {
        return seme;
    }

    public String getNome()
    {
        return nome;
    }

    public int getPunti()
    {
        return punti;
    }

    public int getNumero()
    {
        return numero;
    }

    public int getID()
    {
        return ID;
    }

    // per sapere se la carta è del seme di briscola o no
    public boolean isBriscola()
    {
        return isSemeBriscola;
    }

    public void setSemeBriscola()
    {
        this.isSemeBriscola = true;
    }

}
