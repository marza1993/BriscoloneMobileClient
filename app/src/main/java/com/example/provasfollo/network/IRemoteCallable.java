package com.example.provasfollo.network;

public interface IRemoteCallable
{

    /// <summary>
    /// Permette di invocare un metodo sull'oggetto corrente sulla base del nome del metodo stesso.
    /// </summary>
    /// <param name="parametri"></param>
    /// <returns></returns>
    RemoteObject invokeMethodByName(String nomeMetodo, RemoteObject[] parametri);

}
