package com.example.provasfollo.utility;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import com.example.provasfollo.R;


public class MsgBoxOkCancel extends DialogFragment {
    
    String msgToShow = "";
    String positiveButtonText = "";
    String negativeButtonText = "";

    public MsgBoxOkCancel(String msgToShow, String positiveButtonText){
        this.msgToShow = msgToShow;
        this.positiveButtonText = positiveButtonText;
    }
    public MsgBoxOkCancel(String msgToShow, String positiveButtonText, String negativeButtonText){
        this.msgToShow = msgToShow;
        this.positiveButtonText = positiveButtonText;
        this.negativeButtonText = negativeButtonText;
    }

    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
        builder.setMessage(msgToShow)
                .setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO: personalizzare da fuori con una callback/interface da passare?
                    }
                });
        if(negativeButtonText != ""){
            builder.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
        }
        // Create the AlertDialog object and return it
        return builder.create();
    }
}