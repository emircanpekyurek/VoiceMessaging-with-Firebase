package com.pekyurek.emircan.voicemessaging.domain.model

import android.os.Parcelable
import com.pekyurek.emircan.voicemessaging.extensions.toMd5
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var id: String = "",
    val nickname: String = "",
    val email: String = "",
    val chatUserIds: MutableList<String> = mutableListOf()
) : Parcelable {

    fun getNotificationTopic() = id.toMd5()


}