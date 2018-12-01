package com.example.bruno.diabeteslearning.ImagePaint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class ImageMagnifier{
    private PointF zoomPos;
    private Matrix matrix;
    private Paint paint;
    private BitmapShader shader;
    private int sizeOfMagnifier = 100;
    private int circleOffset = 150;
    private float zoom = 1.3f;

    public ImageMagnifier() {
        init();
    }

    private void init() {
        zoomPos = new PointF(0, 0);
        matrix = new Matrix();
        paint = new Paint();
    }

    public void setZoomPos(float x, float y) {
        zoomPos.x = x;
        zoomPos.y = y;
    }

    public void drawZoom(Canvas canvas, Bitmap bitmap) {

        shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint = new Paint();
        paint.setShader(shader);
        matrix.reset();
        matrix.postScale(zoom, zoom, zoomPos.x, zoomPos.y + ((2.5f*zoom)*circleOffset));
        paint.getShader().setLocalMatrix(matrix);
        canvas.drawCircle(zoomPos.x , zoomPos.y - circleOffset, sizeOfMagnifier, paint);
    }
}