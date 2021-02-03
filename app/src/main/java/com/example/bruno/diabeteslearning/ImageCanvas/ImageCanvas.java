package com.example.bruno.diabeteslearning.ImageCanvas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import org.opencv.core.Mat;

public abstract class ImageCanvas extends View {

    protected static Bitmap wshedVisualization;
    protected static Mat wshedMarkers;
    protected static int numberOfRegions;

    public ImageCanvas(Context context) {
        this(context, null);
    }

    public ImageCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

}

