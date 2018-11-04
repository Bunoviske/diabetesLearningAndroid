package com.example.bruno.diabeteslearning.UserInput;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.example.bruno.diabeteslearning.ImagePaint.ImageViewCanvas;
import com.example.bruno.diabeteslearning.MainActivity;

public class FoodsDialogList {

    private CharSequence selectedItem = "";
    private ImageViewCanvas imageViewCanvas;

    public FoodsDialogList(ImageViewCanvas imageView){
        imageViewCanvas = imageView;
    }

    public void run(Context context){

        final CharSequence[] items = {
                "Arroz", "Feijao", "Carne","Arroz", "Feijao", "Carne","Arroz", "Feijao", "Carne",
                "Arroz", "Feijao", "Carne","Arroz", "Feijao", "Carne","Arroz", "Feijao", "Carne"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Selecione o alimento marcado");

        builder.setSingleChoiceItems(items, -1,
                new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int item) {

                        selectedItem = items[item];
                        Log.i("Dialog_item", selectedItem.toString());
                    }
                });
        builder.setPositiveButton("OK", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int noUse) {

                Log.i("Dialog_ok", selectedItem.toString());

                if (selectedItem.toString() != ""){

                    //TODO - SALVAR NOME
                    dialog.dismiss();
                    selectedItem = "";
                }
                //dialog fecha quando item nao é selecionado e "ok" é clicado
                else imageViewCanvas.callClearLastPath();


            }
        });
        builder.setNegativeButton("CANCELAR", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int noUse) {
                imageViewCanvas.callClearLastPath();
                //qual a melhor pratica para chamar um metodo da classe que te chamou?

                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }
}

