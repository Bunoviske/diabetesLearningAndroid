package com.example.bruno.diabeteslearning.UserIO;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import java.util.ArrayList;

import com.example.bruno.diabeteslearning.Adapters.FoodsListViewAdapter;
import com.example.bruno.diabeteslearning.Database.Firebase;
import com.example.bruno.diabeteslearning.ImagePaint.ImageViewCanvas;



public class FoodsDialogList {

    private String selectedItem = "";
    private ImageViewCanvas imageViewCanvas;
    private ArrayList<String> allFoods = new ArrayList<>();
    private ArrayList<String> selectedFoodsName = new ArrayList<>();
    private ArrayList<Integer> selectedFoodsArea;

    private int lastContourArea;

    Context context;


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    public FoodsDialogList(ImageViewCanvas imageView, ArrayList<Integer> areaArray,
                           RecyclerView recyclerView, Context context) {

        selectedFoodsArea = areaArray;
        imageViewCanvas = imageView;
        mRecyclerView = recyclerView;
        this.context = context;

        mAdapter = new FoodsListViewAdapter(selectedFoodsName, imageViewCanvas);
        mRecyclerView.setAdapter(mAdapter);

        getAllFoodsFromDatabase();

    }

    private void getAllFoodsFromDatabase(){
        allFoods = Firebase.getInstance().getAllFoods();
    }

    public ArrayList<String> getSelectedFoodsName(){
        return selectedFoodsName;
    }


    public void run(int contourArea) {

        lastContourArea = contourArea;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Selecione o alimento marcado");


        builder.setSingleChoiceItems(allFoods.toArray(new CharSequence[allFoods.size()])
                , -1,
                new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int item) {

                        selectedItem = allFoods.get(item);
                        Log.i("Dialog_item", selectedItem);
                    }
                });
        builder.setPositiveButton("OK", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int noUse) {

                Log.i("Dialog_ok", selectedItem);

                if (selectedItem != "") {

                    selectedFoodsName.add(selectedItem);
                    selectedFoodsArea.add(lastContourArea);

                    dialog.dismiss();
                    mAdapter.notifyDataSetChanged();
                    selectedItem = "";
                }
                //dialog fecha quando item nao é selecionado e "ok" é clicado,
                //entao deve apagar o ultimo path
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
        alert.setCanceledOnTouchOutside(false);
        alert.show();

    }
}

