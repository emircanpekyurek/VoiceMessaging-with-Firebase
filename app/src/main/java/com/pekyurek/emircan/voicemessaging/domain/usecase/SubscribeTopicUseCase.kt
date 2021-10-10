package com.pekyurek.emircan.voicemessaging.domain.usecase

import com.google.firebase.messaging.FirebaseMessaging

class SubscribeTopicUseCase(private val firebaseMessaging: FirebaseMessaging) : UseCase {

    fun subscribeTopic(topicName: String) {
        firebaseMessaging.subscribeToTopic(topicName)
    }

    fun unSubscribeTopic(topicName: String) {
        firebaseMessaging.unsubscribeFromTopic(topicName)
    }
}