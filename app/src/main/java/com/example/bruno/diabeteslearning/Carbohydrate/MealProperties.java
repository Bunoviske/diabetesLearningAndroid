package com.example.bruno.diabeteslearning.Carbohydrate;

import java.util.ArrayList;

public class MealProperties {

    //TODO - variaveis a serem ENVIADAS para o firebase
    protected float totalFoodWeight = 0;
    protected float insulinCarboRelation = 0;
    protected float totalCarbo = 0;
    protected float insulinDose = 0;
    protected String timeStamp = "";
    protected ArrayList<FoodRegion> foods = new ArrayList<>();

    //TODO - SALVAR BITMAP??

    public MealProperties getMealProperties(){
        return this;
    }

}
