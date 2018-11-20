package com.example.bruno.diabeteslearning.Carbohydrate;

import android.util.Log;

import java.io.Serializable;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;


public class CarboDetector extends MealProperties implements Serializable {

    private float constant;
    private static String TAG = "CarboDetector";

    public float getConstant() {
        return constant;
    }

    public void setConstant(float constant) {
        this.constant = constant;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public static String getTAG() {
        return TAG;
    }

    public static void setTAG(String TAG) {
        CarboDetector.TAG = TAG;
    }

    public CarboDetector(ArrayList<String> selectedFoodsName,

                         ArrayList<Integer> selectedFoodsArea){

        setTimeStamp();

        for (int i = 0; i < selectedFoodsName.size(); i++){
            saveFoodRegion(selectedFoodsArea.get(i), selectedFoodsName.get(i));
        }
    }

    public MealProperties getMealProperties(){ return this; }

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

    public ArrayList<FoodRegion> getFoods(){
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
        timeStamp = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.SHORT)
                .format(new Date());
    }

    private void setInsulinDose(){
        if (insulinCarboRelation > 0){
            insulinDose = totalCarbo/insulinCarboRelation;
        }
        else{
            insulinDose = 0;
        }
    }

    private void saveFoodRegion(int regionPixeis, String foodName){

        float carboRelation = (float)0.15;
        float foodDensity = (float)0.7;

        //TODO - CHAMAR FUNCAO QUE PEGA RELACAO DE CHO E DENSIDADE DO ALIMENTO NO FIREBASE

        foods.add(new FoodRegion(regionPixeis,carboRelation, foodDensity, foodName));

    }

    public void calculateCarbo(){

        float aux = 0;
        for(int i = 0; i < foods.size();i++){
            aux += (foods.get(i).foodDensity * foods.get(i).regionPixeis);
        }
        constant = totalFoodWeight/aux;
        totalCarbo = 0;

        for(int i = 0; i < foods.size();i++){
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
