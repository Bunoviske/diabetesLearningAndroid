package com.example.bruno.diabeteslearning.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.example.bruno.diabeteslearning.Adapters.HistoryAdapter;
import com.example.bruno.diabeteslearning.Carbohydrate.CarboDetector;
import com.example.bruno.diabeteslearning.Database.Firebase;
import com.example.bruno.diabeteslearning.Database.LogListener;
import com.example.bruno.diabeteslearning.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements HistoryAdapter.ResultsAdapterOnClickHandler {


    private final int MY_PERMISSIONS_REQUEST_CAMERA = 101;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PREFERENCES_ACTIVITY = 2;
    private SharedPreferences sharedPreferences;
    private String authName;

    private HistoryAdapter mHistoryAdapter;
    List<CarboDetector> entries = new ArrayList<>();
    String mCurrentPhotoPath;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        configListView();
        sharedPreferences =
                getSharedPreferences(getString(R.string.pref_key), Context.MODE_PRIVATE);


        Intent preferences_intent = new Intent(this, PreferencesActivity.class);

        //se for a primeira vez usando o app, so chama o
        //configDatabase depois do resultado da preferences activity
        if (sharedPreferences.getString(getString(R.string.pref_name_key), "").equals("")) {
            startActivityForResult(preferences_intent, PREFERENCES_ACTIVITY);
        } else {
            setAuthName();
            configDatabase(authName);
        }
    }

    private void configDatabase(String name) {

        Firebase.getInstance().setLogReference(getString(R.string.log));
        Firebase.getInstance().getLogAsync(name);
        Firebase.getInstance().setLogEventListener(new LogListener() {
            @Override
            public void onLogChanged(List<CarboDetector> log) {
                entries.clear();
                entries.addAll(log);
                if (null != mHistoryAdapter) {
                    mHistoryAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                .format(new Date());

        String imageFileName = "JPG_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private boolean isCameraPermissionOn() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);

            return false;
        }
        return true;
    }

    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    private void configListView() {
        mHistoryAdapter = new HistoryAdapter(this, this);
        RecyclerView mRecyclerView = findViewById(R.id.rv_history);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mHistoryAdapter);
        mHistoryAdapter.setHistoryList(entries);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    private void setAuthName(){

        String emailWithoutPoints = sharedPreferences.getString(getString(R.string.pref_email_key),"");
        emailWithoutPoints = emailWithoutPoints.replace("."," ");
        authName = sharedPreferences.getString(
                   getString(R.string.pref_name_key), "") + ": " + emailWithoutPoints;

    }

    public void takePictureButtonCallback(View v) {
        if (isCameraPermissionOn()) {
            dispatchTakePictureIntent();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Intent i = new Intent(this, ImageActivity.class);
            i.putExtra("bitmapUri", mCurrentPhotoPath);
            startActivity(i);
        } else if (requestCode == PREFERENCES_ACTIVITY && resultCode == RESULT_OK) {
            setAuthName();
            configDatabase(authName);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA && grantResults[0] == 0) {
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onClick(int position) {
        Intent i = new Intent(this, DetailsActivity.class);
        Bundle b = new Bundle();
        b.putSerializable("carboDetector", entries.get(position));
        i.putExtra("carboDetector", b);
        startActivity(i);
    }
}
