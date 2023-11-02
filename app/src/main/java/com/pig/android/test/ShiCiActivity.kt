package com.pig.android.test

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShiCiActivity : AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shici)

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                var poetryList = getPoetryList()
                withContext(Dispatchers.Main) {
                    showPoetry(poetryList?.get(0))
                }
            }
        }
    }

    private fun showPoetry(poetry: Poetry?) {
        poetry ?: return

        findViewById<TextView>(R.id.tvText).setTextWithClick(poetry.text) {
            onWordClick(it)
        }
        findViewById<TextView>(R.id.tvTitle).setTextWithClick(poetry.title) {
            onWordClick(it)
        }
        findViewById<TextView>(R.id.tvAuthor).setTextWithClick(poetry.author) {
            onWordClick(it)
        }
    }

    private fun onWordClick(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }
}