package com.example.provasfollo.utility;

import android.content.DialogInterface;
import android.util.Log;


/*
    Questa classe implementa un ClickListener di un MsgBox (eseguito sul'UI thread).
    Se un thread diverso dal UI necessita di aspettare che venga data una risposta dal MsgBox
    allora potrà invocare il metodo "wait" su un oggetto di tipo IMsgBoxResponseCollector, che ha
    chiamato il metodo "show" del MsgBox sull'UI thread (dovrà dunque implementare anche Runnable).
    Questa classe dovrà essere istanziata passando l'oggetto IMsgBoxResponseCollector di cui sopra;
    nel metodo "onClick" verrà invocato dunque il metodo "notify" di questo, in modo da notificare
    un eventuale thread che sta aspettando la risposta dal MsgBox.
    Inoltre verrà anche impostato il valore della risposta ottenuta dal MsgBox
 */
public class MsgBoxClickListenerWithNotify implements DialogInterface.OnClickListener {

    private final static String TAG = "MsgBoxClickListener";

    IMsgBoxResponseCollector parent;
    int buttonIndex;

    public MsgBoxClickListenerWithNotify(IMsgBoxResponseCollector parentRunnable, int buttonIndex){
        this.parent = parentRunnable;
        this.buttonIndex = buttonIndex;
    }

    @Override
    public void onClick(DialogInterface dialog, int which){
        parent.setResponseReceived(buttonIndex);
        synchronized (parent){
            parent.notify();
            Log.d(TAG, "notify fatto da dentro la callback. response: " + Integer.toString(buttonIndex));
        }
    }
}
