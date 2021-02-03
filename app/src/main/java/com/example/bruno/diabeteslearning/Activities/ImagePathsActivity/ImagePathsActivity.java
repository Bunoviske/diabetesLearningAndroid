package com.example.bruno.diabeteslearning.Activities.ImagePathsActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.bruno.diabeteslearning.Activities.FoodSelectionActivity;
import com.example.bruno.diabeteslearning.Database.Firebase;
import com.example.bruno.diabeteslearning.Database.UploadFileListener;
import com.example.bruno.diabeteslearning.ImageCanvas.ImagePathCanvas;
import com.example.bruno.diabeteslearning.R;

import java.io.File;
import java.io.IOException;


public class ImagePathsActivity extends AppCompatActivity {

    private final static String TAG = ImagePathsActivity.class.getSimpleName();

    private ImagePathCanvas imagePathCanvas;
    private ImageAdjustment imageAdjustment;
    private Bitmap image;
    private boolean isNextButtonAllowed = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_path);

//        Toolbar myToolbar = findViewById(R.id.my_toolbar);
//        setSupportActionBar(myToolbar);
//        ActionBar actionBar = this.getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }

        configButtons();

        String mCurrentPhotoPath = getIntent().getStringExtra("bitmapUri");
        imageAdjustment = new ImageAdjustment(mCurrentPhotoPath,this);
        image = imageAdjustment.getAdjustedImage();

        imagePathCanvas = findViewById(R.id.paintViewPathCanvas);
        imagePathCanvas.init(image);


    }

    @Override
    public void onStop(){
        imageAdjustment.deleteImageFile();
        super.onStop();

    }

    private void configButtons(){

        FloatingActionButton nextButton = findViewById(R.id.nextPageButtonMainActivity);
        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (isNextButtonAllowed) {

                    imageAdjustment.putImageInFirebaseStorage(image);

                    Intent activity = new Intent(ImagePathsActivity.super.getBaseContext(),
                            FoodSelectionActivity.class);
                    startActivity(activity);

                }
                else{
                    Toast.makeText(ImagePathsActivity.super.getBaseContext(),
                            "Clique em 'Achar alimentos' antes de prosseguir",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        Button processButton = findViewById(R.id.findRegionsButton);
        processButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //caso o processamento seja feito corretamente, é permitido ir para a proxima etapa
                isNextButtonAllowed = imagePathCanvas.findFoodRegions();

                if (!isNextButtonAllowed){
                    Toast.makeText(ImagePathsActivity.super.getBaseContext(),
                            "Selecione os alimentos",
                            Toast.LENGTH_LONG).show();
                }

            }
        });

        Button clearButton = findViewById(R.id.clearPathsButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                imagePathCanvas.callClearAllPaths();
            }
        });
    }
}
