package com.pekyurek.emircan.voicemessaging.presentation.ui.message

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DatabaseError
import com.pekyurek.emircan.voicemessaging.data.repository.UserRepository
import com.pekyurek.emircan.voicemessaging.domain.model.Message
import com.pekyurek.emircan.voicemessaging.domain.model.User
import com.pekyurek.emircan.voicemessaging.domain.usecase.MessageRefUseCase
import com.pekyurek.emircan.voicemessaging.domain.usecase.RecordVoiceUseCase
import com.pekyurek.emircan.voicemessaging.domain.usecase.SendNotificationUseCase
import com.pekyurek.emircan.voicemessaging.domain.usecase.UploadFileUseCase
import com.pekyurek.emircan.voicemessaging.extensions.nowDateToFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.*
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val uploadFileUseCase: UploadFileUseCase,
    private val messageRefUseCase: MessageRefUseCase,
    private val sendNotificationUseCase: SendNotificationUseCase,
    private val recordVoiceUseCase: RecordVoiceUseCase,
) : ViewModel() {

    private lateinit var awayUser: User

    val messageList = MutableLiveData<List<Message>>()
    val databaseError = MutableLiveData<DatabaseError>()
    val addedNewMessage = MutableLiveData<Message>()
    val recordedVoiceFile = MutableLiveData<File>()

    val userId = MutableLiveData<String?>()

    fun loadData(awayUser: User) {
        this.awayUser = awayUser
        messageRefUseCase.initForAwayUser(awayUser.id)
        userId.postValue(userRepository.user?.id)
        getMessageList()
    }

    private fun getMessageList() = viewModelScope.launch(Dispatchers.IO) {
        messageRefUseCase.getMessageList(
            onSuccess = {
                messageList.postValue(it)
                listenNewData()
            },
            onError = {
                databaseError.postValue(it)
                listenNewData()
            }
        )
    }

    private fun listenNewData() = viewModelScope.launch(Dispatchers.IO) {
        messageRefUseCase.listenNewData { addedNewMessage.postValue(it) }
    }

    fun startRecording(file: File?) {
        recordVoiceUseCase.startRecording(file)
    }

    fun cancelRecording() {
        recordVoiceUseCase.cancelRecording()
    }

    fun saveRecording() {
        recordVoiceUseCase.saveRecording { recordedVoiceFile.postValue(it) }
    }

    fun uploadFile(uploadFile: File) =
        GlobalScope.launch(Dispatchers.IO) {
            uploadFileUseCase.uploadFile(
                file = uploadFile,
                onSuccess = { downloadUrl ->
                    uploadFile.delete()
                    val message =
                        Message(userRepository.user?.id,
                            userRepository.user?.nickname,
                            downloadUrl,
                            nowDateToFormat())
                    addDatabase(message)
                    pushNotification(message)
                },
                onError = {
                    uploadFile.delete()
                    //todo error handler
                }
            )
        }

    private fun addDatabase(message: Message) = GlobalScope.launch(Dispatchers.IO) {
        messageRefUseCase.addMessage(message)
    }

    private fun pushNotification(message: Message) {
        GlobalScope.launch(Dispatchers.IO) {
            sendNotificationUseCase.pushNotification(awayUser.getNotificationTopic(), message)
        }
    }
}