package com.pig.android.test

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.regex.Pattern

fun Activity.getBitmap() : Bitmap {
    val inputStream = assets.open("a.jpg")
    return BitmapFactory.decodeStream(inputStream)
}

fun Activity.getPoetryList() : List<Poetry>? {
    return try {
        val inputStream = assets.open("shici.json")
        val inputStreamReader = InputStreamReader(inputStream)
        val reader = BufferedReader(inputStreamReader)
        var result = ""
        while (true) {
            val line: String = reader.readLine() ?: break
            result += line
        }

        return JsonUtils.fromJson2List(result)
    } catch (e: Exception) {
        // ignored
        null
    }
}

fun TextView.setTextWithClick(text:String, listener: (s: String) -> Unit) {
    this@setTextWithClick.movementMethod = LinkMovementMethod.getInstance()
    this@setTextWithClick.highlightColor = Color.TRANSPARENT
    val spannable = SpannableStringBuilder()
    spannable.append(text)
    for(i in text.indices) {
        val s = text.substring(i, i+1)
        val pattern = Pattern.compile("[\u4e00-\u9fa5]")
        val matcher = pattern.matcher(s)
        if (matcher.find()) {
            val span = object : ClickableSpan() {
                override fun onClick(p0: View) {
                    listener(text.substring(i, i+1))
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = Color.BLACK
                    ds.isUnderlineText = false
                }
            }
            spannable.setSpan(span, i, i+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    this@setTextWithClick.text = spannable
}