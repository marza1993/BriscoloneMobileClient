package com.example.provasfollo.network;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;

public class RemoteObject implements IJsonType
{
    private String tipo = "";
    private boolean isTipoSemplice = true;
    private boolean isArray = false;
    private IJsonType[] valori = null;

    private static final String TIPO = "tipo";
    private static final String IS_TIPO_SEMPLICE = "isTipoSemplice";
    private static final String IS_ARRAY = "isArray";
    private static final String VAL = "val";

    private static final String NOME_CLASSE = "RemoteObject";

    private static HashMap<String, String> typeToFullyQualified = new HashMap<String, String>();

    static {
        typeToFullyQualified.put("Carta", "com.example.provasfollo.model.Carta");
        typeToFullyQualified.put("GiocatoreController", "com.example.provasfollo.controller.GiocatoreController");
    }

    public RemoteObject(String tipo, boolean isTipoSemplice, boolean isArray, IJsonType[] valori)
    {

        init(tipo, isTipoSemplice, isArray, valori);
    }

    public RemoteObject()
    {

    }

    private void init(String tipo, boolean isTipoSemplice, boolean isArray, IJsonType[] valori)
    {
        this.tipo = tipo;
        this.isTipoSemplice = isTipoSemplice;
        this.isArray = isArray;
        this.valori = valori;
    }


    public String toJson()
    {
        StringBuilder json = new StringBuilder();

        json.append(" { " +
                "\"" + TIPO + "\": \"" + tipo + "\", " +
                "\"" + IS_TIPO_SEMPLICE + "\": \"" + String.valueOf(isTipoSemplice) + "\", " +
                "\"" + IS_ARRAY + "\": \"" + String.valueOf(isArray) + "\", " +
                "\"" + VAL + "\": ");

        json.append(" [ ");
        int i = 0;
        for(IJsonType v : valori)
        {
            if(i > 0)
            {
                json.append(", ");
            }

            json.append(v.toJson());

            i++;
        }
        json.append(" ] ");

        json.append(" } ");
        return json.toString();
    }

    public void populateObjectFromJson(String json)
    {
        JsonElement jsonElement = new JsonParser().parse(json);
        JsonObject remoteObjectJson = jsonElement.getAsJsonObject();

        String tipo = remoteObjectJson.get(TIPO).getAsString();
        boolean isTipoSemplice = Boolean.parseBoolean(remoteObjectJson.get(IS_TIPO_SEMPLICE).getAsString());
        boolean isArray = Boolean.parseBoolean(remoteObjectJson.get(IS_ARRAY).getAsString());

        JsonArray valoriJsonToken = remoteObjectJson.get(VAL).getAsJsonArray();

        int numVal = valoriJsonToken.size();

        IJsonType[] valori = new IJsonType[numVal];

        if (isTipoSemplice)
        {
            for (int i = 0; i < numVal; i++)
            {
                valori[i] = new TipoSempliceROC();
                valori[i].populateObjectFromJson(valoriJsonToken.get(i).getAsString());
            }
        }
        else
        {
            for (int i = 0; i < numVal; i++)
            {
                try{
                    Class c = Class.forName(typeToFullyQualified.get(tipo));
                    valori[i] = (IJsonType) c.newInstance();
                    valori[i].populateObjectFromJson(valoriJsonToken.get(i).toString());

                }
                catch(Exception e){
                    Log.d(NOME_CLASSE, e.toString());
                }
            }
        }

        init(tipo, isTipoSemplice, isArray, valori);
    }

    public String getTipo()
    {
        return tipo;
    }

    public boolean getIsTipoSemplice()
    {
        return isTipoSemplice;
    }

    public boolean getIsArray()
    {
        return isArray;
    }

    public IJsonType[] getValori()
    {
        return valori;
    }


}
