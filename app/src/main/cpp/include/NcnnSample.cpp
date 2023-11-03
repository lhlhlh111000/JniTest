//
// Created by cutie on 2023/11/3.
//

#include <jni.h>
#include <android/asset_manager_jni.h>

#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>

#include "net.h"
#include "benchmark.h"
#include "mat.h"

using namespace ncnn;

static UnlockedPoolAllocator g_blob_pool_allocator;
static PoolAllocator g_workspace_pool_allocator;

static Net bgnet;

extern "C" {

    JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
        __android_log_print(ANDROID_LOG_DEBUG, "NativeSample", "JNI_OnLoad");

        create_gpu_instance();

        return JNI_VERSION_1_6;
    }

    JNIEXPORT void JNI_OnUnload(JavaVM *vm, void *reserved) {

        __android_log_print(ANDROID_LOG_DEBUG, "NativeSample", "JNI_OnUnload");

        destroy_gpu_instance();
    }

    JNIEXPORT jboolean JNICALL
    Java_com_pig_android_test_NativeSample_init(JNIEnv *env, jobject thiz, jobject assetManager) {
        Option opt;
        opt.lightmode = true;
        opt.num_threads = 4;
        opt.blob_allocator = &g_blob_pool_allocator;
        opt.workspace_allocator = &g_workspace_pool_allocator;

        if (get_gpu_count() != 0) opt.use_vulkan_compute = true;

        AAssetManager* mgr = AAssetManager_fromJava(env, assetManager);
        const char* model_paths = "mobilenetv2.bin";
        const char* param_paths = "mobilenetv2.param";
        bgnet.opt = opt;
        int ret0 = bgnet.load_param(mgr, param_paths);
        int ret1 = bgnet.load_model(mgr, model_paths);
        __android_log_print(ANDROID_LOG_DEBUG, "NativeSample", "load %s ret0=%d ret1=%d", model_paths, ret0, ret1);

        return JNI_TRUE;
    }

    JNIEXPORT jboolean JNICALL
    Java_com_pig_android_test_NativeSample_processBg(JNIEnv *env, jobject thiz, jobject bmp, jboolean use_gpu) {

        if (use_gpu == JNI_TRUE && get_gpu_count() == 0) {
            return JNI_FALSE;
        }

        double start_time = get_current_time();

        AndroidBitmapInfo bitmap;
        AndroidBitmap_getInfo(env, bmp, &bitmap);
        if (bitmap.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
            return JNI_FALSE;
        }

        ncnn::Mat in = ncnn::Mat::from_android_bitmap(env, bmp, ncnn::Mat::PIXEL_RGB);
        cv::Mat rgb = cv::Mat::zeros(in.h, in.w, CV_8UC3);
        in.to_pixels(rgb.data, ncnn::Mat::PIXEL_RGB);

        int width = rgb.cols;
        int height = rgb.rows;
        ncnn::Mat in_resize = ncnn::Mat::from_pixels_resize(rgb.data, ncnn::Mat::PIXEL_RGB, rgb.cols,rgb.rows,512,512);
        const float meanVals[3] = { 127.5f, 127.5f,  127.5f };
        const float normVals[3] = { 0.0078431f, 0.0078431f, 0.0078431f };
        in_resize.substract_mean_normalize(meanVals, normVals);
        ncnn::Mat out;
        {
            ncnn::Extractor ex = bgnet.create_extractor();
            ex.set_vulkan_compute(use_gpu);
            ex.input("input", in_resize);
            ex.extract("output", out);
        }

        ncnn::Mat alpha;
        ncnn::resize_bilinear(out,alpha,width,height);
        cv::Mat blendImg = cv::Mat::zeros(cv::Size(width,height), CV_8UC3);

        const int bg_color[3] = {255, 255, 255};
        float* alpha_data = (float*)alpha.data;
        for (int i = 0; i < height; i++)
        {
            for (int j = 0; j < width; j++)
            {
                float alpha_ = alpha_data[i*width+j];
                blendImg.at < cv::Vec3b>(i, j)[0] = rgb.at < cv::Vec3b>(i, j)[0] * alpha_ + (1 - alpha_) * bg_color[0];
                blendImg.at < cv::Vec3b>(i, j)[1] = rgb.at < cv::Vec3b>(i, j)[1] * alpha_ + (1 - alpha_) * bg_color[1];
                blendImg.at < cv::Vec3b>(i, j)[2] = rgb.at < cv::Vec3b>(i, j)[2] * alpha_ + (1 - alpha_) * bg_color[2];
            }
        }

        ncnn::Mat blengImg_ncnn = ncnn::Mat::from_pixels(blendImg.data,ncnn::Mat::PIXEL_RGB,blendImg.cols,blendImg.rows);

        // ncnn to bitmap
        blengImg_ncnn.to_android_bitmap(env, bmp, ncnn::Mat::PIXEL_RGB);

        double elasped = ncnn::get_current_time() - start_time;
        __android_log_print(ANDROID_LOG_DEBUG, "NativeSample", "%.2fms", elasped);

        return JNI_TRUE;
    }
}