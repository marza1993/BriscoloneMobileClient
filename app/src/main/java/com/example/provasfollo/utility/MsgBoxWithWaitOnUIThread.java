package com.example.provasfollo.utility;

import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;


/*
    Classe che permette di costruire un MsgBox con due bottoni (positivo e negativo), mostrarlo sul
    UI thread e bloccare il thread corrente (non UI) in attesa della risposta ottenuta con il click
    su uno dei bottoni.
    Il metodo da invocare è showAndWait(). Non va invocato il metodo run(). Questo è pubblico perchè
    deve implementare l'interface Runnable.
    Una volta terminato il metodo showAndWait() è possibile ottenere accedere alla risposta tramite
    getResponse().
 */
public class MsgBoxWithWaitOnUIThread implements Runnable, IMsgBoxResponseCollector {

    private final String TAG = "MsgBoxWithWait";
    private int response = -1; // -1 = None, 0 = no, 1 = sì

    private String msg = null;
    private String positiveText = null;
    private String negativeText = null;
    private AppCompatActivity activity;


    public MsgBoxWithWaitOnUIThread(String msg, String positiveText, AppCompatActivity activity){
        this.msg = msg;
        this.positiveText = positiveText;
        this.activity = activity;
    }


    public MsgBoxWithWaitOnUIThread(String msg, String positiveText, String negativeText, AppCompatActivity activity){
        this.msg = msg;
        this.positiveText = positiveText;
        this.negativeText = negativeText;
        this.activity = activity;
    }


    public void setResponseReceived(int response){
        this.response = response;
    }

    public void showAndWait(){
        synchronized (this) {
            activity.runOnUiThread(this);
            try {
                this.wait();
            } catch (InterruptedException e) {
                Log.d(TAG, e.toString());
            }
        }
    }


    @Override
    public void run(){

        MsgBoxOkCancel msgBox = (negativeText == null ? new MsgBoxOkCancel(msg, positiveText,
                new MsgBoxClickListenerWithNotify(this, 1)) :
                new MsgBoxOkCancel(msg, positiveText, negativeText, new MsgBoxClickListenerWithNotify(this,1),
                        new MsgBoxClickListenerWithNotify(this,0)));

        msgBox.show(activity.getSupportFragmentManager(), TAG);
    }

    public int getResponse(){
        return response;
    }
}
