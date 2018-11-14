package com.example.bruno.diabeteslearning.Activities;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.bruno.diabeteslearning.Adapters.DataDisplayAdapter;
import com.example.bruno.diabeteslearning.Adapters.FoodsListViewAdapter;
import com.example.bruno.diabeteslearning.Carbohydrate.CarboDetector;
import com.example.bruno.diabeteslearning.Carbohydrate.FoodRegion;
import com.example.bruno.diabeteslearning.R;

import java.util.ArrayList;

public class DisplayDataActivity extends Activity {

    private CarboDetector carboDetector;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_carbohydrate_data);

        carboDetector = new CarboDetector(
                getIntent().getStringArrayListExtra("selectedFoodsName"),
                getIntent().getIntegerArrayListExtra("selectedFoodsArea"));

        configListView();
        setButton();
        displayEmptyData();

        EditText weightEditText = findViewById(R.id.totalWeight);
        EditText carboRelEditText = findViewById(R.id.carboRelation);

        carboRelEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    carboDetector.setInsulinCarboRelation(Float.parseFloat(v.getText().toString()));
                }
                return false;
            }
        });

        weightEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    carboDetector.setTotalFoodWeight(Float.parseFloat(v.getText().toString()));
                    carboDetector.calculateCarbo();
                    displayData();
                }
                return false;
            }
        });
    }

    private void configListView() {
        mRecyclerView = findViewById(R.id.foodDataDisplayListView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new DataDisplayAdapter(carboDetector.getFoods());
        mRecyclerView.setAdapter(mAdapter);

    }


    private void displayData() {
        TextView textView = findViewById(R.id.dataDisplayTextView);

        String text = "Peso total: " + Math.round(carboDetector.getTotalFoodWeight()) + "\n\n";

        text += "Carboidrato total: " + Math.round(carboDetector.getTotalCarbo()) + "\n\n";
        if (carboDetector.getInsulinCarboRelation() > 0) {
            text += "Dose de insulina: " + Math.round(carboDetector.getInsulinDose()) + "u\n\n";
        } else {
            text += "Dose de insulina: \n\n";
        }

        mAdapter.notifyDataSetChanged(); //lista foi atualizada

//        for (FoodRegion food: foods ) {
//            text += food.foodName +" Peso: " + food.weight + "g Carboidrato: " + food.carbo +"g\n\n";
//        }


        textView.setText(text);
    }

    private void displayEmptyData() {
        TextView textView = findViewById(R.id.dataDisplayTextView);

        String text = "Peso total: " + "\n\n";
        text += "Carboidrato total: " + "\n\n";
        text += "Dose de insulina: \n\n";

        textView.setText(text);

    }

    private void setButton() {
        FloatingActionButton imageButton = findViewById(R.id.nextPageButtonDisplayActivity);
        imageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO - SALVAR NO FIREBASE A CLASSE MEALPROPERTIES QUANDO SAIR DA PAGINA
                //carboDetector.getMealProperties();

//                Intent activity = new Intent(ImageActivity.super.getBaseContext(),
//                startActivity(activity);
            }
        });
    }
}
