package com.example.bruno.diabeteslearning.Database;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.bruno.diabeteslearning.Carbohydrate.CarboDetector;
import com.example.bruno.diabeteslearning.Carbohydrate.FoodProperties;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Firebase {

    private static final Firebase ourInstance = new Firebase();
    private LogListener logListener;
    private LocalFirebaseListener localFirebaseListener;

    private HashMap<String, FoodProperties> allFoodsHashMap =  new HashMap<>();
    private List<String> sortedKeys = new ArrayList<>();

    private FirebaseDatabase mDatabase;
    private DatabaseReference mLogReference;
    private DatabaseReference mFoodReference;
    private boolean isLocalFirebaseOn = false;

    public static Firebase getInstance() {
        return ourInstance;
    }

    private Firebase() {
        initFirebase();
    }

    private void initFirebase(){
        mDatabase = FirebaseDatabase.getInstance();
        mDatabase.setPersistenceEnabled(true);
        getAllFoodsAsync();
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
        mFoodReference.keepSynced(false);
    }

    public void setLogEventListener(LogListener logListener){
        this.logListener = logListener;
    }

    public void setLogReference(String firstChild){
        mLogReference = mDatabase.getReference().child(firstChild);
        mLogReference.keepSynced(false);
    }
    public void getLogAsync(String nome){
        DatabaseReference reference = mLogReference.child(nome);

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
}
