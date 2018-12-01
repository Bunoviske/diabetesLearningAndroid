package com.example.bruno.diabeteslearning.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Toast;

import com.example.bruno.diabeteslearning.ImagePaint.ImageViewCanvas;
import com.example.bruno.diabeteslearning.R;

import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class ImageActivity extends AppCompatActivity {

    private final static String TAG = ImageActivity.class.getSimpleName();
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
        setContentView(R.layout.activity_image);

//        Toolbar myToolbar = findViewById(R.id.my_toolbar);
//        setSupportActionBar(myToolbar);
//        ActionBar actionBar = this.getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }


        configButton();
        configListView();

//        Bitmap bitmap = getIntent().getParcelableExtra("bitmap");
//        showBitmap(bitmap);

        mCurrentPhotoPath = getIntent().getStringExtra("bitmapUri");
        showBitmap(getBitmap());
        deleteImageFile();
    }

    private Bitmap getBitmap() {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(mCurrentPhotoPath, opts);

        /**************** set image bitmap orientation *******************/
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

    private void deleteImageFile() {
        File file = new File(mCurrentPhotoPath);
        boolean delete = file.delete();
        if (!delete){
            Log.e(TAG, "Erro deletando imagem da memoria cache");
        }
        else{
            Log.i(TAG, "Imagem deletada com sucesso");
        }
    }

    private void showBitmap(Bitmap bitmap){

        if(bitmap != null){

            imageViewCanvas = findViewById(R.id.paintView);

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);

            int origHeight = bitmap.getHeight();
            int origWidth = bitmap.getWidth();
            float origProportion = origHeight/origWidth;

            int height = (int) (metrics.heightPixels*0.8); // 80% scaled
            int width = metrics.widthPixels;
            float proportion = height/width;

            float distortionPermitted = (float)0.1; //0% de distorção permitido

            if (Math.min(proportion,origProportion)/Math.max(proportion,origProportion)
                    >= 1 - distortionPermitted){
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
            }
            else {
                //TODO - O QUE FAZER PARA CELULARES QUE DEIXAM IMAGEM DISTORCIDA???
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
            }

            imageViewCanvas.init(bitmap,  mRecyclerView);

        } else{

            Log.e(TAG, "Bitmap null");

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

                if (!imageViewCanvas.getSelectedFoodsName().isEmpty()) {
                    Intent activity = new Intent(ImageActivity.super.getBaseContext(),
                            DisplayDataActivity.class);
                    activity.putStringArrayListExtra("selectedFoodsName",
                            imageViewCanvas.getSelectedFoodsName());
                    activity.putIntegerArrayListExtra("selectedFoodsArea",
                            imageViewCanvas.getSelectedFoodsArea());
                    startActivity(activity);
                }
                else{
                    Toast.makeText(ImageActivity.super.getBaseContext(),
                            "Circule pelo menos um alimento",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //public native String stringFromJNI();
}
