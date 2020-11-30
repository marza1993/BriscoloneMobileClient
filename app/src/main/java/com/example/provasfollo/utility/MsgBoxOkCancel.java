package com.example.provasfollo.utility;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import com.example.provasfollo.R;


public class MsgBoxOkCancel extends DialogFragment {

    public enum BOTTONE_CLICCATO {
        POSITIVE,
        NEGATIVE,
        NONE
    }

    private String msgToShow = "";
    private String positiveButtonText = "";
    private String negativeButtonText = "";

    private DialogInterface.OnClickListener positiveButtonCallback = null;
    private DialogInterface.OnClickListener negativeButtonCallback = null;


    private BOTTONE_CLICCATO bottoneCliccato = BOTTONE_CLICCATO.NONE;

    public MsgBoxOkCancel(String msgToShow, String positiveButtonText){
        this.msgToShow = msgToShow;
        this.positiveButtonText = positiveButtonText;
    }

    public MsgBoxOkCancel(String msgToShow, String positiveButtonText, String negativeButtonText) {
        this.msgToShow = msgToShow;
        this.positiveButtonText = positiveButtonText;
        this.negativeButtonText = negativeButtonText;
    }

    public MsgBoxOkCancel(String msgToShow, String positiveButtonText,
                          DialogInterface.OnClickListener positiveButtonCallback){
        this.msgToShow = msgToShow;
        this.positiveButtonText = positiveButtonText;
        this.positiveButtonCallback = positiveButtonCallback;
    }

    public MsgBoxOkCancel(String msgToShow, String positiveButtonText, String negativeButtonText,
                          DialogInterface.OnClickListener positiveButtonCallback,
                          DialogInterface.OnClickListener negativeButtonCallback) {
        this.msgToShow = msgToShow;
        this.positiveButtonText = positiveButtonText;
        this.negativeButtonText = negativeButtonText;
        this.positiveButtonCallback = positiveButtonCallback;
        this.negativeButtonCallback = negativeButtonCallback;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
        builder.setMessage(msgToShow)
                .setPositiveButton(positiveButtonText,
                        // se la callback passata è null ne creo una, altrimenti uso quella passata
                        this.positiveButtonCallback == null ?
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                bottoneCliccato = BOTTONE_CLICCATO.POSITIVE;
                            }
                        }
                        :
                        positiveButtonCallback
                );

        if(negativeButtonText != ""){
            builder.setNegativeButton(negativeButtonText,

                    this.negativeButtonCallback == null ?
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            bottoneCliccato = BOTTONE_CLICCATO.NEGATIVE;
                        }
                    }
                    :
                    negativeButtonCallback
            );
        }
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public BOTTONE_CLICCATO getBottoneCliccato(){
        return bottoneCliccato;
    }

}