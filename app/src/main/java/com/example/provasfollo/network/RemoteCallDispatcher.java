package com.example.provasfollo.network;

import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import android.util.Xml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;

import com.example.provasfollo.controller.GiocatoreController;
import com.example.provasfollo.model.Carta;
import com.example.provasfollo.utility.MyReflectionUtility;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

public class RemoteCallDispatcher implements Runnable {

    // riferimento all'unica istanza singleton
    private static RemoteCallDispatcher instance = null;


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

    private Socket tcpClient = null;

    private boolean mRun = false;
    // used to send messages
    private PrintWriter mBufferOut;
    // used to read messages from the server
    private BufferedReader mBufferIn;

    // message to send to the server
    private String mServerMessage;

    public static RemoteCallDispatcher getInstance(){
        if(instance == null){
            instance = new RemoteCallDispatcher();
        }
        return instance;
    }


    private RemoteCallDispatcher(){

    }


    public void setIP_porta(String IP, int porta){
        this.IP = IP;
        this.porta = porta;
    }

    @Override
    public void run(){
        connectToServer();
        mRun = true;
        riceviMsgRemoti();
    }


    public void connectToServer(){

        try{
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(IP);

            Log.d("TCP Client", "C: Connecting...");

            //create a socket to make the connection with the server
            tcpClient = new Socket(serverAddr, porta);
        }
        catch(Exception e){
            Log.d(TAG, "socket exception, " + e.toString());
        }

        try{
            //sends the message to the server
            mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(tcpClient.getOutputStream())), true);

            //receives the message which the server sends back
            mBufferIn = new BufferedReader(new InputStreamReader(tcpClient.getInputStream()));

        }
        catch(Exception e){
            Log.d(TAG, e.toString());
        }

    }

    public void riceviMsgRemoti(){
        try {

            while(true){

                mServerMessage = "";

                //in this while the client listens for the messages sent by the server
                while (mRun) {

                    mServerMessage = mBufferIn.readLine();
                    mServerMessage = mServerMessage.substring(mServerMessage.indexOf('{'));

                    if (mServerMessage != null) {

                        Log.d(TAG, "messaggio ricevuto dal server: " + mServerMessage);

                        break;

                    }

                }

                dispatchROC(mServerMessage);

                String risp = getRispostaElab();

                sendMessage(risp);
            }

        } catch (Exception e) {
            Log.e("TCP", "S: Error", e);
        } finally {
            //the socket must be closed. It is not possible to reconnect to this socket
            // after it is closed, which means a new socket instance has to be created.
            try{
                tcpClient.close();
            }
            catch(Exception e){
                Log.d(TAG, e.toString());
            }

        }
    }

    public void sendMessage(final String message) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (mBufferOut != null) {
                    Log.d(TAG, "Sending: " + message);
                    mBufferOut.println(message);
                    mBufferOut.flush();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
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

    public void addOggettoRemoto(String chiave, IRemoteCallable oggettoRemoto){
        oggettiRemoti.put(chiave, oggettoRemoto);
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





    public void stopClient() {

        mRun = false;

        if (mBufferOut != null) {
            mBufferOut.flush();
            mBufferOut.close();
        }
        mBufferIn = null;
        mBufferOut = null;
        mServerMessage = null;
    }













}