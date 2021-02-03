package com.example.bruno.diabeteslearning.ImgProc;

import android.graphics.Bitmap;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FoodRegionProcessing {

    private Mat mMarkers;
    private int[] foodAreas;
    private boolean[] alreadyClicked;

    //TODO - EXECUTAR EM BACKGROUND ESSA CLASSE

    public FoodRegionProcessing(Mat markers, int numberOfRegions) {
        mMarkers = markers;
        getAllFoodRegionsArea(markers,numberOfRegions);
    }

    private void getAllFoodRegionsArea(Mat markers, int numberOfRegions) {


        foodAreas = new int[numberOfRegions];
        alreadyClicked = new boolean[numberOfRegions];
        for (int i = 0; i < numberOfRegions; i++) {
            foodAreas[i] = 0;
            alreadyClicked[i] = false;
        }

        int size = (int) (markers.total() *  markers.channels());
        int[] imageArray = new int[size];
        markers.get(0, 0, imageArray);

        for(int i = 0; i < size; i++){

            if (imageArray[i] > 0){
                foodAreas[imageArray[i]-1]++;
            }
        }
    }

    public int getFoodRegionArea(Point point){

        int index = getFoodRegionIndex(point);

        if (index >= 0 && index < foodAreas.length) //se for uma regiao com index valido
            return foodAreas[index];
        else
            return 0;



    }

    public void setRegionIsAlreadyClicked(int index){
        alreadyClicked[index] = true;
    }
    public void clearRegionIsAlreadyClicked(int index){
        alreadyClicked[index] = false;
    }

    public boolean alreadyClicked(Point point) {

        int index = getFoodRegionIndex(point);
        return index < 0 || alreadyClicked[index];
        //caso a pessoa clique no branco (borda das regioes),
        //retorna como se alimento ja tivesse sido clicado

    }

    public int getFoodRegionIndex(Point point) {
        int[] pos = new int[1];
        mMarkers.get((int)point.y,(int)point.x,pos);
        return pos[0]-1;
    }
}
