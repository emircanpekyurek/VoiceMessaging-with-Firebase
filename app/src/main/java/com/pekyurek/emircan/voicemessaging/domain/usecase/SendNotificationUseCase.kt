package com.pekyurek.emircan.voicemessaging.domain.usecase

import com.pekyurek.emircan.voicemessaging.domain.model.Message
import com.pekyurek.emircan.voicemessaging.constants.PushNotificationConstants.CLOUD_MESSAGE_SERVER_KEY
import com.pekyurek.emircan.voicemessaging.constants.PushNotificationConstants.HEADER_AUTHORIZATION
import com.pekyurek.emircan.voicemessaging.constants.PushNotificationConstants.HEADER_CONTENT_TYPE
import com.pekyurek.emircan.voicemessaging.constants.PushNotificationConstants.KEY_AWAY_USER_ID
import com.pekyurek.emircan.voicemessaging.constants.PushNotificationConstants.KEY_AWAY_USER_NICKNAME
import com.pekyurek.emircan.voicemessaging.constants.PushNotificationConstants.KEY_DATA
import com.pekyurek.emircan.voicemessaging.constants.PushNotificationConstants.KEY_EMAIL
import com.pekyurek.emircan.voicemessaging.constants.PushNotificationConstants.KEY_TO
import com.pekyurek.emircan.voicemessaging.constants.PushNotificationConstants.KEY_VOICE_URL
import com.pekyurek.emircan.voicemessaging.constants.PushNotificationConstants.PN_URL
import com.pekyurek.emircan.voicemessaging.constants.PushNotificationConstants.getTopic
import com.pekyurek.emircan.voicemessaging.data.repository.UserRepository
import org.json.JSONObject
import java.io.BufferedOutputStream
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.net.URL
import javax.net.ssl.HttpsURLConnection

//todo retrofit and structure
class SendNotificationUseCase(private val userRepository: UserRepository) : UseCase {

    fun pushNotification(topicName: String, message: Message) {
        val endpoint = PN_URL
        try {
            val url = URL(endpoint)
            val httpsURLConnection: HttpsURLConnection =
                (url.openConnection() as HttpsURLConnection).apply {
                    readTimeout = 10000
                    connectTimeout = 15000
                    requestMethod = "POST"
                    doInput = true
                    doOutput = true
                    setRequestProperty(HEADER_AUTHORIZATION, CLOUD_MESSAGE_SERVER_KEY)
                    setRequestProperty(HEADER_CONTENT_TYPE, "application/json")
                }

            val data = JSONObject().apply {
                put(KEY_EMAIL, userRepository.user?.email)
                put(KEY_VOICE_URL, message.voiceUrl)
                put(KEY_AWAY_USER_ID, userRepository.user?.id.toString())
                put(KEY_AWAY_USER_NICKNAME, userRepository.user?.nickname.toString())
            }

            val body = JSONObject().apply {
                put(KEY_DATA, data)
                put(KEY_TO, getTopic(topicName))
            }

            val outputStream = BufferedOutputStream(httpsURLConnection.outputStream)
            val writer = BufferedWriter(OutputStreamWriter(outputStream, "utf-8"))
            writer.write(body.toString())
            writer.flush()
            writer.close()
            outputStream.close()
            val responseCode: Int = httpsURLConnection.responseCode
            val responseMessage: String = httpsURLConnection.responseMessage
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}