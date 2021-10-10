package com.pekyurek.emircan.voicemessaging.constants

object PushNotificationConstants {

    const val PN_URL = "https://fcm.googleapis.com/fcm/send"

    private const val SERVER_KEY = ""

    const val CLOUD_MESSAGE_SERVER_KEY = "key=$SERVER_KEY"

    const val HEADER_AUTHORIZATION = "Authorization"
    const val HEADER_CONTENT_TYPE = "Content-Type"

    const val KEY_EMAIL = "email"
    const val KEY_VOICE_URL = "voiceUrl"
    const val KEY_AWAY_USER_ID = "awayUserId"
    const val KEY_AWAY_USER_NICKNAME = "awayUserNickname"
    const val KEY_DATA = "data"
    const val KEY_TO = "to"

    fun getTopic(topicName: String) = "/topics/$topicName"
}