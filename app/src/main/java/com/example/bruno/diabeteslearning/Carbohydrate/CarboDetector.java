package com.example.bruno.diabeteslearning.Carbohydrate;

import android.util.Log;
import java.util.ArrayList;


public class CarboDetector extends MealProperties{

    private float constant;

    private static String TAG = "CarboDetector";

    public CarboDetector(ArrayList<String> selectedFoodsName,
                         ArrayList<Integer> selectedFoodsArea){

        for (int i = 0; i < selectedFoodsName.size(); i++){
            saveFoodRegion(selectedFoodsArea.get(i), selectedFoodsName.get(i));
        }
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

    public ArrayList<FoodRegion> getFoods(){
        return foods;
    }


    public void setTotalFoodWeight(float totalFoodWeight) {
        super.totalFoodWeight = totalFoodWeight;
    }

    public void setInsulinCarboRelation(float insulinCarboRelation){
        super.insulinCarboRelation = insulinCarboRelation;
    }

    private void saveFoodRegion(int regionPixeis, String foodName){

        float carboRelation = 0, foodDensity = 0;
        //TODO - CHAMAR FUNCAO QUE PEGA RELACAO DE CHO E DENSIDADE DO ALIMENTO NO FIREBASE

        foods.add(new FoodRegion(regionPixeis,carboRelation, foodDensity, foodName));

    }

    public void calculateCarbo(){

        float aux = 0;
        for(int i = 0; i < foods.size();i++){
            aux += (foods.get(i).foodDensity * foods.get(i).regionPixeis);
        }
        constant = totalFoodWeight/aux;

        for(int i = 0; i < foods.size();i++){
            foods.get(i).weight = foods.get(i).foodDensity * foods.get(i).regionPixeis * constant;
            foods.get(i).carbo = foods.get(i).weight * foods.get(i).carboRelation;
            totalCarbo += foods.get(i).carbo;

            Log.i(TAG, foods.get(i).foodName + "  Peso: "
                    + foods.get(i).weight + " Carbo: " + foods.get(i).carbo);

        }

        if (insulinCarboRelation > 0)
            insulinDose = totalCarbo/insulinCarboRelation;

        Log.i(TAG, "Total de carboidrato: " + totalCarbo);
        Log.i(TAG, "Dose de insulina: " + insulinDose);
    }

}
