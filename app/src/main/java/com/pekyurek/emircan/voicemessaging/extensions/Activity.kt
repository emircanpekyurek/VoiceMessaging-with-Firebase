package com.pekyurek.emircan.voicemessaging.extensions

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

fun Activity.showToast(@StringRes textRes: Int) {
    Toast.makeText(this, textRes, Toast.LENGTH_LONG).show()
}

fun Context.showToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}