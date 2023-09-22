package com.pig.android.test

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory

fun Activity.getBitmap() : Bitmap {
    val assetManager = assets
    val inputStream = assetManager.open("a.jpg")
    return BitmapFactory.decodeStream(inputStream)
}