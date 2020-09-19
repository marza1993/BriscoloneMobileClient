package com.example.provasfollo.network;

import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import android.util.Xml;

import java.util.HashMap;

import com.example.provasfollo.controller.GiocatoreController;
import com.example.provasfollo.model.Carta;
import com.example.provasfollo.utility.MyReflectionUtility;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

public class RemoteCallDispatcher extends AsyncTask<String, String, TcpClient> {

    // riferimento all'unica istanza singleton
    private static RemoteCallDispatcher instance = null;


    // oggetto per la connessione tcp
    private TcpClient tcpClient;

    private String IP = "";
    private int porta = -1;


    private final int DIM_BUFFER_MSG = 1024;

    private String payloadReceived = "";

    private int numByteMsg = 0;

    public static String data = null;

    public final String TERMINATORE_MSG = "<EOF>";


    // dictionary con coppie del tipo <"nome oggetto" , oggetto>
    private HashMap<String, IRemoteCallable> oggettiRemoti = new HashMap<String, IRemoteCallable>();

    // oggetto che rappresenta il risultato dell'elaborazione dei metodi remoti
    RemoteObject resultElab;

    private final static String TAG = "RemoteCallDispatcher";

    private RemoteCallDispatcher(){

    }

    public void setIP_porta(String IP, int porta){
        this.IP = IP;
        this.porta = porta;
    }

    public static RemoteCallDispatcher getInstance(){
        if(instance == null){
            instance = new RemoteCallDispatcher();
        }
        return instance;
    }

    public void addOggettoRemoto(String chiave, IRemoteCallable oggettoRemoto){
        oggettiRemoti.put(chiave, oggettoRemoto);
    }

    public void sendMsgToServer(String msg){
        if(tcpClient != null){
            tcpClient.sendMessage(msg);
        }
        else{
            Log.d("RemoteCallDispatcher", "il client tcp e' null");
        }

    }

    public void stopClient(){
        if (tcpClient != null) {
            tcpClient.stopClient();
        }
    }

    @Override
    protected TcpClient doInBackground(String... message) {

        //we create a TCPClient object
        tcpClient = new TcpClient(new TcpClient.OnMessageReceived() {
            @Override
            //here the messageReceived method is implemented
            public void messageReceived(String message) {
                //this method calls the onProgressUpdate
                publishProgress(message);
            }
        });

        tcpClient.setServerIp(IP, porta);
        //tcpClient.setServerIp("127.0.0.1", 11000);
        tcpClient.run();

        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);


/*        //response received from server
        Log.d("test", "response " + values[0]);


        String json = values[0];

        //json = json.substring(0, json.length() - 1);

        Gson gson = new Gson();
        MainActivity.RemoteObjectCall roc = gson.fromJson(json, MainActivity.RemoteObjectCall.class);

        //process server response here....

        ((TextView) findViewById(R.id.txtRispServer)).setText(roc.nome + "; " + roc.numCorde);*/

        if(Looper.myLooper() == Looper.getMainLooper()){
            Log.d("RemoteCallDispatcher", "Sono nel thread UI");
        }
        else{
            Log.d("RemoteCallDispatcher", "NON sono nel thread UI");
        }

        dispatchROC(values[0]);

        String risp = getRispostaElab();
        sendMsgToServer(risp);
    }

    private boolean dispatchROC(String jsonFromServer)
    {
        RemoteObjectCall roc = new RemoteObjectCall();

        // TODO: ripulitura json
        roc.populateObjectFromJson(jsonFromServer);

        if(oggettiRemoti.containsKey(roc.getNomeOggetto()))
        {
            resultElab = oggettiRemoti.get(roc.getNomeOggetto()).invokeMethodByName(roc.getNomeMetodo(), roc.getParametri());
            return true;
        }
        return false;
    }


    // converte il risultato dell'elbaroazione dei metodi remoti in un messaggio che può essere inviato dal TCPServer
    private String getRispostaElab()
    {
        String json = "";
        if(resultElab != null)
        {
            json = resultElab.toJson() + TERMINATORE_MSG;
        }
        else{
            json = TERMINATORE_MSG;
        }
        return json;
    }

}