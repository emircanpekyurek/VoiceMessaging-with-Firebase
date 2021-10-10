package com.pekyurek.emircan.voicemessaging.extensions

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
fun nowDateToFormat(): String {
    return SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Date())
}