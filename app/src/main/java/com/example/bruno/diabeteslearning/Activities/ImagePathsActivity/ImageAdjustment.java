package com.example.bruno.diabeteslearning.Activities.ImagePathsActivity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.DisplayMetrics;
import android.util.Log;

import com.example.bruno.diabeteslearning.Database.Firebase;
import com.example.bruno.diabeteslearning.Database.UploadFileListener;

import java.io.File;
import java.io.IOException;

public class ImageAdjustment {

    private final static String TAG = ImageAdjustment.class.getSimpleName();
    private String mCurrentPhotoPath;
    private Context activityContext;


    ImageAdjustment(String currentPhotoPath, Context activityContext){

        this.mCurrentPhotoPath = currentPhotoPath;
        this.activityContext = activityContext;

    }


    public void putImageInFirebaseStorageAndDelete(Bitmap image) {

        if (image != null) {

//            int width = 240;
//            int height = 320;
//            if (image.getWidth() > image.getHeight()) {
//                width = 320;
//                height = 240;
//            }
//            Bitmap scaledImage = Bitmap.createScaledBitmap(image, width, height, true)

            Firebase.getInstance().setUploadFileListener(new UploadFileListener() {
                @Override
                public void onUploadFile(boolean sucess) {
                    if (sucess) {
                        Log.i(TAG, "Upload ok to Firebase Storage");
                        deleteImageFile();
                    } else {
                        //TODO - nao sei o que fazer aqui. Deletar mesmo assim?
                        Log.e(TAG, "Upload error to Firebase Storage");
                        deleteImageFile();
                    }
                }
            });
            Firebase.getInstance().saveImage(image);
        }
    }

    public Bitmap getAdjustedImage(){
        return showBitmap(getBitmap());
    }


    private Bitmap getBitmap() {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(mCurrentPhotoPath, opts);

        /**************** set image bitmap orientation *******************/
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(mCurrentPhotoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) :  ExifInterface.ORIENTATION_NORMAL;

        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2,
                (float) bm.getHeight() / 2);

        //se a imagem for tirada no modo paisagem, rotaciona ela novamente
        if (bm.getWidth() > bm.getHeight()){
            matrix.setRotate(90, (float) bm.getWidth() / 2,
                    (float) bm.getHeight() / 2);
        }

        return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(),
                matrix, true);

    }


    public void deleteImageFile() {
        File file = new File(mCurrentPhotoPath);
        boolean delete = file.delete();
        if (!delete){
            Log.e(TAG, "Erro deletando imagem da memoria");
            Log.e(TAG, "deleteImageFile: " + mCurrentPhotoPath);
        }
        else{
            Log.i(TAG, "Imagem deletada com sucesso");
        }
    }

    private Bitmap showBitmap(Bitmap bitmap){

        if(bitmap != null){

            DisplayMetrics metrics = new DisplayMetrics();
            ((Activity)activityContext).getWindowManager().getDefaultDisplay().getMetrics(metrics);

            int origHeight = bitmap.getHeight();
            int origWidth = bitmap.getWidth();
            float origProportion = origHeight/origWidth;

            int height = (int)(metrics.heightPixels*0.75); // 75% scaled
            int width = metrics.widthPixels;
            float proportion = height/width;

            float distortionPermitted = (float)0.1; //10% de distorção permitido

            if (Math.min(proportion,origProportion)/Math.max(proportion,origProportion)
                    >= 1 - distortionPermitted){
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
            }
            else {
                //TODO - O QUE FAZER PARA CELULARES QUE DEIXAM IMAGEM DISTORCIDA???
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
            }

        } else{
            Log.e(TAG, "Bitmap null");
        }

        return bitmap;

    }

}
