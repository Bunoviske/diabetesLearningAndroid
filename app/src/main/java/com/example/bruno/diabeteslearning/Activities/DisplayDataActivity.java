package com.example.bruno.diabeteslearning.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.TextView;

import com.example.bruno.diabeteslearning.Carbohydrate.CarboDetector;
import com.example.bruno.diabeteslearning.R;

public class DisplayDataActivity extends Activity {

    private CarboDetector carboDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_carbohydrate_data);

        carboDetector = new CarboDetector(
                getIntent().getStringArrayListExtra("selectedFoodsName"),
                getIntent().getIntegerArrayListExtra("selectedFoodsArea"));

        //TODO - PEGAR PESO TOTAL DOS ALIMENTOS E RELACAO CHO:INSULINA
    }
}
