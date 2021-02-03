package com.example.bruno.diabeteslearning.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.bruno.diabeteslearning.ImageCanvas.ImageSelectFoodCanvas;
import com.example.bruno.diabeteslearning.R;


public class FoodSelectionActivity extends AppCompatActivity {

    private final static String TAG = FoodSelectionActivity.class.getSimpleName();

    private ImageSelectFoodCanvas imageSelectFoodCanvas;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_selection);

//        Toolbar myToolbar = findViewById(R.id.my_toolbar);
//        setSupportActionBar(myToolbar);
//        ActionBar actionBar = this.getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
        //        Bitmap bitmap = getIntent().getParcelableExtra("bitmap");
//        showBitmap(bitmap);

        configButton();
        configListView();


        imageSelectFoodCanvas = findViewById(R.id.paintViewSelectFoodCanvas);
        imageSelectFoodCanvas.init(mRecyclerView);

    }



    private void configListView() {
        mRecyclerView = findViewById(R.id.foodsListView);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

    }

    private void configButton(){
        FloatingActionButton button = findViewById(R.id.nextPageButtonMainActivity);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!imageSelectFoodCanvas.getSelectedFoodsName().isEmpty()) {

                    Intent activity = new Intent(FoodSelectionActivity.super.getBaseContext(),
                            DisplayDataActivity.class);
                    activity.putStringArrayListExtra("selectedFoodsName",
                            imageSelectFoodCanvas.getSelectedFoodsName());
                    activity.putIntegerArrayListExtra("selectedFoodsArea",
                            imageSelectFoodCanvas.getSelectedFoodsArea());
                    startActivity(activity);
                }
                else{
                    Toast.makeText(FoodSelectionActivity.super.getBaseContext(),
                            "Selecione todos os alimentos",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
