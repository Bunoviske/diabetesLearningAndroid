package com.example.bruno.diabeteslearning.Carbohydrate;

public class ImageFoodRegion extends FoodProperties {

    //TODO - variaveis a serem ENVIADAS para firebase
    public int regionPixeis;
    public float weight;
    public float carbo;
    public String foodName;

    public ImageFoodRegion(int regionPixeis, String foodName, FoodProperties foodDatabaseProperties) {
        this.regionPixeis = regionPixeis;
        this.foodName = foodName;
        super.carboRelation = foodDatabaseProperties.carboRelation;
        super.foodDensity = foodDatabaseProperties.foodDensity;
    }

}
