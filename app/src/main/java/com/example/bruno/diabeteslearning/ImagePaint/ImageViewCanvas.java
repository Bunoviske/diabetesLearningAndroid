package com.example.bruno.diabeteslearning.ImagePaint;
import com.example.bruno.diabeteslearning.Carbohydrate.CarboDetector;
import com.example.bruno.diabeteslearning.ImgProc.ContourProcessing;
import com.example.bruno.diabeteslearning.UserIO.FoodsDialogList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Magnifier;

import java.lang.Math;


import org.opencv.core.Point;
import java.util.ArrayList;



public class ImageViewCanvas extends View {

    private static String TAG = "ImageViewCanvas";

    private static int BRUSH_SIZE = 10; //espessura da linha
    private static final int RED_COLOR = Color.RED;
    private static final int GRAY_COLOR = Color.GRAY;

    private static final float TOUCH_TOLERANCE = 4;
    private static final double FINISHED_CONTOUR_TOLERANCE = 40;
    private boolean isAutoCompleteAvailable = false;
    private boolean isContourClosed = false;

    private ContourProcessing contourProcessing;
    private FoodsDialogList foodsDialogList;
    private ArrayList<Integer> selectedFoodsArea = new ArrayList<>();

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
    private boolean zoomMode = true;
    private ImageMagnifier imageMagnifier = new ImageMagnifier();


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


    public void removeSpecificAreaFromPath(final int index){selectedFoodsArea.remove(index);}

    public void callClearSpecificPath(final int index) { clearSpecificPath(index); }

    public void callClearLastPath(){clearSpecificPath(paths.size() - 1);}

    public ArrayList<Integer> getSelectedFoodsArea() {return selectedFoodsArea; }

    public ArrayList<String> getSelectedFoodsName() {
        return foodsDialogList.getSelectedFoodsName();
    }

    public void init(Bitmap bitmap, RecyclerView recyclerView) {

        mBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        firstBitmap = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
        mCanvas = new Canvas(mBitmap);
        contourProcessing = new ContourProcessing();
        foodsDialogList = new FoodsDialogList(this,selectedFoodsArea,recyclerView);

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

    private void getContourArea(){
        mPath.lineTo(initialX, initialY); //muda cor e desenha linha para o ponto inicial
        currentFp.setColor(GRAY_COLOR);

        double area = contourProcessing.getFoodContourArea();
        contourProcessing.clearContour();


        Log.i(TAG,"Area:" + area);

        if (area < 500.00){
            clearSpecificPath(paths.size() - 1);
        }
        else {
            //area é passada para o dialog pois ele que controla se o nome do alimento é inserido
            //na lista, então ele deve controlar tambem se a area será inserida.
            foodsDialogList.run(super.getContext(), (int)area);
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
        mPath.quadTo(mX, mY, mX+1, mY+1);

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
        isZooming = false;
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
                clearSpecificPath(paths.size() - 1);
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
        imageMagnifier.setZoomPos(x,y);

        //reseta o bitmap que esta vinculado ao Canvas (senao nao reseta o drawCirle da lupa)
        mBitmap = firstBitmap.copy(Bitmap.Config.ARGB_8888, true);
        mCanvas = new Canvas(mBitmap);

        //so add pontos no contorno caso este ainda nao esteja fechado
        if (!isContourClosed) contourProcessing.addContourPoint(point);

        /* ha duas formas de extrair o contorno final: touchUp ou autoComplete.
        autoComplete só ocorre quando o usuario cria um curva que primeiro passa
        do limiar FINISHED_CONTOUR_TOLERANCE e depois retorna para o ponto inicial */

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