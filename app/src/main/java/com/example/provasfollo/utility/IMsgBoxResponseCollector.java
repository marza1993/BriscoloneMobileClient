package com.example.provasfollo.utility;

/*
    Interface che specifica il comportamento di una classe su cui viene impostata la risposta
    ottenuta tramite un click di un MsgBox.
    I clickListener dei MsgBox (MsgBoxClickListenerWithNotifyParent) avranno un riferimento ad un
    oggetto che implementa questa interface in modo da impostare il valore della risposta ricevuta.
 */
public interface IMsgBoxResponseCollector {

    void setResponseReceived(int response);
}
