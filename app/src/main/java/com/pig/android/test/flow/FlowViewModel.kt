package com.pig.android.test.flow

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class FlowViewModel : ViewModel() {

    private val refreshIntervalMs: Long = 5000

    var latestOneNews = 0

    val latestNews: Flow<Int> = flow {
        var index = 0
        while (true) {
            emit(index)
            delay(refreshIntervalMs)
            index++
        }
    }

    val favoriteNews: Flow<Int> = latestNews
        .map { news -> news*10 }
//        .onEach { news -> latestOneNews = news }
}