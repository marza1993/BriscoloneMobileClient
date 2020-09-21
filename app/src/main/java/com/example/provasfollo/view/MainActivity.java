package com.example.provasfollo.view;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.content.Intent;

import com.example.provasfollo.R;
import com.example.provasfollo.utility.ConfigReaderWriter;

public class MainActivity extends AppCompatActivity {

    public final static String KEY_IP = "IP";
    public final static String KEY_NOME = "nome";
    public final static String KEY_SERVER = "server";

    public final static String NOME_PROPERTY = "nome";


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


    }


    public void onDestroy() {
        super.onDestroy();
    }


    public void onBtnGiocaClick(View view){

        // ottengo il riferimento all'edit text
        String nome = ((EditText) findViewById(R.id.txtNome)).getText().toString();

        String nomeConfig = ConfigReaderWriter.getConfigValue(this, NOME_PROPERTY);

        if(!nome.equalsIgnoreCase(nomeConfig)){
            ConfigReaderWriter.setConfigValue(this, NOME_PROPERTY, nome);
        }

        String IP = ((EditText) findViewById(R.id.txtIP)).getText().toString();
        String server = ((EditText) findViewById(R.id.txtIP2)).getText().toString();


        if(nome.equalsIgnoreCase("") || IP.equalsIgnoreCase("")){
            // menate
        }
        else{
            Intent intent = new Intent(this, PartitaActivity.class);

            intent.putExtra(KEY_IP, IP);
            intent.putExtra(KEY_NOME, nome);
            intent.putExtra(KEY_SERVER, server);
            startActivity(intent);
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
