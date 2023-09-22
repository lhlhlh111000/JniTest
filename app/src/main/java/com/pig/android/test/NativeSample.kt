package com.pig.android.test

class NativeSample {

    init {
        System.loadLibrary("native-lib")
    }

    external fun sayHello() : String

    external fun gray(pixels: IntArray, w: Int, h: Int, per: Float): IntArray

    external fun blur(pixels: IntArray, w: Int, h: Int): IntArray

    external fun pointSee(blurPixels: IntArray, originPixels: IntArray, w: Int, h: Int, x: Int, y: Int): IntArray
}