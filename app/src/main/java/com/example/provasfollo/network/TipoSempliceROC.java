package com.example.provasfollo.network;

/// <summary>
/// Oggetto che racchiude un valore di un tipo semplice (int, string, bool, float, etc..) e ne fornisce
/// una rappresentazione json
/// </summary>
public class TipoSempliceROC implements IJsonType
{
    Object val = null;

    public TipoSempliceROC(Object val)
    {
        this.val = val;
    }

    public TipoSempliceROC()
    {

    }

    public void populateObjectFromJson(String json)
    {

        val = (Object)json;
    }

    public String toJson()
    {
        return "\"" + val.toString() + "\"";
    }

    public Object getVal()
    {
        return val;
    }
}
