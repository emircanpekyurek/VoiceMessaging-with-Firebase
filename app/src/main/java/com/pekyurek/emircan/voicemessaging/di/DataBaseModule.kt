package com.pekyurek.emircan.voicemessaging.di

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataBaseModule {

    private const val PATH_USER_REF = "userList"

    @Singleton
    @Provides
    fun provideFirebaseDatabase() = Firebase.database

    @Singleton
    @Provides
    fun provideFirebaseDatabaseUserRef() = provideFirebaseDatabase().getReference(PATH_USER_REF)

    @Singleton
    @Provides
    fun provideFirebaseStorageRef() = Firebase.storage.reference

    @Singleton
    @Provides
    fun provideFirebaseMessaging() = Firebase.messaging


}