package com.example.bruno.diabeteslearning.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;

import com.example.bruno.diabeteslearning.ImagePaint.ImageViewCanvas;
import com.example.bruno.diabeteslearning.R;

import org.opencv.android.OpenCVLoader;

import java.io.IOException;


public class ImageActivity extends AppCompatActivity {

    private static String TAG = "ImageActivity";
    private ImageViewCanvas imageViewCanvas;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private String mCurrentPhotoPath;

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
        setContentView(R.layout.image_activity);

        configButton();
        configListView();

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.pref_key), Context.MODE_PRIVATE);
        Intent preferences_intent = new Intent(this, PreferencesActivity.class);

        if(sharedPreferences.getString(getString(R.string.pref_name_key), "").equals("")){
            startActivity(preferences_intent);
        }

//        Bitmap bitmap = getIntent().getParcelableExtra("bitmap");
//        showBitmap(bitmap);

        mCurrentPhotoPath = getIntent().getStringExtra("bitmapUri");
        showBitmap(getBitmap());
    }

    private Bitmap getBitmap() {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(mCurrentPhotoPath, opts);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(mCurrentPhotoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) :  ExifInterface.ORIENTATION_NORMAL;

        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2,
                (float) bm.getHeight() / 2);
        return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(),
                                    matrix, true);
    }


    private void showBitmap(Bitmap bitmap){

        if(bitmap != null){

            imageViewCanvas = findViewById(R.id.paintView);

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);


            int height = (int) (metrics.heightPixels*0.8);
            int width = metrics.widthPixels;

            //altura dp declarada no arquivo xml. N consegui extrair dinamicamente
            //int heightDp = 520;
            //int imageHeight = Math.round(heightDp * (metrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));

            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);

            imageViewCanvas.init(bitmap,  mRecyclerView);

        } else{

            Log.e(TAG, "Bitmap null");

            //TODO - LANÇAR TOAST DE ERRO OU PELO MENOS LOGAR

        }
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
                Intent activity = new Intent(ImageActivity.super.getBaseContext(),
                        DisplayDataActivity.class);
                activity.putStringArrayListExtra("selectedFoodsName",
                        imageViewCanvas.getSelectedFoodsName());
                activity.putIntegerArrayListExtra("selectedFoodsArea",
                        imageViewCanvas.getSelectedFoodsArea());
                startActivity(activity);
            }
        });
    }

    //public native String stringFromJNI();
}
