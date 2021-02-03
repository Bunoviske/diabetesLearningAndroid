package com.example.bruno.diabeteslearning.Carbohydrate;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class MealProperties implements Serializable {

    protected float totalFoodWeight = 0;
    protected float insulinCarboRelation = 0;
    protected float totalCarbo = 0;
    protected float insulinDose = 0;
    protected String timeStamp = "";
    protected ArrayList<ImageFoodRegion> foods = new ArrayList<>();

}
