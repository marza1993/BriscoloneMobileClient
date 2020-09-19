package com.example.provasfollo.view;
import android.text.Layout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import java.util.ArrayList;
import java.util.List;

import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;

import androidx.appcompat.app.AppCompatActivity;

import com.example.provasfollo.R;

public class MyGraphicsUtility {


    public static <T> void addItemsSpinner(Spinner spinner, List<T> elementi, AppCompatActivity contextActivity){

        ArrayAdapter<T> adapter = new ArrayAdapter<T>(
                contextActivity, android.R.layout.simple_spinner_item, elementi);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
    }

    public static void clearElementsSpinner(Spinner spinner){
        spinner.setAdapter(null);
    }

}
