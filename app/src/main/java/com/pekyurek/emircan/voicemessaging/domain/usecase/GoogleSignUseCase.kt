package com.pekyurek.emircan.voicemessaging.domain.usecase

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.pekyurek.emircan.voicemessaging.data.repository.UserRepository

class GoogleSignUseCase(
    private val context: Context,
    private val userRepository: UserRepository,
    private val subscribeTopicUseCase: SubscribeTopicUseCase
) : UseCase {

    private val mGoogleSignInClient: GoogleSignInClient

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    fun getSignInIntent() = mGoogleSignInClient.signInIntent

    fun getLastLoginUser(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }

    fun signOut(loggedOut: () -> Unit) {
        mGoogleSignInClient.signOut().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                userRepository.user?.getNotificationTopic()?.let {
                    subscribeTopicUseCase.unSubscribeTopic(it)
                }
                userRepository.account = null
                userRepository.user = null
                loggedOut.invoke()
            }
        }
    }

}