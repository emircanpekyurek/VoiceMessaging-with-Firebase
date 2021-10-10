package com.pekyurek.emircan.voicemessaging.domain.usecase

import com.google.firebase.database.*
import com.pekyurek.emircan.voicemessaging.domain.model.User

class UserRefUseCase(private val userRef: DatabaseReference) : UseCase {

    fun getUserList(
        onSuccess: (list: List<User>) -> Unit,
        onError: ((error: DatabaseError) -> Unit)? = null
    ) {
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                onSuccess.invoke(snapshot.children.mapNotNull { it.getValue(User::class.java) })
            }

            override fun onCancelled(error: DatabaseError) {
                onError?.invoke(error)
            }
        })
    }

    fun listenNewData(onNewUser: (user: User) -> Unit, updatedUser: (user: User) -> Unit) {
        userRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue(User::class.java)?.let { onNewUser.invoke(it) }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue(User::class.java)?.let { updatedUser.invoke(it) }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun addUser(user: User, onAddedUser: (user: User) -> Unit) {
        userRef.push().key?.let { key ->
            user.id = key
            val map: MutableMap<String, Any> = HashMap()
            map[key] = user
            userRef.updateChildren(map)
            onAddedUser.invoke(user)
        }
    }

    private fun updateUser(user: User) {
        userRef.child(user.id).setValue(user)
    }

    fun updateUserForNewChat(userId: String, awayUserId: String, onSuccess: () -> Unit) {
        getUserList({ list ->
            list.forEach { user ->
                val chatUserId = when (user.id) {
                    userId -> awayUserId
                    awayUserId -> userId
                    else -> return@forEach
                }
                if (!user.chatUserIds.contains(chatUserId)) {
                    user.chatUserIds.add(chatUserId)
                    updateUser(user)
                }
            }
            onSuccess.invoke()
        })
    }
}