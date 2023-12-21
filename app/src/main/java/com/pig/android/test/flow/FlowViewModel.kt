package com.pig.android.test.flow

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class FlowViewModel : ViewModel() {

    private val refreshIntervalMs: Long = 1000

    var latestOneNews = 0

    val latestNews: Flow<Int> = flow {
        var index = 0
        while (true) {
            emit(index)
            delay(refreshIntervalMs)
            index++
            if (index > 10) {
                break
            }
        }
    }

    val favoriteNews: Flow<Int> = latestNews
        .map { news -> news*10 }
        .onEach { news -> latestOneNews = news }


    val getUserName: Flow<String> = callbackFlow {
        callback {name ->
            try {
                trySend(name)
                close()
            } catch (e: Throwable) {
                // ignored
            }
        }

        awaitClose {
            // Do nothing
        }
    }

    private fun callback(action: (String) -> Unit) {
        action("123")
    }
}