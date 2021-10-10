package com.pekyurek.emircan.voicemessaging.domain.usecase

import com.google.firebase.database.*
import com.pekyurek.emircan.voicemessaging.data.repository.UserRepository
import com.pekyurek.emircan.voicemessaging.domain.model.Message
import com.pekyurek.emircan.voicemessaging.extensions.getRelationKey
import java.lang.Exception

class MessageRefUseCase(
    firebaseDatabase: FirebaseDatabase,
    private val userRepository: UserRepository,
    private val userRefUseCase: UserRefUseCase
) : UseCase {

    private val REF_KEY_MESSAGE_LIST = "messageList"

    private lateinit var relationKey: String
    private lateinit var awayUserId: String

    private val messageRef by lazy { firebaseDatabase.getReference(REF_KEY_MESSAGE_LIST).child(relationKey) }

    private var chattedBefore = false
    val messageIds = mutableSetOf<String>()

    fun initForAwayUser(awayUserId: String?) {
        val userId = userRepository.user?.id
        if (awayUserId == null || userId == null) {
            throw Exception("mail and awayEmail must not be null")
        }
        this.awayUserId = awayUserId

        relationKey = getRelationKey(awayUserId, userId)
    }

    fun getMessageList(
        onSuccess: (list: List<Message>) -> Unit,
        onError: (error: DatabaseError) -> Unit,
    ) {
        messageRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val messageList = dataSnapshot.children.mapNotNull { it.getValue(Message::class.java) }
                if (messageList.isNotEmpty()) { chattedBefore = true }
                messageIds.addAll(messageList.mapNotNull { it.id })
                onSuccess.invoke(messageList)
            }

            override fun onCancelled(error: DatabaseError) {
                onError.invoke(error)
            }
        })
    }

    fun listenNewData(onNewMessage: (message: Message) -> Unit) {
        messageRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue(Message::class.java)?.let {
                    val id = it.id ?: return@let
                    if (messageIds.add(id)) {
                        onNewMessage.invoke(it)
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun addMessage(message: Message) {
        messageRef.push().key?.let { key ->
            val map: MutableMap<String, Any> = HashMap()
            message.id = key
            map[key] = message
            messageRef.updateChildren(map)
        }

        userRefUseCase.updateUserForNewChat(userRepository.user?.id.toString(), awayUserId) {
            chattedBefore = true
        }
    }
}