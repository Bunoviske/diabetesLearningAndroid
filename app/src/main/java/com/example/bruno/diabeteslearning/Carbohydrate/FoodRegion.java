package com.example.bruno.diabeteslearning.Carbohydrate;

import java.io.Serializable;

public class FoodRegion implements Serializable {

    //TODO - variaveis a serem ENVIADAS para firebase
    public int regionPixeis;
    public float weight;
    public float carbo;
    public String foodName;

    //TODO - variaveis que devem ser RECUPERADAS do firebase
    public float carboRelation;
    public float foodDensity;

    public FoodRegion(int regionPixeis, float carboRelation, float foodDensity, String foodName) {
        this.regionPixeis = regionPixeis;
        this.carboRelation = carboRelation;
        this.foodDensity = foodDensity;
        this.foodName = foodName;
    }

    public int getRegionPixeis() {
        return regionPixeis;
    }

    public void setRegionPixeis(int regionPixeis) {
        this.regionPixeis = regionPixeis;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getCarbo() {
        return carbo;
    }

    public void setCarbo(float carbo) {
        this.carbo = carbo;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public float getCarboRelation() {
        return carboRelation;
    }

    public void setCarboRelation(float carboRelation) {
        this.carboRelation = carboRelation;
    }

    public float getFoodDensity() {
        return foodDensity;
    }

    public void setFoodDensity(float foodDensity) {
        this.foodDensity = foodDensity;
    }
}
