package com.example.bruno.diabeteslearning.Carbohydrate;

import android.util.Log;
import java.util.ArrayList;


public class CarboDetector {

    //TODO - variaveis a serem ENVIADAS para o firebase
    private float totalFoodWeigh;
    private float totalCarbo;

    private ArrayList<FoodRegion> foods = new ArrayList<>();
    private float constant;

    private static String TAG = "CarboDetector";

    public CarboDetector(ArrayList<String> selectedFoodsName,
                         ArrayList<Integer> selectedFoodsArea){

        for (int i = 0; i < selectedFoodsName.size(); i++){
            saveFoodRegion(selectedFoodsArea.get(i), selectedFoodsName.get(i));
        }
    }

    public void setTotalFoodWeigh(float totalFoodWeigh) {
        this.totalFoodWeigh = totalFoodWeigh;
    }

    public void saveFoodRegion(int regionPixeis, String foodName){

        float carboRelation = 0, foodDensity = 0;
        //TODO - CHAMAR FUNCAO QUE PEGA RELACAO DE CHO E DENSIDADE DO ALIMENTO NO FIREBASE

        foods.add(new FoodRegion(regionPixeis,carboRelation, foodDensity, foodName));

    }

    public void calculateCarbo(){

        float aux = 0;
        for(int i = 0; i < foods.size();i++){
            aux += (foods.get(i).foodDensity * foods.get(i).regionPixeis);
        }
        constant = totalFoodWeigh/aux;

        for(int i = 0; i < foods.size();i++){
            foods.get(i).weigh = foods.get(i).foodDensity * foods.get(i).regionPixeis * constant;
            foods.get(i).carbo = foods.get(i).weigh * foods.get(i).carboRelation;
            totalCarbo += foods.get(i).carbo;

            Log.i(TAG, foods.get(i).foodName + "  Peso: "
                    + foods.get(i).weigh + " Carbo: " + foods.get(i).carbo);

        }

        Log.i(TAG, "Total de carboidrato: " + totalCarbo);
    }

}
