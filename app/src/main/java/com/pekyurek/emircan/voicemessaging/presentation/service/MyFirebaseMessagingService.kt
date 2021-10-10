package com.pekyurek.emircan.voicemessaging.presentation.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.app.NotificationChannel
import androidx.core.app.NotificationCompat
import android.app.PendingIntent
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.pekyurek.emircan.voicemessaging.R
import com.pekyurek.emircan.voicemessaging.constants.PushNotificationConstants.KEY_AWAY_USER_ID
import com.pekyurek.emircan.voicemessaging.constants.PushNotificationConstants.KEY_AWAY_USER_NICKNAME
import com.pekyurek.emircan.voicemessaging.constants.PushNotificationConstants.KEY_EMAIL
import com.pekyurek.emircan.voicemessaging.constants.PushNotificationConstants.KEY_VOICE_URL
import com.pekyurek.emircan.voicemessaging.data.repository.UserRepository
import com.pekyurek.emircan.voicemessaging.presentation.ui.main.MainActivity
import com.pekyurek.emircan.voicemessaging.presentation.ui.message.MessageActivity
import com.pekyurek.emircan.voicemessaging.domain.model.User
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val channelId = "voice_messaging"
    private val channelName = "voice_messaging_name"

    @Inject
    lateinit var userRepository: UserRepository

    private val manager by lazy { applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        remoteMessage.data.run {
            val email = get(KEY_EMAIL) ?: return
            val voiceUrl = get(KEY_VOICE_URL) ?: return
            val awayUserId = get(KEY_AWAY_USER_ID) ?: return
            val awayUserNickname = get(KEY_AWAY_USER_NICKNAME) ?: return
            val awayUser = User(awayUserId, awayUserNickname, email)

            if (awayUserId != userRepository.user?.id) {
                showNotification(awayUser)
            }
        }
    }


    private fun showNotification(awayUser: User) {
        createNotificationChannel()
        manager.notify(0, getNotificationBuilder(awayUser).build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.getNotificationChannel(channelId)
                ?: run {
                    val channel = NotificationChannel(channelId,
                        channelName,
                        NotificationManager.IMPORTANCE_DEFAULT)
                    manager.createNotificationChannel(channel)
                }
        }
    }

    private fun getNotificationBuilder(awayUser: User) =
        NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(getString(R.string.notification_title_new_message))
            .setContentText(getString(R.string.notification_description_new_message,
                awayUser.nickname))
            .setAutoCancel(true)
            .setContentIntent(getPendingIntent(awayUser))

    private fun getPendingIntent(awayUser: User): PendingIntent {
        val intent = if (userRepository.user == null) {
            MainActivity.newIntent(this, awayUser)
        } else {
            MessageActivity.newIntent(this, awayUser)
        }
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

}