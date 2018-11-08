package com.example.bruno.diabeteslearning;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.bruno.diabeteslearning.Activities.DisplayDataActivity;
import com.example.bruno.diabeteslearning.Adapters.FoodsListViewAdapter;
import com.example.bruno.diabeteslearning.ImagePaint.ImageViewCanvas;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";
    private ImageViewCanvas imageViewCanvas;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    static {
        if (OpenCVLoader.initDebug()){
            Log.i(TAG, "Opencv OK Static");
        }
        else{
            Log.i(TAG, "Opencv Error");
        }
        System.loadLibrary("native-lib");
        System.loadLibrary("opencv_java3");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setButton();
        setListView();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.oi);


        imageViewCanvas = findViewById(R.id.paintView);

        //int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        //altura dp declarada no arquivo xml. N consegui extrair dinamicamente
        int heightDp = 396;
        int imageHeight = Math.round(heightDp * (metrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));

        bitmap = Bitmap.createScaledBitmap(bitmap, width, imageHeight, true);


        imageViewCanvas.init(bitmap,  mRecyclerView);

    }

    @Override
    protected void onResume(){
        super.onResume();
        if (OpenCVLoader.initDebug()){
            Log.i(TAG, "Opencv OK Resume");
        }
        else{
            Log.i(TAG, "Opencv Error");
        }
    }

    private void setListView() {
        mRecyclerView = findViewById(R.id.foodsListView);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

    }

    private void setButton(){
        Button button = findViewById(R.id.nextPageButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent activity = new Intent(MainActivity.super.getBaseContext(),
                        DisplayDataActivity.class);
                activity.putStringArrayListExtra("selectedFoodsName",
                        imageViewCanvas.getSelectedFoodsName());
                activity.putIntegerArrayListExtra("selectedFoodsArea",
                        imageViewCanvas.getSelectedFoodsArea())
                startActivity(activity);
            }
        });
    }

    //public native String stringFromJNI();
}
