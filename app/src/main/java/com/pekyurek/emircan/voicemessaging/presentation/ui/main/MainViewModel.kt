package com.pekyurek.emircan.voicemessaging.presentation.ui.main

import android.content.Intent
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.pekyurek.emircan.voicemessaging.R
import com.pekyurek.emircan.voicemessaging.data.repository.UserRepository
import com.pekyurek.emircan.voicemessaging.domain.model.User
import com.pekyurek.emircan.voicemessaging.domain.usecase.GoogleSignUseCase
import com.pekyurek.emircan.voicemessaging.domain.usecase.SubscribeTopicUseCase
import com.pekyurek.emircan.voicemessaging.domain.usecase.UserRefUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userRefUseCase: UserRefUseCase,
    private val subscribeTopicUseCase: SubscribeTopicUseCase,
    private val googleSignUseCase: GoogleSignUseCase
) : ViewModel() {

    val errorMessage = MutableLiveData<@StringRes Int>()
    val successLogin = MutableLiveData<Any>()
    val requiredRegister = MutableLiveData<Any>()
    val signInIntent = MutableLiveData<Intent>()

    fun signIn(account: GoogleSignInAccount?) {
        if (account == null) {
            errorMessage.postValue(R.string.label_error)
        } else {
            userRepository.account = account
            checkDatabase(account)
        }
    }

    private fun checkDatabase(account: GoogleSignInAccount) = viewModelScope.launch(Dispatchers.IO) {
        userRefUseCase.getUserList(
            onSuccess = { userList ->
                userList.find { it.email == account.email }?.let {
                    successLogin(it)
                } ?: requiredRegister.postValue(Any())
            },
            onError = {
                //todo error handler
            }
        )
    }

    private fun successLogin(user: User) {
        userRepository.user = user
        subscribeTopicUseCase.subscribeTopic(user.getNotificationTopic())
        successLogin.postValue(Any())
    }

    fun autoLogin() {
        googleSignUseCase.getLastLoginUser()?.let { signIn(it) }
    }

    fun signInIntent() {
        signInIntent.postValue(googleSignUseCase.getSignInIntent())
    }
}