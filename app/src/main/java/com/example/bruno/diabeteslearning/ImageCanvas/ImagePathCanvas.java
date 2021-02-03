package com.example.bruno.diabeteslearning.ImageCanvas;
import com.example.bruno.diabeteslearning.ImgProc.Watershed;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import java.lang.Math;


import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;



public class ImagePathCanvas extends ImageCanvas {

    private static String TAG = "ImagePathCanvas";

    private static int BRUSH_SIZE = 7; //espessura da linha
    private static final int RED_COLOR = Color.RED;
    private static final int GRAY_COLOR = Color.GRAY;
    private static final int WHITE_COLOR = Color.WHITE;


    //pontos de referencia conforme o usuario move o dedo (usados para rastreamento e
    //autoComplete do contorno)
    private float mX, mY, initialX, initialY;

    private Path mPath;
    private FingerPath currentFp;
    private ArrayList<FingerPath> paths = new ArrayList<>();

    private Paint mPaint;
    private Bitmap mBitmap, firstBitmap;
    private Canvas mCanvas;

    private boolean isZooming = false;
    private boolean zoomMode = false;
    private ImageMagnifier imageMagnifier = new ImageMagnifier();

    //Watershed variables
    private Watershed watershed = new Watershed();
    private Bitmap markerMask;



    public ImagePathCanvas(Context context) {
        this(context, null);
    }

    public ImagePathCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(RED_COLOR);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xff);

    }

    public void callClearSpecificPath(final int index) { clearSpecificPath(index); }

    public void callClearAllPaths() { clearAllPaths(); }

    public void callClearLastPath(){clearSpecificPath(paths.size() - 1);}

    public void init(Bitmap bitmap) {

        mBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        firstBitmap = mBitmap.copy(Bitmap.Config.ARGB_8888, true);

        //cria mascara que irá conter as marcações do usuario
        markerMask = Bitmap.createBitmap(mBitmap.getWidth(),mBitmap.getHeight(),Bitmap.Config.ARGB_8888);

        mCanvas = new Canvas(mBitmap);
    }

    private void clearAllPaths() {
        paths.clear();
        mBitmap = firstBitmap.copy(Bitmap.Config.ARGB_8888, true);
        mCanvas = new Canvas(mBitmap);
        invalidate();
    }

    private void clearSpecificPath(final int index){
        paths.remove(index);
        mBitmap = firstBitmap.copy(Bitmap.Config.ARGB_8888, true);
        mCanvas = new Canvas(mBitmap);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();

        for (FingerPath fp : paths) {
            mPaint.setColor(fp.color);
            mPaint.setStrokeWidth(fp.strokeWidth);
            mPaint.setMaskFilter(null);
            mCanvas.drawPath(fp.path, mPaint);
        }

        if (isZooming && zoomMode){
            imageMagnifier.drawZoom(mCanvas,mBitmap);
        }

        if(mBitmap != null){
            canvas.drawBitmap(mBitmap, 0, 0,null);
        }
        canvas.restore();
    }

    public boolean findFoodRegions(){

        if (paths.isEmpty())
            return false;

        Log.i(TAG,"Chamando classe que lida com watershed");

        drawPathsOnMask();

        watershed.initWatershed(firstBitmap,markerMask);
        watershed.runWatershed();

        Bitmap wshedVisualization = watershed.getmWshed();
        mBitmap = wshedVisualization.copy(Bitmap.Config.ARGB_8888, true);
        mCanvas = new Canvas(mBitmap);
        invalidate();

        //update ImageCanvas abstract class to share resources with the another canvas
        ImageCanvas.wshedVisualization = wshedVisualization.copy(Bitmap.Config.ARGB_8888, true);
        ImageCanvas.wshedMarkers = watershed.getMarkers();
        ImageCanvas.numberOfRegions = watershed.getNumberOfRegions();

        return true;

        //mask visualization
//            mBitmap = markerMask.copy(Bitmap.Config.ARGB_8888, true);
//            mCanvas = new Canvas(mBitmap);
//            invalidate();
    }

    private void drawPathsOnMask() {
        markerMask.eraseColor(Color.BLACK);
        Canvas maskCanvas = new Canvas(markerMask);
        for (FingerPath fp : paths) {
            mPaint.setColor(fp.color);
            mPaint.setStrokeWidth(fp.strokeWidth);
            mPaint.setMaskFilter(null);
            maskCanvas.drawPath(fp.path, mPaint);
        }
        maskCanvas.drawBitmap(markerMask, 0, 0,null);
    }

    private void touchStart(float x, float y) {
        mPath = new Path();
        currentFp = new FingerPath(WHITE_COLOR, BRUSH_SIZE, mPath);
        paths.add(currentFp);

        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
        initialX = x;
        initialY = y;
        mPath.quadTo(mX, mY, mX+1, mY+1);

    }

    private void touchMove(float x, float y) {

        mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
        mX = x;
        mY = y;

    }


    private void touchUp(){

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        if (zoomMode) {
            Point point = new Point(Math.round(x), Math.round(y));
            imageMagnifier.setZoomPos(x, y);

            //reseta o bitmap que esta vinculado ao Canvas (senao nao reseta o drawCirle da lupa)
            mBitmap = firstBitmap.copy(Bitmap.Config.ARGB_8888, true);
            mCanvas = new Canvas(mBitmap);
        }



        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN :
                isZooming = true;
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE :
                isZooming = true;
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP :
                isZooming = false;
                touchUp();
                invalidate();
                break;

        }
        return true;
    }
}