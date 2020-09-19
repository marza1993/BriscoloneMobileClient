package com.example.provasfollo.network;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

public class RemoteObjectCall
{
    private String nomeOggetto = "";
    private String nomeMetodo = "";
    private RemoteObject[] parametri = null;
    private boolean hasParam = true;

    private final static String NOME_OGGETTO = "nomeOggetto";
    private final static String NOME_METODO = "nomeMetodo";
    private final static String HAS_PARAM = "hasParam";
    private final static String PARAMETRI = "parametri";


    public RemoteObjectCall(String nomeOggetto, String nomeMetodo, RemoteObject[] parametri)
    {
        init(nomeOggetto, nomeMetodo, parametri);
    }

    public RemoteObjectCall()
    {

    }

    private void init(String nomeOggetto, String nomeMetodo, RemoteObject[] parametri)
    {
        this.nomeOggetto = nomeOggetto;
        this.nomeMetodo = nomeMetodo;
        this.parametri = parametri;
        if (this.parametri == null)
        {
            this.hasParam = false;
        }
    }

    public String getNomeOggetto()
    {
        return nomeOggetto;
    }

    public String getNomeMetodo()
    {
        return nomeMetodo;
    }

    public RemoteObject[] getParametri()
    {
        return parametri;
    }

    public String toJson()
    {
        StringBuilder json = new StringBuilder();
        json.append("{" +
                "\"" + NOME_OGGETTO + "\": \"" + nomeOggetto + "\", " +
                "\"" + NOME_METODO + "\": \"" + nomeMetodo + "\", " +
                "\"" + HAS_PARAM + "\": \"" + String.valueOf(hasParam) + "\"");
        if (hasParam)
        {
            json.append(", " +
                    "\"" + PARAMETRI + "\": [ ");

            int i = 0;
            for(RemoteObject p : parametri)
            {
                if (i > 0)
                {
                    json.append(", ");
                }
                json.append(p.toJson());
                i++;
            }
            json.append("]");
        }

        json.append("}");

        return json.toString();

    }

    public void populateObjectFromJson(String json_)
    {
        String json = json_.substring(json_.indexOf("{"));

        JsonElement jsonElement = new JsonParser().parse(json);
        JsonObject jsonROC = jsonElement.getAsJsonObject();

        String nomeOggetto = jsonROC.get(NOME_OGGETTO).getAsString();
        String nomeMetodo = jsonROC.get(NOME_METODO).getAsString();
        boolean hasParam = Boolean.parseBoolean(jsonROC.get(HAS_PARAM).getAsString());

        if (hasParam)
        {
            JsonArray parametriJsonElements = jsonROC.get(PARAMETRI).getAsJsonArray();

            int numParams = parametriJsonElements.size();

            RemoteObject[] parametri = new RemoteObject[numParams];
            for(int i = 0; i < numParams; i++)
            {
                parametri[i] = new RemoteObject();
                parametri[i].populateObjectFromJson(parametriJsonElements.get(i).toString());
            }

            init(nomeOggetto, nomeMetodo, parametri);
        }
        else
        {
            init(nomeOggetto, nomeMetodo, null);
        }
    }

}
