#include <jni.h>
#include <string>
#include "jni_bridge.h"
#include <opencv2/stitching.hpp>



using namespace std;
using namespace cv;

extern "C" {
JNIEXPORT jstring JNICALL
Java_com_vlusi_klintelligent_MainActivity_stringFromJNI(JNIEnv *env, jobject instance) {
    std::string hello = "Hello from C++";

    return env->NewStringUTF(hello.c_str());
}



JNIEXPORT jobject JNICALL
Java_com_vlusi_klintelligent_nativeJni_NdkProc_stitchImage0(JNIEnv *env, jclass type,
                                                            jobject srcImg1, jobject srcImg2,
                                                            jobject srcImg3, jobject srcImg4) {

    // TODO

    //get c++ Mat from Java
    Mat* src1=getNativeMat(env,srcImg1);
    Mat* src2=getNativeMat(env,srcImg2);
    Mat* src3=getNativeMat(env,srcImg3);
    Mat* src4=getNativeMat(env,srcImg4);

    //Stitch images
    Mat blendImg;
    Stitcher stitcher=Stitcher::createDefault(true);
    Stitcher::Status status=stitcher.stitch(vector<Mat>{*src1,*src2,*src3,*src4},blendImg);
    if(status==Stitcher::Status::OK){
        return getJavaMat(env,blendImg);
    } else{
        return NULL;
    }

}



JNIEXPORT jobject JNICALL
Java_com_vlusi_klintelligent_nativeJni_NdkProc_stitchImage1(JNIEnv *env, jobject instance,
                                                            jobject srcImg1, jobject srcImg2,
                                                            jobject srcImg3, jobject srcImg4,
                                                            jobject srcImg5, jobject srcImg6) {

    // TODO 180
    //get c++ Mat from Java
    Mat* src1=getNativeMat(env,srcImg1);
    Mat* src2=getNativeMat(env,srcImg2);
    Mat* src3=getNativeMat(env,srcImg3);
    Mat* src4=getNativeMat(env,srcImg4);
    Mat* src5=getNativeMat(env,srcImg5);
    Mat* src6=getNativeMat(env,srcImg6);

    //Stitch images
    Mat blendImg;
    Stitcher stitcher=Stitcher::createDefault(true);
    Stitcher::Status status=stitcher.stitch(vector<Mat>{*src1,*src2,*src3,*src4,*src5,*src6},blendImg);
    if(status==Stitcher::Status::OK){
        return getJavaMat(env,blendImg);
    } else{
        return NULL;
    }

}

JNIEXPORT jobject JNICALL
Java_com_vlusi_klintelligent_nativeJni_NdkProc_stitchImage2(JNIEnv *env, jobject instance,
                                                            jobject srcImg1, jobject srcImg2,
                                                            jobject srcImg3, jobject srcImg4,
                                                            jobject srcImg5, jobject srcImg6,
                                                            jobject srcImg7, jobject srcImg8,
                                                            jobject srcImg9) {

    // TODO rectangle
    //get c++ Mat from Java
    Mat* src1=getNativeMat(env,srcImg1);
    Mat* src2=getNativeMat(env,srcImg2);
    Mat* src3=getNativeMat(env,srcImg3);
    Mat* src4=getNativeMat(env,srcImg4);
    Mat* src5=getNativeMat(env,srcImg5);
    Mat* src6=getNativeMat(env,srcImg6);
    Mat* src7=getNativeMat(env,srcImg7);
    Mat* src8=getNativeMat(env,srcImg8);
    Mat* src9=getNativeMat(env,srcImg9);

    //Stitch images
    Mat blendImg;
    Stitcher stitcher=Stitcher::createDefault(true);
    Stitcher::Status status=stitcher.stitch(vector<Mat>{*src1,*src2,*src3,*src4,*src5,*src6,*src7,*src8,*src9},blendImg);
    if(status==Stitcher::Status::OK){
        return getJavaMat(env,blendImg);
    } else{
        return NULL;
    }



}

JNIEXPORT jobject JNICALL
Java_com_vlusi_klintelligent_nativeJni_NdkProc_stitchImage3(JNIEnv *env, jobject instance,
                                                            jobject srcImg1, jobject srcImg2,
                                                            jobject srcImg3, jobject srcImg4,
                                                            jobject srcImg5, jobject srcImg6,
                                                            jobject srcImg7, jobject srcImg8,
                                                            jobject srcImg9, jobject srcImg10) {

    // TODO  360
    //get c++ Mat from Java
    Mat* src1=getNativeMat(env,srcImg1);
    Mat* src2=getNativeMat(env,srcImg2);
    Mat* src3=getNativeMat(env,srcImg3);
    Mat* src4=getNativeMat(env,srcImg4);
    Mat* src5=getNativeMat(env,srcImg5);
    Mat* src6=getNativeMat(env,srcImg6);
    Mat* src7=getNativeMat(env,srcImg7);
    Mat* src8=getNativeMat(env,srcImg8);
    Mat* src9=getNativeMat(env,srcImg9);
    Mat* src10=getNativeMat(env,srcImg10);

    //Stitch images
    Mat blendImg;
    Stitcher stitcher=Stitcher::createDefault(true);
    Stitcher::Status status=stitcher.stitch(vector<Mat>{*src1,*src2,*src3,*src4,*src5,*src6,*src7,*src8,*src9,*src10},blendImg);
    if(status==Stitcher::Status::OK){
        return getJavaMat(env,blendImg);
    } else{
        return NULL;
    }

}




}