package com.example.provasfollo.view;
import android.view.View;
import android.widget.TextView;
import android.graphics.Color;

public class VistaGiocatore
{
    // slot che visualizza la carta giocata dal giocatore
    private VistaCarta vistaCarta;

    // dati del giocatore associato
    private int ID_giocatore;
    private String nome;

    private TextView lblNomeGiocatore;



    public VistaGiocatore(VistaCarta vistaCarta, TextView lblNomeGiocatore)
    {
        this.vistaCarta = vistaCarta;
        this.ID_giocatore = -1;
        this.nome = "";
        this.lblNomeGiocatore = lblNomeGiocatore;

        // TODO
        //vistaCarta.GetParentControl().addView  Controls.Add(lblNomeGiocatore);
    }

    public void setDatiGiocatore(int ID_giocatore, String nome)
    {
        this.ID_giocatore = ID_giocatore;
        this.nome = nome;
        // ThreadGraphicSyncHelper.SetControlText(vistaCarta.getContainerForm(), lblNomeGiocatore, nome);

        // TODO verificare se serve il meccanismo tipo invoke


        lblNomeGiocatore.setText(nome);

        lblNomeGiocatore.invalidate();
        /*lblNomeGiocatore.setVisibility(View.VISIBLE);
        lblNomeGiocatore.bringToFront();*/


    }

    public int getIDGiocatore()
    {
        return ID_giocatore;
    }

    public String getNomeGiocatore()
    {
        return nome;
    }

    public VistaCarta GetVistaCarta()
    {
        return vistaCarta;
    }



}
