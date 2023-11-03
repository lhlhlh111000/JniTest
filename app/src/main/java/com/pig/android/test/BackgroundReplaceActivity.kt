package com.pig.android.test

import android.app.ProgressDialog
import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.pig.android.test.databinding.ActivityBackgroundReplaceBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BackgroundReplaceActivity : AppCompatActivity() {

    lateinit var binding: ActivityBackgroundReplaceBinding
    lateinit var nativeSample: NativeSample
    var isInit: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBackgroundReplaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val assetManager = assets
                nativeSample = NativeSample()
                nativeSample.init(assetManager)
                isInit = true
            }
        }

        val bmp = getBitmap()
        binding.ivBackgroundReplace.setImageBitmap(bmp)

        binding.btnBackgroundReplaceReset.setOnClickListener {
            binding.ivBackgroundReplace.setImageBitmap(bmp)
        }

        binding.btnBackgroundReplaceStart.setOnClickListener {
            val dialog = ProgressDialog(this@BackgroundReplaceActivity)
            dialog.show()
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val waitInit = async {
                        while (!isInit) {
                            delay(10)
                        }
                    }
                    waitInit.await()

                    val bmpCopy = bmp.copy(Bitmap.Config.ARGB_8888, true)
                    nativeSample.processBg(bmpCopy, true)
                    withContext(Dispatchers.Main) {
                        binding.ivBackgroundReplace.setImageBitmap(bmpCopy)
                        dialog.dismiss()
                    }
                }
            }
        }
    }
}