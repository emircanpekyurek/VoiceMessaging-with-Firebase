package com.pekyurek.emircan.voicemessaging.domain.model

data class Message(
    val userId: String? = null,
    val nickname: String? = null,
    val voiceUrl: String? = null,
    val time: String? = null,
    var id: String? = null,
)