package com.github.diarmaidlindsay.myanimequiz.utils.extensions

import android.content.Context
import android.widget.Toast

fun Context.showToast(message: String?) {
    Toast.makeText(this.applicationContext, message, Toast.LENGTH_SHORT).show()
}