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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;

import com.example.provasfollo.controller.GiocatoreController;
import com.example.provasfollo.model.Carta;
import com.example.provasfollo.utility.MyReflectionUtility;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeoutException;

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
    public final String PING_MSG = "<PING>";


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

    // flag per sapere se continuare a ricevere msg dal server
    private boolean keepReceivingMsgs = false;

    public synchronized boolean getKeepReceivingMSgThreadSafe(){
        return keepReceivingMsgs;
    }

    public synchronized void setKeepReceivingMsgsThreadSafe(boolean newVal){
        keepReceivingMsgs = newVal;
    }

    private synchronized boolean getMrunThreadSafe(){
        return mRun;
    }

    private synchronized void setMrunThreadSafe(boolean newVal){
        mRun = newVal;
    }

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
        setMrunThreadSafe(true);
        setKeepReceivingMsgsThreadSafe(true);
        riceviMsgRemoti();
    }


    public String read() throws Exception {

        class MyRunnable implements  Runnable{
            String receivedMsg = "";
            public String getReceivedMsg(){
                return receivedMsg;
            }

            @Override
            public void run(){
                try{
                    receivedMsg = mBufferIn.readLine();
                }
                catch(Exception e) {
                    Log.d(TAG, e.toString());
                }
            }
        }
        MyRunnable r = new MyRunnable();
        Thread t = new Thread(r);
        t.start();
        try{
            t.join();
        }
        catch(InterruptedException e){
            Log.d(TAG,e.toString());
        }

        if(r.getReceivedMsg().equalsIgnoreCase("") || r.getReceivedMsg().equalsIgnoreCase(PING_MSG)){
            return null;
        }
        return r.getReceivedMsg();
    }


    public void connectToServer() throws SocketTimeoutException, Exception {

        try{
            //here you must put your computer's IP address.
            InetSocketAddress serverAddr = new InetSocketAddress(IP, porta);

            Log.d("TCP Client", "C: Connecting...");

            //create a socket to make the connection with the server
            tcpClient = new Socket();

            tcpClient.connect(serverAddr, 3000);
        }
        catch(SocketTimeoutException e){
            Log.d(TAG, "socket timeout exception, " + e.toString());
            throw e;
        }
        catch(Exception e){
            Log.d(TAG, "socket exception, " + e.toString());
            throw e;
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

            while(getKeepReceivingMSgThreadSafe()){

                mServerMessage = "";

                //in this while the client listens for the messages sent by the server
                while (getMrunThreadSafe()) {

                    mServerMessage = mBufferIn.readLine();
                    if(mServerMessage!=null){
                        if(mServerMessage.contains(PING_MSG)){
                            Log.d(TAG, "ricevuto: " + mServerMessage);
                        }
                        else{
                            mServerMessage = mServerMessage.substring(mServerMessage.indexOf('{'));

                            if (mServerMessage != null) {

                                Log.d(TAG, "messaggio ricevuto dal server: " + mServerMessage);

                                break;

                            }
                        }
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

            if (mBufferOut != null) {
                mBufferOut.flush();
                mBufferOut.close();
            }
            mBufferIn = null;
            mBufferOut = null;
            mServerMessage = null;
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


    public void stop() {

        setKeepReceivingMsgsThreadSafe(false);
        setMrunThreadSafe(false);
    }













}