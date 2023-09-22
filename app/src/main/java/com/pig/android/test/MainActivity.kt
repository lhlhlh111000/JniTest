package com.pig.android.test

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var originBitmap: Bitmap
    private var resultBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        originBitmap = getBitmap()

        findViewById<SeekBar>(R.id.skb_progress).setOnSeekBarChangeListener(object : OnSeekBarChangeListener {

            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                val per = p1/100f
                showImage(per)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })
        showImage(0.5f)
    }

    private fun showImage(per: Float) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val w = originBitmap.width
                val h = originBitmap.height
                val pixels = IntArray(w*h)
                originBitmap.getPixels(pixels, 0, w, 0, 0, w, h)

                val startTime = System.currentTimeMillis()
                val grayResult = NativeSample().gray(pixels, w, h, per)
                Log.d("AAAA", (System.currentTimeMillis() - startTime).toString())

                if (resultBitmap == null) {
                    resultBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
                }
                resultBitmap?.setPixels(grayResult, 0, w, 0, 0, w, h)

                withContext(Dispatchers.Main) {
                    findViewById<ImageView>(R.id.iv_avatar).setImageBitmap(resultBitmap)
                }
            }
        }
    }
}