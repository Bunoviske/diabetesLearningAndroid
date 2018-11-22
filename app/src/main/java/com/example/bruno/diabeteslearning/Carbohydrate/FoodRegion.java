package com.example.bruno.diabeteslearning.Carbohydrate;

import java.io.Serializable;

public class FoodRegion {

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

}
