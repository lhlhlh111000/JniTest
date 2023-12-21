package com.pig.android.test.flow

import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.pig.android.test.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FlowActivity : AppCompatActivity() {

    private val viewMode by viewModels<FlowViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flow)

        lifecycleScope.launch {
            viewMode.latestNews.take(5).collect {
                showLatestNews(it)
            }
            viewMode.getUserName.collect {
                showName(it)
            }
        }
    }

    private suspend fun showLatestNews(index: Int) = withContext(Dispatchers.Main) {
        findViewById<TextView>(R.id.tv_flow).text = index.toString()
    }

    private suspend fun showName(name: String) = withContext(Dispatchers.Main) {
        findViewById<TextView>(R.id.tv_flow_name).text = name
    }
}