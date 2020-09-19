package com.example.provasfollo.view;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.view.View;
import android.widget.ImageView;

import com.example.provasfollo.model.Carta;

public class VistaCarta
{
    // TODO leggere da ini entrambi?

    private final float FATTORE_WIDTH_HEIGHT = (float) (2 / 3.0);
    private int top;
    private int left;
    private int width;
    private int height;

    // riferimento all'oggetto carta
    private Carta carta;

    // immagine della carta
    private Drawable immagine;

    // indice dello slot
    private int indiceSlot;

    // container in cui viene mostrata l'immagine della carta
    private ImageView pictureBox;

    // riferimento al form, per segnalare a questo gli eventi tipo click sulla carta
    private PartitaActivity parentActivity;

    // flag per sapere se la carta associata a questa vista è stata selezionata (cliccandoci sopra)
    private boolean cartaSelezionata = false;

    // riferimento al contenitore
    private View parent;

    public VistaCarta(PartitaActivity parentActivity, View parent, int imageViewID, int indiceSlot)
    {
        this.parentActivity = parentActivity;
        this.indiceSlot = indiceSlot;   // -1?

        pictureBox = (ImageView) parentActivity.findViewById(imageViewID);

        this.height = pictureBox.getHeight();

        this.width = pictureBox.getWidth();

        pictureBox.setImageBitmap(null);

        // TODO
        // pictureBox.BorderStyle = BorderStyle.Fixed3D;
        this.parent = parent;

        //parent.Controls.Add(pictureBox);

        // aggiungo alla pictureBox il metodo PictureBox_Click come gestore dell'evento click
        pictureBox.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                PictureBox_Click(v);
            }
        });

    }

    // imposto l'oggetto carta a cui la visualizzazione si riferisce
    public void setCarta(Carta c)
    {
        carta = c;
        if (c != null)
        {
            // carico l'immagine corrispondente
            setImage();
        }
        else
        {
            immagine = null;
            pictureBox.setImageBitmap(null);

        }
    }

    private void setImage()
    {
        try
        {
            int resourceID = parentActivity.getResources().getIdentifier(
                    "c" + String.valueOf(carta.getID()),
                    "drawable",
                    parentActivity.getPackageName()
                    );
            immagine = parentActivity.getResources().getDrawable(resourceID);

            // TODO
            // immagine = Utility.ResizeBitmap(immagine, pictureBox.Width, pictureBox.Height);
        }
        catch (Exception e)
        {
            //MessageBox.Show(e.ToString());
            //Log.d()
        }

        pictureBox.setImageDrawable(immagine);
    }


    // il click sull'immagine permette di selezionare o deselezionare questa carta come carta da giocare
    private void PictureBox_Click(View view)
    {
        // la selezione della carta da giocare può essere fatta solo se è il turno del giocatore
        // corrente e questa non è già stata selezionata
        if (parentActivity.IsTurnoGiocatoreCorrente() && carta!= null)
        {
            // se non era ancora stata selezionata questa carta la imposto come carta da giocare
            if (!cartaSelezionata)
            {
                cartaSelezionata = true;

                pictureBox.setLeft(pictureBox.getLeft() -20);
                pictureBox.setTop(pictureBox.getTop() - 20);

                // segnalo alla activity che la carta corrente è stata selezionata da giocare
                parentActivity.selezionaDeselezionaCartaDaGiocare(indiceSlot);
            }
            else
            {
                // se la carta era già stata selezionata annullo la selezione
                cartaSelezionata = false;
                pictureBox.setLeft(this.left);
                pictureBox.setTop(this.top);

                // segnalo alla activity che la carta corrente non è più selezionata da giocare
                parentActivity.selezionaDeselezionaCartaDaGiocare(indiceSlot);
            }
        }
        else
        {
            // TODO qualcosa  grafico ?
        }
    }


    // ripristina la posizione dello slot contenente la carta a quella originale
    public void restorePosition()
    {
        if (cartaSelezionata)
        {
            cartaSelezionata = false;
        }
        pictureBox.setLeft(this.left);
        pictureBox.setTop(this.top);
    }


    public Carta getCarta()
    {
        return carta;
    }

    public View GetParentControl()
    {
        return parent;
    }

    public PartitaActivity getParentActivity()
    {
        return parentActivity;
    }

    public ImageView getPictureBox()
    {
        return pictureBox;
    }

    public int getLeft()
    {
        return left;
    }

    public void setLeft(int left){
        this.left = left;
        pictureBox.setLeft(this.left);
    }


    public int getTop()
    {
        return top;
    }

    public void setTop(int top){
        this.top = top;
        pictureBox.setTop(this.top);
    }


    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }


}