package com.pig.android.test

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

open class JsonUtils {

    companion object {

        inline fun <reified T> fromJson2List(json: String) = fromJson<List<T>>(json)

        inline fun <reified T> fromJson(json: String): T? {
            return try {
                val type = object : TypeToken<T>() {}.type
                return Gson().fromJson(json, type)
            } catch (e: Exception) {
                null
            }
        }
    }
}