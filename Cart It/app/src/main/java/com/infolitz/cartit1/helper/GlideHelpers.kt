package com.infolitz.cartit1.helper

import android.content.Context
import androidx.swiperefreshlayout.widget.CircularProgressDrawable

fun Context.getGlideProgress(): CircularProgressDrawable {
    val circularProgressDrawable = CircularProgressDrawable(this)
    circularProgressDrawable.strokeWidth = 5f
    circularProgressDrawable.centerRadius = 30f
    circularProgressDrawable.start()
    return circularProgressDrawable
}

