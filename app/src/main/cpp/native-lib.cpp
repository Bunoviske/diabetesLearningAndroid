#include <jni.h>
#include <string>
#include <opencv2/opencv.hpp>
#include <iostream>

using namespace cv;
using namespace std;

//K = 18
Vec3b colorTab[] =
{

    Vec3b(0, 0, 255),
    Vec3b(0,255,0),
    Vec3b(255, 0, 0),

    Vec3b(255,0,255),
    Vec3b(0,255,255),
    Vec3b(255,255,0),

    Vec3b(255,100,100),
    Vec3b(100,0,255),
    Vec3b(100,255,0),
    Vec3b(255,0,100),

    Vec3b(100,255,100),
    Vec3b(100,100,255),


    Vec3b(255,255,100),
    Vec3b(255,100,255),
    Vec3b(100,255,255),


    Vec3b(0,255,100),
    Vec3b(255,100,0),
    Vec3b(0,100,255),

};

extern "C" JNIEXPORT jstring

JNICALL
Java_com_example_bruno_diabeteslearning_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_example_bruno_diabeteslearning_ImgProc_Watershed_runWatershedFromNative(JNIEnv *env,
                                                                                 jobject instance,
                                                                                 jlong imageJava,
                                                                                 jlong maskMarkersJava,
                                                                                 jlong markersJava,
                                                                                 jlong wshedJava) {

    Mat &maskMarkers = *(Mat*)maskMarkersJava;
    Mat &img0 = *(Mat*)imageJava;
    Mat &markers = *(Mat*)markersJava;
    Mat &wshed = *(Mat*)wshedJava;

    int i, j, compCount = 0; //NUMEROS DE REGIOES DIFERENTES MARCADAS (compCount)!
    vector<vector<Point> > contours;
    vector<Vec4i> hierarchy;

    findContours(maskMarkers, contours, hierarchy, RETR_CCOMP, CHAIN_APPROX_SIMPLE);
    if( contours.empty() )
        return 0;

    markers = Mat(maskMarkers.size(), CV_32S, Scalar(0));

    int idx = 0;
    for( ; idx >= 0; idx = hierarchy[idx][0], compCount++ ){
        //preenche dentro do contorno. essa etapa foi feita para detectar a qtd de contornos
        drawContours(markers, contours, idx, Scalar::all(compCount+1), -1, 8, hierarchy, INT_MAX);
    }
    if (compCount == 0)
        return 0;


//    if (compCount > 18) {
//
//        vector<Vec3b> colorTab;
//
//        for (i = 0; i < compCount; i++) {
//            int b = theRNG().uniform(0, 255);
//            int g = theRNG().uniform(0, 255);
//            int r = theRNG().uniform(0, 255);
//
//            colorTab.push_back(Vec3b((uchar) b, (uchar) g, (uchar) r));
//        }
//    }

    watershed( img0, markers );

    wshed = Mat(markers.size(), CV_8UC3);

    // paint the watershed image
    for( i = 0; i < markers.rows; i++ )
        for( j = 0; j < markers.cols; j++ )
        {
            int index = markers.at<int>(i,j);
            if( index == -1 ) { //representa as bordas
                wshed.at<Vec3b>(i, j) = Vec3b(255, 255, 255);
            }
            else if( index <= 0 || index > compCount ){
                wshed.at<Vec3b>(i,j) = Vec3b(0,0,0); //erro
            }
            else { //regiao segmentada
                wshed.at<Vec3b>(i, j) = colorTab[index - 1];
            }
        }

    return compCount;

}


extern "C"
JNIEXPORT void JNICALL
Java_com_example_bruno_diabeteslearning_ImgProc_Watershed_sobel(JNIEnv *env, jobject instance,
                                                                jlong mImage,
                                                                jlong sobel) {


    Mat &img = *(Mat*)mImage;
    Mat &sobelImg = *(Mat*)sobel;

    int ddepth = CV_16S;
    int scale = 1;
    int delta = 0;
    Mat grad,src_gray;

    cvtColor(img,src_gray,COLOR_BGR2GRAY);

    Mat grad_x, grad_y;
    Mat abs_grad_x, abs_grad_y;

    /// Gradient X
    Sobel( src_gray, grad_x, ddepth, 1, 0, 3, scale, delta, BORDER_DEFAULT );
    convertScaleAbs( grad_x, abs_grad_x );

    /// Gradient Y
    Sobel( src_gray, grad_y, ddepth, 0, 1, 3, scale, delta, BORDER_DEFAULT );
    convertScaleAbs( grad_y, abs_grad_y );

    /// Total Gradient (approximate)
    addWeighted( abs_grad_x, 0.5, abs_grad_y, 0.5, 0, grad );

    sobelImg = grad.clone();


}