package com.example.bruno.diabeteslearning.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.bruno.diabeteslearning.Adapters.DataDisplayAdapter;
import com.example.bruno.diabeteslearning.Carbohydrate.CarboDetector;
import com.example.bruno.diabeteslearning.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

public class DisplayDataActivity extends Activity {

    private CarboDetector carboDetector;
    private Context context;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displaydata);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.pref_key), Context.MODE_PRIVATE);
        String nome = sharedPreferences.getString(getString(R.string.pref_name_key), "");
        String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        mDatabaseReference = database.getReference().child(nome);
        mDatabaseReference = mDatabaseReference.child(timeStamp);


        context = this;

        carboDetector = new CarboDetector(
                getIntent().getStringArrayListExtra("selectedFoodsName"),
                getIntent().getIntegerArrayListExtra("selectedFoodsArea"));

        configListView();
        configButton();
        displayData();
        setTableDescriptors();

        EditText weightEditText = findViewById(R.id.totalWeight);
        EditText carboRelEditText = findViewById(R.id.carboRelation);

        carboRelEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals(""))
                    carboDetector.setInsulinCarboRelation(0);
                else
                    carboDetector.setInsulinCarboRelation(Float.parseFloat(s.toString()));
                displayData();
            }
        });

        weightEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")){
                    carboDetector.setTotalFoodWeight(Float.parseFloat(s.toString()));
                    carboDetector.calculateCarbo();
                }
                else{
                    carboDetector.clearFoodsCalculus();
                }
                displayData();
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
        textView.setTypeface(null, Typeface.BOLD);

        String text = "Peso total: " + Math.round(carboDetector.getTotalFoodWeight()) + "g\n\n";

        text += "Carboidrato total: " + Math.round(carboDetector.getTotalCarbo()) + "g\n\n";

        text += "Dose de insulina: " + Math.round(carboDetector.getInsulinDose()) + "u\n\n";


        mAdapter.notifyDataSetChanged();
        //sinaliza que lista foi atualizada (na maioria das vezes que entrar nessa funcao a lista
        //vai ter sido atualizada)

        textView.setText(text);
    }

    private void setTableDescriptors(){
        View table = findViewById(R.id.tableDescriptors);
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

    private void configButton() {
        FloatingActionButton imageButton = findViewById(R.id.nextPageButtonDisplayActivity);
        imageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO - SALVAR NO FIREBASE A CLASSE MEALPROPERTIES QUANDO SAIR DA PAGINA

                Gson gson = new Gson();
                String json = gson.toJson(carboDetector.getMealProperties());
                mDatabaseReference.setValue(json);
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
