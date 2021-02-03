package com.example.bruno.diabeteslearning.ImageCanvas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.bruno.diabeteslearning.ImgProc.FoodRegionProcessing;
import com.example.bruno.diabeteslearning.Dialogs.FoodRegionListener;
import com.example.bruno.diabeteslearning.Dialogs.FoodsDialogList;

import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.util.ArrayList;

public class ImageSelectFoodCanvas extends ImageCanvas{

    private static String TAG = "ImageSelectFoodCanvas";

    private FoodRegionProcessing foodRegionProcessing;
    private FoodsDialogList foodsDialogList;
    private ArrayList<Integer> selectedFoodsArea = new ArrayList<>();

    private static final int WHITE_COLOR = Color.WHITE;
    private Paint mPaint;
    private Bitmap mBitmap, firstBitmap;
    private Canvas mCanvas;

    public ImageSelectFoodCanvas(Context context) {
        this(context, null);
    }

    public ImageSelectFoodCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(WHITE_COLOR);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xff);

    }

    public ArrayList<Integer> getSelectedFoodsArea() {
        return selectedFoodsArea;
    }

    public ArrayList<String> getSelectedFoodsName() {
        return foodsDialogList.getSelectedFoodsName();
    }

    public void init(RecyclerView recyclerView) {

        Bitmap bitmap = ImageCanvas.wshedVisualization;
        Mat markers = ImageCanvas.wshedMarkers;
        int numberOfRegions = ImageCanvas.numberOfRegions;

        mBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        firstBitmap = mBitmap.copy(Bitmap.Config.ARGB_8888, true);

        mCanvas = new Canvas(mBitmap);
        foodRegionProcessing = new FoodRegionProcessing(markers,numberOfRegions);

        //area é passada para o dialog pois ele que controla se o nome do alimento é inserido
        //na lista, então ele deve controlar tambem se a area será inserida.
        foodsDialogList = new FoodsDialogList(selectedFoodsArea,recyclerView,super.getContext());

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        if(mBitmap != null){
            canvas.drawBitmap(mBitmap, 0, 0,null);
        }
        canvas.restore();
    }

    private void getRegionArea(Point point){

        if (!foodRegionProcessing.alreadyClicked(point)) {

            int area = foodRegionProcessing.getFoodRegionArea(point);

            Log.i(TAG, "Area:" + area);

            int index = foodRegionProcessing.getFoodRegionIndex(point);
            if (index < 0) //caso a pessoa clique no branco (borda das regioes), nao abre dialogList
                return;

            foodsDialogList.run(area, index);

            foodsDialogList.setFoodRegionClickListener( new FoodRegionListener() {

                @Override
                public void onRegionClick(int regionIndex) {
                    foodRegionProcessing.setRegionIsAlreadyClicked(regionIndex);
                    //todo - adicionar gray paint na regiao selecionada
                }
                @Override
                public void onRegionDeleted(int regionIndex) {
                    foodRegionProcessing.clearRegionIsAlreadyClicked(regionIndex);
                    //todo - remover gray paint na regiao selecionada
                }
            });
        }
        else{
            Toast.makeText(super.getContext(),
                    "Alimento ja selecionado",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void touchStart(float x, float y) {
        getRegionArea(new Point(x,y));
    }

    private void touchMove(float x, float y) {

    }

    private void touchUp(){

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

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
