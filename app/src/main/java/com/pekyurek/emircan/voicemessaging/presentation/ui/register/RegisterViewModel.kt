package com.pekyurek.emircan.voicemessaging.presentation.ui.register

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pekyurek.emircan.voicemessaging.R
import com.pekyurek.emircan.voicemessaging.data.repository.UserRepository
import com.pekyurek.emircan.voicemessaging.domain.model.User
import com.pekyurek.emircan.voicemessaging.domain.usecase.SubscribeTopicUseCase
import com.pekyurek.emircan.voicemessaging.domain.usecase.UserRefUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val subscribeTopicUseCase: SubscribeTopicUseCase,
    private val userRepository: UserRepository,
    private val userRefUseCase: UserRefUseCase
) : ViewModel() {

    val errorMessage = MutableLiveData<@StringRes Int>()
    val loggedIn = MutableLiveData<Any>()

    fun register(nickname: String?) {
        if (nickname.isNullOrBlank()) {
            errorMessage.postValue(R.string.error_empty_text)
        } else {
            addDatabaseToUser(nickname)
        }
    }

    private fun addDatabaseToUser(nickname: String) = viewModelScope.launch(Dispatchers.IO) {
        userRefUseCase.getUserList(
            onSuccess = { userList ->
                if (userList.any { it.nickname == nickname }) {
                    errorMessage.postValue(R.string.error_try_different_nickname)
                } else {
                    userRepository.account?.email?.let { email ->
                        addDatabase(User(nickname = nickname, email = email))
                        loggedIn.postValue(Any())
                    } ?: errorMessage.postValue(R.string.label_error)
                }
            },
            onError = {
                //todo error handler
            }
        )
    }

    private fun addDatabase(user: User) = GlobalScope.launch(Dispatchers.IO) {
        userRefUseCase.addUser(user) {
            userRepository.user = it
            subscribeVoiceMessageTopic(it.getNotificationTopic())
        }
    }

    private fun subscribeVoiceMessageTopic(topicName: String) {
        subscribeTopicUseCase.subscribeTopic(topicName)
    }

}