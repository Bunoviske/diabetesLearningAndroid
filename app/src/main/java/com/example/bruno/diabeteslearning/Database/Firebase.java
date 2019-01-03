package com.example.bruno.diabeteslearning.Database;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.bruno.diabeteslearning.Carbohydrate.CarboDetector;
import com.example.bruno.diabeteslearning.Carbohydrate.FoodProperties;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Firebase {

    private static final Firebase ourInstance = new Firebase();
    private LogListener logListener;
    private LocalFirebaseListener localFirebaseListener;
    private UploadFileListener uploadFileListener;

    private HashMap<String, FoodProperties> allFoodsHashMap =  new HashMap<>();
    private List<String> sortedKeys = new ArrayList<>();
    private String authName;

    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private DatabaseReference mLogReference;
    private DatabaseReference mFoodReference;
    private boolean isLocalFirebaseOn = false;
    private String timeStamp;

    public static Firebase getInstance() {
        return ourInstance;
    }

    private Firebase() {
        initFirebase();
    }

    private void initFirebase(){
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mDatabase.setPersistenceEnabled(true);
        getAllFoodsAsync();
    }

    public void setAuthName(String name){
        authName = name;
    }

    public void retryFirebaseConnection(){
        initFirebase();
    }

    public ArrayList<String> getAllFoods() {
        return new ArrayList<>(sortedKeys);
    }

    public HashMap<String, FoodProperties> getAllFoodsHashMap() {
        return allFoodsHashMap;
    }

    /*****
    Chamado apenas uma vez, quando inicializa o app -> addListenerForSingleValueEvent
    *****/
    private void getAllFoodsAsync() {
        setFoodsReference();
        Query queryRef = mFoodReference.orderByValue();

        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){

                    allFoodsHashMap.clear();
                    Gson gson = new Gson();
                    for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                        FoodProperties entry = gson.fromJson(dataSnapshot1.getValue().toString(),
                                FoodProperties.class);
                        allFoodsHashMap.put(dataSnapshot1.getKey(),entry);
                    }
                    sortKeys();
                    isLocalFirebaseOn = true;
                    if (localFirebaseListener != null)
                        localFirebaseListener.onLocalFirebaseLoaded(true);
                }
                else{
                    if (localFirebaseListener != null)
                        localFirebaseListener.onLocalFirebaseLoaded(false);                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO: mostrar mensagem de erro
                Log.w("ui", "getAllFoodsAsync:onCancelled",
                        databaseError.toException());
                // ...
            }
        });
    }

    private void sortKeys() {
        sortedKeys = new ArrayList<>(allFoodsHashMap.keySet());
        Collections.sort(sortedKeys);
    }

    private void setFoodsReference() {
        mFoodReference = mDatabase.getReference().child("Foods");
        mFoodReference.keepSynced(true);
    }

    public void setLogEventListener(LogListener logListener){
        this.logListener = logListener;
    }

    public void setLogReference(String firstChild){
        mLogReference = mDatabase.getReference().child(firstChild);
        mLogReference.keepSynced(false);
    }
    public void getLogAsync(){
        DatabaseReference reference = mLogReference.child(authName);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<CarboDetector> entries = new ArrayList<>();

                if(dataSnapshot.getValue() != null){
                    Gson gson = new Gson();
                    for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                        CarboDetector entry = gson.fromJson(dataSnapshot1.getValue().toString(),
                                                            CarboDetector.class);
                        entries.add(entry);
                    }
                }
                logListener.onLogChanged(entries);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO: mostrar mensagem de erro
                Log.w("ui", "getLogAsync:onCancelled",
                        databaseError.toException());
                // ...
            }
        };
        reference.addValueEventListener(postListener);
    }

    public void addLogEntry(CarboDetector carboDetector, String name){
        Gson gson = new Gson();
        String json = gson.toJson(carboDetector);
        String timeStamp = carboDetector.getTimeStamp();
        mLogReference.child(name).child(timeStamp).setValue(json);
    }

    public void setLocalFirebaseListener(LocalFirebaseListener localFirebaseListener){
        this.localFirebaseListener = localFirebaseListener;
    }

    public boolean isLocalFirebaseOn() {
        return isLocalFirebaseOn;
    }

    public void setUploadFileListener(UploadFileListener uploadFileListener) {
        this.uploadFileListener = uploadFileListener;
    }

    public void saveImage(Bitmap image){

        setTimeStamp();
        mStorageRef = mStorage.getReference().child("FoodImages/" + authName +'/'
                                                    +getTimeStamp());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build();

        UploadTask uploadTask = mStorageRef.putBytes(data,metadata);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                uploadFileListener.onUploadFile(false);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                uploadFileListener.onUploadFile(true);
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });
    }

    private void setTimeStamp(){
        timeStamp = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.SHORT)
                .format(new Date());
    }

    public String getTimeStamp() {
        return timeStamp;
    }

}
