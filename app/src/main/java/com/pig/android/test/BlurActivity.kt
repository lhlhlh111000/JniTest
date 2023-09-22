package com.pig.android.test

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class BlurActivity : AppCompatActivity() {

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blur)

        val bitmap = getBitmap()

        val w = bitmap.width
        val h = bitmap.height
        val pixels = IntArray(w*h)
        val originPixels = IntArray(w*h)
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h)
        bitmap.getPixels(originPixels, 0, w, 0, 0, w, h)
        val blurResult = NativeSample().blur(pixels, w, h)

        val resultBmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        resultBmp.setPixels(blurResult,0, w, 0, 0, w, h)
        findViewById<ImageView>(R.id.iv_blur).setImageBitmap(resultBmp)

        findViewById<ImageView>(R.id.iv_blur).setOnTouchListener(object : OnTouchListener {
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                when(p1?.action) {
                    MotionEvent.ACTION_MOVE -> {
                        val width = p0?.width ?: 0
                        val height = p0?.height ?: 0
                        val x = p1.x*w/width
                        val y = p1.y*h/height

                        val pointSee = NativeSample().pointSee(blurResult.clone(), originPixels, w, h, x.toInt(), y.toInt())
                        resultBmp.setPixels(pointSee, 0, w, 0, 0, w, h)
                        findViewById<ImageView>(R.id.iv_blur).setImageBitmap(resultBmp)
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {

                        resultBmp.setPixels(blurResult, 0, w, 0, 0, w, h)
                        findViewById<ImageView>(R.id.iv_blur).setImageBitmap(resultBmp)
                    }
                }

                return true
            }
        })
    }
}