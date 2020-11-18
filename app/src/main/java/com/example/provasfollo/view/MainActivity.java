package com.example.provasfollo.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.provasfollo.R;
import com.example.provasfollo.network.RemoteCallDispatcher;
import com.example.provasfollo.utility.ConfigReaderWriter;
import com.example.provasfollo.utility.MsgBoxOkCancel;

import java.net.Socket;
import java.net.SocketTimeoutException;


public class MainActivity extends AppCompatActivity {

    public final static String KEY_IP = "IP";
    public final static String KEY_NOME = "nome";
    public final static String KEY_SERVER = "server";

    public final static String NOME_PROPERTY = "nome";

    private final static String TAG = "MainActivity";

    private static RemoteCallDispatcher serverListener = null;
    private static int stato = -1;
    private static String IP = "";
    private static int porta = 11000;
    private boolean connesso = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String nome = ConfigReaderWriter.getConfigValue(this, "nome");
        if(!nome.equalsIgnoreCase("")){
            ((EditText) findViewById(R.id.txtNome)).setText(nome);
        }

        ((EditText)findViewById(R.id.txtIP)).setText(getString(R.string.IP));

        Log.d("STATO", "on create chiamato!");

        // ottengo l'unica istanza della classe RemoteCallDispatcher
        serverListener = RemoteCallDispatcher.getInstance();

        stato = 1;
    }

    @Override
    protected void onResume(){
        super.onResume();

        Log.d(TAG, "sono nel resume!" + Integer.toString(stato));
    }


    @Override
    protected void onPause(){
        super.onPause();
        Log.d(TAG, "sono nel Pause!" + Integer.toString(stato));
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.d(TAG, "sono nel Stop!" + Integer.toString(stato));
    }


    @Override
    public void onDestroy() {

        super.onDestroy();
        Log.d(TAG, "sono nel destroy!" + Integer.toString(stato));
    }


    @Override
    public void onRestart(){
        super.onRestart();
        Log.d(TAG, "sono nel restart!" + Integer.toString(stato));
    }

    private int numClickBtnGioca = 0;

    public void onBtnGiocaClick(View view){

        numClickBtnGioca ++;
        Log.d(TAG, "num click btn gicoa: " + Integer.toString(numClickBtnGioca));
        // controllo che non sia già stata effettuata la connessione (altrimenti lancerebbe due richieste
        // se si clicca due volte velocemente sul bottone prima che venga caricata l'activity)
        if(!connesso){
            String nome = ((EditText) findViewById(R.id.txtNome)).getText().toString();
            String nomeConfig = ConfigReaderWriter.getConfigValue(this, NOME_PROPERTY);

            // se il nome è diverso da quello salvato sulla memoria lo sovrascrivo
            if(!nome.equalsIgnoreCase(nomeConfig)){
                ConfigReaderWriter.setConfigValue(this, NOME_PROPERTY, nome);
            }

            // ottengo l'IP dal campo di testo
            IP = ((EditText) findViewById(R.id.txtIP)).getText().toString();
            String server = ((EditText) findViewById(R.id.txtIP2)).getText().toString();


            if(nome.equalsIgnoreCase("") || IP.equalsIgnoreCase("")){
                // menate
            }
            else{
                serverListener.setIP_porta(IP, porta);

                // provo ad effettuare la connessione al server (va fatto in un thread diverso dal main)
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try{
                            serverListener.connectToServer();
                            connesso = true;
                        }
                        catch(SocketTimeoutException e){
                            Log.d(TAG, "Socket timeout exception: " + e.toString());
                        }
                        catch(Exception e){
                            Log.d(TAG, "Exception: " + e.toString());
                        }

                    }
                });
                t.start();


                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // se la connessione è andata a buon fine apro l'activity principale della partita
                if(connesso){
                    Intent intent = new Intent(this, PartitaActivity.class);
                    intent.putExtra(KEY_IP, IP);
                    intent.putExtra(KEY_NOME, nome);
                    intent.putExtra(KEY_SERVER, server);
                    startActivity(intent);
                }
                else{
                    // la connessione non è riuscita: segnalo l'errore con un popup
                    MsgBoxOkCancel msgBox = new MsgBoxOkCancel(getString(R.string.erroreConnessione), getString(R.string.msgOk));
                    msgBox.show(getSupportFragmentManager(), TAG);
                }
            }
        }
    }


/*    private void mostraMsgAlert(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Add the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });


        // Create the AlertDialog
        AlertDialog dialog = builder.create();
    }*/


    public void cambiaSchermata(View view){
        Intent intent = new Intent(this, PartitaActivity.class);
        String msg = "message";
        intent.putExtra(msg, "robe");
        startActivity(intent);
    }



}
