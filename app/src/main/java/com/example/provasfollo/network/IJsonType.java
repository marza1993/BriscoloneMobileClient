package com.example.provasfollo.network;

public interface IJsonType
{

    /// <summary>
    /// Metodo che fornisce la rappresentazione json dell'oggetto
    /// </summary>
    /// <returns></returns>
    String toJson();


    /// <summary>
    /// Metodo che permette di impostare lo stato dell'oggetto a partire da una rappresentazione json
    /// </summary>
    /// <param name="json"></param>
    void populateObjectFromJson(String json);

}
