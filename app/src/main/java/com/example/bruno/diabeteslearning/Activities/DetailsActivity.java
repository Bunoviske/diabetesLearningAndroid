package com.example.bruno.diabeteslearning.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.bruno.diabeteslearning.Adapters.DataDisplayAdapter;
import com.example.bruno.diabeteslearning.Carbohydrate.CarboDetector;
import com.example.bruno.diabeteslearning.R;

import java.text.DecimalFormat;

public class DetailsActivity extends AppCompatActivity {

    private TextView tv_timestamp;
    private TextView tv_pesototal;
    private TextView tv_relacao_insulina;
    private TextView tv_total_carbo;
    private TextView tv_insulina;
    private CarboDetector carboDetector;
    private RecyclerView.Adapter mAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        tv_timestamp = findViewById(R.id.tv_timestamp);
        tv_pesototal = findViewById(R.id.tv_pesototal);
        tv_relacao_insulina = findViewById(R.id.tv_relacao_insulina);
        tv_total_carbo = findViewById(R.id.tv_total_carbo);
        tv_insulina = findViewById(R.id.tv_insulina);


        ActionBar actionBar = this.getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        LoadCarboDetector();
        setTableDescriptors();
        DisplayCarboDetectorData();
        configListView();


    }

    private void configListView() {
        RecyclerView mRecyclerView = findViewById(R.id.rv_foodHistory);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new DataDisplayAdapter(carboDetector.getFoods());
        mRecyclerView.setAdapter(mAdapter);
    }


    private void LoadCarboDetector(){
        Intent i = getIntent();
        carboDetector = (CarboDetector) i.getBundleExtra("carboDetector").getSerializable("carboDetector");

    }

    private void DisplayCarboDetectorData(){

        tv_timestamp.setText(carboDetector.getTimeStamp());
        tv_pesototal.append(Integer.toString(Math.round(carboDetector.getTotalFoodWeight()))+"g");
        tv_total_carbo.append(String.format("%sg", new DecimalFormat("##.##").format(
                (carboDetector.getTotalCarbo()))));
        tv_relacao_insulina.append(String.format("%s", new DecimalFormat("##.##").format(
                (carboDetector.getInsulinCarboRelation()))));
        tv_insulina.append(String.format("%su", new DecimalFormat("##.##").format(
                (carboDetector.getInsulinDose()))));


    }

    private void setTableDescriptors(){
        View table = findViewById(R.id.tv_tableDescriptors);
        String text = "Alimento";
        TextView descriptor = table.findViewById(R.id.foodDataDisplayTile);
//        descriptor.setTypeface(null, Typeface.BOLD);
//        descriptor.setText(text);

        text = "Peso";
        descriptor = table.findViewById(R.id.weightTile);
        descriptor.setTypeface(null, Typeface.BOLD);
        descriptor.setText(text);

        text = "CHO";
        descriptor = table.findViewById(R.id.carboTile);
        descriptor.setTypeface(null, Typeface.BOLD);
        descriptor.setText(text);
    }
}
