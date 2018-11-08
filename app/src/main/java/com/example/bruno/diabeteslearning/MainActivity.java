package com.example.bruno.diabeteslearning;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.bruno.diabeteslearning.Activities.DisplayDataActivity;
import com.example.bruno.diabeteslearning.Activities.PreferencesActivity;
import com.example.bruno.diabeteslearning.ImagePaint.ImageViewCanvas;

import org.opencv.android.OpenCVLoader;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";
    private ImageViewCanvas imageViewCanvas;

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


        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.pref_key), Context.MODE_PRIVATE);
        Intent preferences_intent = new Intent(this, PreferencesActivity.class);

        if(sharedPreferences.getString(getString(R.string.pref_name_key), "").equals("")){
            startActivity(preferences_intent);
        }

        Bitmap bitmap = getIntent().getParcelableExtra("bitmap");
        if(bitmap!=null){
            showBitmap(bitmap);
        }
    }


    private void showBitmap(Bitmap bitmap){

        if(bitmap != null){

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);


            imageViewCanvas = findViewById(R.id.paintView);

            //int height = metrics.heightPixels;
            int width = metrics.widthPixels;

            //altura dp declarada no arquivo xml. N consegui extrair dinamicamente
            int heightDp = 396;
            int imageHeight = Math.round(heightDp * (metrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));

            bitmap = Bitmap.createScaledBitmap(bitmap, width, imageHeight, true);

            //TODO - PEGAR PESO TOTAL DOS ALIMENTOS

            imageViewCanvas.init(bitmap,  150);

        } else{

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



    //public native String stringFromJNI();
}
