package com.example.bruno.diabeteslearning.ImageCanvas;

import android.graphics.Path;

class FingerPath {

    public int color;
    public int strokeWidth;
    public Path path;

    public FingerPath(int color, int strokeWidth, Path path) {
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.path = path;
    }

    public void setColor(int color) {
        this.color = color;
    }
}