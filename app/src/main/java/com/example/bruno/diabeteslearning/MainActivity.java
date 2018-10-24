package com.example.bruno.diabeteslearning;

import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.view.SurfaceView;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

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

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.oi);


        imageViewCanvas = findViewById(R.id.paintView);

        //int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        //altura dp declarada no arquivo xml. N consegui extrair dinamicamente
        int heightDp = 396;
        int height = Math.round(heightDp * (metrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));

        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);

        imageViewCanvas.init(bitmap);



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
