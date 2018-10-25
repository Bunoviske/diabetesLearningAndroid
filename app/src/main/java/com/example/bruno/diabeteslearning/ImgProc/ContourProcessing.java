package com.example.bruno.diabeteslearning.ImgProc;

import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class ContourProcessing {

    private List<Point> foodContour = new ArrayList<>();
    private MatOfPoint foodContourMat = new MatOfPoint();


    public void addContourPoint(Point point){
        foodContour.add(point);
    }

    public void clearContour(){
        foodContour.clear();
    }

    public double getFoodContourArea(){
        foodContourMat.fromList(foodContour);
        return Imgproc.contourArea(foodContourMat);
    }
}
