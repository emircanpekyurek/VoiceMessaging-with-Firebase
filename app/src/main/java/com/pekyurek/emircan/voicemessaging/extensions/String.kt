package com.pekyurek.emircan.voicemessaging.extensions

import java.math.BigInteger
import java.security.MessageDigest


fun getRelationKey(userId1: String, userId2: String): String {
    return listOf(userId1, userId2)
        .sortedBy { it }
        .joinToString("")
        .toMd5()
}

fun String.toMd5(): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(this.toByteArray())).toString(16).padStart(32, '0').trim()
}

fun String.toSearchFilter() = this.replace("İ", "i").lowercase()