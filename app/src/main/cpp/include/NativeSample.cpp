#include <jni.h>
#include <string>
#include <opencv2/opencv.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <opencv2/core/mat.hpp>

using namespace std;
using namespace cv;

extern "C"
JNIEXPORT jstring JNICALL
Java_com_pig_android_test_NativeSample_sayHello(JNIEnv *env, jobject thiz) {

    throw "Test error.";

    return (*env).NewStringUTF("hello.");
}

extern "C"
JNIEXPORT jintArray JNICALL
Java_com_pig_android_test_NativeSample_gray(JNIEnv *env, jobject thiz, jintArray pixels, jint w,
                                            jint h, jfloat per) {
    jint* p = env->GetIntArrayElements(pixels, NULL);
    if (p == NULL) {
        return 0;
    }

    Mat img(h, w, CV_8UC4, (unsigned char*) p);
    int n = img.channels();
    for (int i=0; i<h; i++) {
        uchar* rowsPtr = img.ptr<uchar>(i);
        for (int j=w*per; j<w; j++) {
            rowsPtr[n*j+1] = rowsPtr[n*j];
            rowsPtr[n*j+2] = rowsPtr[n*j];
        }
    }

    int size = w*h;
    jintArray result = env->NewIntArray(size);
    env->SetIntArrayRegion(result, 0, size, p);
    env->ReleaseIntArrayElements(pixels, p, 0);
    return result;
}
extern "C"
JNIEXPORT jintArray JNICALL
Java_com_pig_android_test_NativeSample_blur(JNIEnv *env, jobject thiz, jintArray pixels, jint w,
                                            jint h) {
    jint* p = env ->GetIntArrayElements(pixels, NULL);
    if (p == NULL) {
        return 0;
    }

    Mat img(h, w, CV_8UC4, (unsigned char*) p);
    GaussianBlur(img, img, Size(45, 13), 0, 0);

    int size = w*h;
    jintArray result = env->NewIntArray(size);
    env->SetIntArrayRegion(result, 0, size, p);
    env->ReleaseIntArrayElements(pixels, p, 0);
    return result;
}

bool isInCirclePoint(int cx, int cy, int x, int y, int r) {
    int dx = x - cx;
    int dy = y - cy;
    return (dx*dx + dy*dy) < r*r;
}

extern "C"
JNIEXPORT jintArray JNICALL
Java_com_pig_android_test_NativeSample_pointSee(JNIEnv *env, jobject thiz, jintArray blur_pixels,
                                                jintArray origin_pixels, jint w, jint h, jint x,
                                                jint y) {
    jint* pBlur = env -> GetIntArrayElements(blur_pixels, NULL);
    jint* pOrigin = env ->GetIntArrayElements(origin_pixels, NULL);
    if (pBlur == NULL || pOrigin == NULL) {
        return 0;
    }

    Mat blur(h, w, CV_8UC4, (unsigned char*) pBlur);
    Mat origin(h, w, CV_8UC4, (unsigned  char*) pOrigin);
    int n = blur.channels();
    for(int i=0; i<h; i++) {
        uchar* rowsBlurPtr = blur.ptr<uchar>(i);
        uchar* rowsOriginPtr = origin.ptr<uchar>(i);
        for(int j=0; j<w; j++) {
            if (isInCirclePoint(x, y, j, i, w/6)) {
                rowsBlurPtr[n*j] = rowsOriginPtr[n*j];
                rowsBlurPtr[n*j + 1] = rowsOriginPtr[n*j + 1];
                rowsBlurPtr[n*j + 2] = rowsOriginPtr[n*j + 2];
                rowsBlurPtr[n*j + 3] = rowsOriginPtr[n*j + 3];
            }
        }
    }

    int size = w*h;
    jintArray result = env->NewIntArray(size);
    env->SetIntArrayRegion(result, 0, size, pBlur);
    env->ReleaseIntArrayElements(blur_pixels, pBlur, 0);
    env->ReleaseIntArrayElements(origin_pixels, pOrigin, 0);
    return result;
}