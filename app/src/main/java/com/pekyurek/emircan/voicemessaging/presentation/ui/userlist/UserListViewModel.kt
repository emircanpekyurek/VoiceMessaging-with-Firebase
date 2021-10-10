package com.pekyurek.emircan.voicemessaging.presentation.ui.userlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pekyurek.emircan.voicemessaging.data.repository.UserRepository
import com.pekyurek.emircan.voicemessaging.domain.model.User
import com.pekyurek.emircan.voicemessaging.domain.usecase.GoogleSignUseCase
import com.pekyurek.emircan.voicemessaging.domain.usecase.UserRefUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userRefUseCase: UserRefUseCase,
    private val googleSignUseCase: GoogleSignUseCase
) : ViewModel() {

    val userList = MutableLiveData<List<User>>()
    val addedNewMessage = MutableLiveData<User>()
    val updatedUser = MutableLiveData<User>()
    val getUserId = MutableLiveData<String>()

    val signOut = MutableLiveData<Any>()

    init {
        getUserId.postValue(userRepository.user?.id)
        listenNewData()
    }

    fun loadUsers() = viewModelScope.launch(Dispatchers.IO) {
        userRefUseCase.getUserList(
            onSuccess = { list ->
                userList.postValue(list.filter { it.email != userRepository.account?.email })
            },
            onError = {
                //todo error handler
            }
        )
    }

    private fun listenNewData() = GlobalScope.launch(Dispatchers.IO) {
        userRefUseCase.listenNewData(
            onNewUser = { addedNewMessage.postValue(it) },
            updatedUser = { updatedUser.postValue(it) }
        )
    }

    fun signOut() {
        googleSignUseCase.signOut { signOut.postValue(Any()) }
    }

}