package com.example.bruno.diabeteslearning.ImgProc;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.android.Utils;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.CvType.CV_16S;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.COLOR_GRAY2BGR;
import static org.opencv.imgproc.Imgproc.MORPH_RECT;


public class Watershed {

    private Mat mImage = new Mat(), mMarkersMask = new Mat(),
            mMarkers = new Mat(), mWshed = new Mat(), mImageGray = new Mat();

    private int numberOfRegions = 0;

    private static String TAG = "Watershed";


    static {
        if (OpenCVLoader.initDebug()){
            Log.i(TAG, "Opencv OK Static");
        }
        else{
            Log.i(TAG, "Opencv Error");
        }
        System.loadLibrary("native-lib");
        System.loadLibrary("opencv_java3");
    }

    public void initWatershed(Bitmap image, Bitmap markersMask){

        Utils.bitmapToMat(image, mImage);
        Utils.bitmapToMat(markersMask, mMarkersMask);

        Imgproc.cvtColor(mImage,mImage,Imgproc.COLOR_BGRA2BGR);
        Imgproc.cvtColor(mMarkersMask,mMarkersMask,Imgproc.COLOR_BGRA2GRAY);
        Imgproc.cvtColor(mImage,mImageGray,Imgproc.COLOR_BGRA2GRAY);

        filterImage();
    }

    private void filterImage() {
        Imgproc.medianBlur(mImage,mImage,7);
    }

    public void runWatershed(){

        numberOfRegions = runWatershedFromNative(
                mImage.getNativeObjAddr(),
                mMarkersMask.getNativeObjAddr(),
                mMarkers.getNativeObjAddr(),
                mWshed.getNativeObjAddr());
        
        enhanceWshedEdges();

    }

    private void enhanceWshedEdges() {

        Mat wshedGray = new Mat();
        Imgproc.cvtColor(mWshed,wshedGray,COLOR_BGR2GRAY);
        Imgproc.Canny(wshedGray,wshedGray, 20,40);
        int dilation_size = 1;
        Imgproc.dilate(wshedGray,wshedGray,  Imgproc.getStructuringElement( MORPH_RECT,
                new Size( 2*dilation_size + 1, 2*dilation_size+1 ),
                new Point( dilation_size, dilation_size )));

        //so é possivel somar imagens com mesmo numero de canais
        Imgproc.cvtColor(mImageGray, mImageGray, COLOR_GRAY2BGR);
        Imgproc.cvtColor(wshedGray, wshedGray, COLOR_GRAY2BGR);

        Core.addWeighted(mWshed,0.3,mImageGray,0.7,0,mWshed);
        Core.add(mWshed,wshedGray,mWshed); //add pixels branco da borda

    }

    public Bitmap getmWshed() {
        Bitmap bitmap = Bitmap.createBitmap(mWshed.width(),mWshed.height(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mWshed,bitmap);
        return bitmap;
    }

    public int getNumberOfRegions(){
        return numberOfRegions;
    }

    public Mat getMarkers() {
        return mMarkers;
    }

    //public native void sobel(long mImage, long wshed);
    //public native String stringFromJNI();
    public native int runWatershedFromNative(long imageJava, long maskMarkersJava,
                                              long markersJava, long wshedJava);


}


