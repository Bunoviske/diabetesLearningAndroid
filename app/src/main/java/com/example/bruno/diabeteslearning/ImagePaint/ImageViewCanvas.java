package com.example.bruno.diabeteslearning.ImagePaint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import java.lang.Math;

import com.example.bruno.diabeteslearning.ImgProc.ContourProcessing;

import org.opencv.core.Core;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;



import java.util.ArrayList;
import java.util.List;



public class ImageViewCanvas extends View {

    private static String TAG = "ImageViewCanvas";

    public static int BRUSH_SIZE = 10; //espessura da linha
    public static final int RED_COLOR = Color.RED;
    public static final int GRAY_COLOR = Color.GRAY;

    private static final float TOUCH_TOLERANCE = 4;
    private static final double FINISHED_CONTOUR_TOLERANCE = 30;
    private boolean isAutoCompleteAvailable = false;
    private boolean isContourClosed = false;

    private ContourProcessing contourProcessing = new ContourProcessing();

    //pontos de referencia conforme o usuario move o dedo (usados para rastreamento e
    //autoComplete do contorno)
    private float mX, mY, initialX, initialY;

    private Path mPath;
    private FingerPath currentFp;
    private ArrayList<FingerPath> paths = new ArrayList<>();

    private Paint mPaint;
    private Bitmap mBitmap, mBitmapBackup, firstBitmap;
    private Canvas mCanvas;

    public ImageViewCanvas(Context context) {
        this(context, null);
    }

    public ImageViewCanvas(Context context, AttributeSet attrs) {
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

    public void init(Bitmap bitmap) {

        mBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        firstBitmap = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
        mCanvas = new Canvas(mBitmap);

//        mCanvas.drawColor(backgroundColor);
//        int height = metrics.heightPixels;
//        int width = metrics.widthPixels;
//        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

    }

    private void clearAllPaths() {
        paths.clear();
        mBitmap = firstBitmap.copy(Bitmap.Config.ARGB_8888, true);
        mCanvas = new Canvas(mBitmap);
        invalidate();
    }

    private void clearLastPath(){
        paths.remove(paths.size() - 1);
        mBitmap = mBitmapBackup.copy(Bitmap.Config.ARGB_8888, true);
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
        canvas.drawBitmap(mBitmap, 0, 0,null);
        canvas.restore();

    }

    private void getContourArea(){
        mPath.lineTo(initialX, initialY); //muda cor e desenha linha para o ponto inicial
        currentFp.setColor(GRAY_COLOR);

        double area = contourProcessing.getFoodContourArea();
        contourProcessing.clearContour();
        Log.i(TAG,"Area:" + area);

        if (area < 500.00){
            clearLastPath();
        }
    }

    private void touchStart(float x, float y) {
        mPath = new Path();
        currentFp = new FingerPath(RED_COLOR, BRUSH_SIZE, mPath);
        paths.add(currentFp);

        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
        initialX = x;
        initialY = y;
        mBitmapBackup = mBitmap.copy(Bitmap.Config.ARGB_8888, true);

    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        double dxi = Math.abs(initialX - x);
        double dyi = Math.abs(initialY - y);
        double distance = Math.sqrt((dxi*dxi) + (dyi*dyi));

        if (!isContourClosed && dxi > FINISHED_CONTOUR_TOLERANCE &&
                dyi > FINISHED_CONTOUR_TOLERANCE){
            isAutoCompleteAvailable = true;
        }

        if (isAutoCompleteAvailable && distance < FINISHED_CONTOUR_TOLERANCE){
            mX = x;
            mY = y;
            contourAutoComplete();
        }
        else if (!isContourClosed && (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE)) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void contourAutoComplete() {

        isAutoCompleteAvailable = false;
        isContourClosed = true;
        getContourArea();
    }

    private void touchUp(){

        double dxi = Math.abs(initialX - mX);
        double dyi = Math.abs(initialY - mY);
        double distance = Math.sqrt((dxi*dxi) + (dyi*dyi));

        if (!isContourClosed){
            if(distance < FINISHED_CONTOUR_TOLERANCE){
                getContourArea();
            }
            else {
                clearLastPath();
            }
        }

        // independente da forma que o contorno foi fechado (autoComplete
        // ou touchUp), inicializa-se novamente essas var para a proxima curva
        contourProcessing.clearContour();
        isAutoCompleteAvailable = false;
        isContourClosed = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        Point point = new Point(Math.round(x),Math.round(y));

        //so add pontos no contorno caso este ainda nao esteja fechado
        if (!isContourClosed) contourProcessing.addContourPoint(point);

        /* ha duas formas de extrair o contorno final: touchUp ou autoComplete.
        autoComplete só ocorre quando o usuario cria um curva que primeiro passa
        do limiar FINISHED_CONTOUR_TOLERANCE e depois retorna para o ponto inicial */

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN :
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE :
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP :
                touchUp();
                invalidate();
                break;
        }
        return true;
    }
}