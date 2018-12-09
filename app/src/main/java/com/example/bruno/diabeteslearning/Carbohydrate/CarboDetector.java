package com.example.bruno.diabeteslearning.Carbohydrate;

import android.util.Log;

import com.example.bruno.diabeteslearning.Database.Firebase;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class CarboDetector extends MealProperties {

    public CarboDetector(ArrayList<String> selectedFoodsName,
                         ArrayList<Integer> selectedFoodsArea){

        setTimeStamp();
        saveFoodsRegions(selectedFoodsName,selectedFoodsArea);

    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public float getTotalFoodWeight(){
        return totalFoodWeight;
    }

    public float getInsulinDose(){
        return insulinDose;
    }

    public float getInsulinCarboRelation(){
        return insulinCarboRelation;
    }

    public float getTotalCarbo(){
        return totalCarbo;
    }

    public ArrayList<ImageFoodRegion> getFoods(){
        return foods;
    }

    public void clearFoodsCalculus(){
        totalCarbo = 0;
        totalFoodWeight = 0;
        insulinDose = 0;
        for (int i = 0; i < foods.size(); i++){
            foods.get(i).weight = 0;
            foods.get(i).carbo = 0;
        }
    }


    public void setTotalFoodWeight(float totalFoodWeight) {
        super.totalFoodWeight = totalFoodWeight;
    }

    public void setInsulinCarboRelation(float insulinCarboRelation){
        super.insulinCarboRelation = insulinCarboRelation;
        setInsulinDose();
    }

    private void setTimeStamp(){
        //timestamp é setted quando salva a imagem no firebase
        timeStamp = Firebase.getInstance().getTimeStamp();
    }

    private void setInsulinDose(){
        if (insulinCarboRelation > 0){
            insulinDose = totalCarbo/insulinCarboRelation;
        }
        else{
            insulinDose = 0;
        }
    }

    private HashMap<String, FoodProperties> getFoodPropertiesFromDatabase(){
        return Firebase.getInstance().getAllFoodsHashMap();

    }

    private void saveFoodsRegions(ArrayList<String> selectedFoodsName,
                                  ArrayList<Integer> selectedFoodsArea){

        HashMap<String, FoodProperties> foodsHash = getFoodPropertiesFromDatabase();

        for (int i = 0; i < selectedFoodsName.size(); i++) {
            FoodProperties foodProperties = foodsHash.get(selectedFoodsName.get(i));

            foods.add(new ImageFoodRegion(selectedFoodsArea.get(i),
                                          selectedFoodsName.get(i), foodProperties));
        }
    }

    public void calculateCarbo(){

        float aux = 0;
        for(int i = 0; i < foods.size();i++){
            aux += (foods.get(i).foodDensity * foods.get(i).regionPixeis);
        }
        float constant = totalFoodWeight / aux;
        totalCarbo = 0;

        String TAG = "CarboDetector";

        for(int i = 0; i < foods.size(); i++){
            foods.get(i).weight = foods.get(i).foodDensity * foods.get(i).regionPixeis * constant;
            foods.get(i).carbo = foods.get(i).weight * foods.get(i).carboRelation;
            totalCarbo += foods.get(i).carbo;

            Log.i(TAG, foods.get(i).foodName + "  Peso: "
                    + foods.get(i).weight + " Carbo: " + foods.get(i).carbo);

        }

        setInsulinDose();

        Log.i(TAG, "Total de carboidrato: " + totalCarbo);
        Log.i(TAG, "Dose de insulina: " + insulinDose);
    }

}
