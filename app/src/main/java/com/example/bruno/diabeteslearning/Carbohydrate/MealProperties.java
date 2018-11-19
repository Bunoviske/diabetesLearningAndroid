package com.example.bruno.diabeteslearning.Carbohydrate;

import java.util.ArrayList;

public class MealProperties {

    //TODO - variaveis a serem ENVIADAS para o firebase
    protected float totalFoodWeight = 0;
    protected float insulinCarboRelation = 0;
    protected float totalCarbo = 0;
    protected float insulinDose = 0;
    //TODO - TIMESTAMP e BITMAP
    protected ArrayList<FoodRegion> foods = new ArrayList<>();

    public void setTotalFoodWeight(float totalFoodWeight) {
        this.totalFoodWeight = totalFoodWeight;
    }

    public void setInsulinCarboRelation(float insulinCarboRelation) {
        this.insulinCarboRelation = insulinCarboRelation;
    }

    public void setTotalCarbo(float totalCarbo) {
        this.totalCarbo = totalCarbo;
    }

    public void setInsulinDose(float insulinDose) {
        this.insulinDose = insulinDose;
    }

    public void setFoods(ArrayList<FoodRegion> foods) {
        this.foods = foods;
    }
}
