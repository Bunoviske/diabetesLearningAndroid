package com.example.bruno.diabeteslearning.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.bruno.diabeteslearning.Carbohydrate.CarboDetector;
import com.example.bruno.diabeteslearning.Carbohydrate.FoodRegion;
import com.example.bruno.diabeteslearning.MainActivity;
import com.example.bruno.diabeteslearning.R;

import java.util.ArrayList;

public class DisplayDataActivity extends Activity {

    private CarboDetector carboDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_carbohydrate_data);

        setButton();
        displayEmptyData();

        carboDetector = new CarboDetector(
                getIntent().getStringArrayListExtra("selectedFoodsName"),
                getIntent().getIntegerArrayListExtra("selectedFoodsArea"));

        EditText weightEditText = findViewById(R.id.totalWeight);
        EditText carboRelEditText = findViewById(R.id.carboRelation);

        carboRelEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT){
                    carboDetector.setInsulinCarboRelation(Float.parseFloat(v.getText().toString()));
                }
                return false;
            }
        });

        weightEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    carboDetector.setTotalFoodWeight(Float.parseFloat(v.getText().toString()));
                    carboDetector.calculateCarbo();
                    displayData();
                }
                return false;
            }
        });
    }


    private void displayData() {
        TextView textView = findViewById(R.id.dataDisplayView);

        String text = "Peso total: " + Math.round(carboDetector.getTotalFoodWeight()) + "\n\n";

        text += "Carboidrato total: " + Math.round(carboDetector.getTotalCarbo()) + "\n\n";
        if (carboDetector.getInsulinCarboRelation() > 0) {
            text += "Dose de insulina: " + Math.round(carboDetector.getInsulinDose()) + "u\n\n";
        }
        else {
            text += "Dose de insulina: \n\n" ;
        }

        ArrayList<FoodRegion> foods = carboDetector.getFoods();
        //TODO - TRANSFORMAR DEPOIS EM UM LISTVIEW
        for (FoodRegion food: foods ) {
            text += food.foodName +" Peso: " + food.weight + "g Carboidrato: " + food.carbo +"g\n\n";
        }


        textView.setText(text);
    }

    private void displayEmptyData() {
        TextView textView = findViewById(R.id.dataDisplayView);

        String text = "Peso total: " + "\n\n";
        text += "Carboidrato total: " + "\n\n";
        text += "Dose de insulina: \n\n" ;

        textView.setText(text);

    }


    private void setButton(){
        ImageButton imageButton = findViewById(R.id.nextPageButtonDisplayActivity);
        imageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO - SALVAR NO FIREBASE A CLASSE MEALPROPERTIES QUANDO SAIR DA PAGINA
//                Intent activity = new Intent(MainActivity.super.getBaseContext(),
//                startActivity(activity);
            }
        });
    }
}
