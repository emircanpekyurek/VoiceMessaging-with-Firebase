package com.pekyurek.emircan.voicemessaging.di

import android.content.Context
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.StorageReference
import com.pekyurek.emircan.voicemessaging.data.repository.UserRepository
import com.pekyurek.emircan.voicemessaging.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    fun provideMessageRefUseCase(database: FirebaseDatabase, userRepository: UserRepository, userRefUseCase: UserRefUseCase) =
        MessageRefUseCase(database, userRepository, userRefUseCase)

    @Provides
    fun provideUserRefUseCase(databaseReference: DatabaseReference) =
        UserRefUseCase(databaseReference)

    @Provides
    fun provideUploadFileUseCase(storageReference: StorageReference) =
        UploadFileUseCase(storageReference)

    @Provides
    fun provideSubscribeTopicUseCase(firebaseMessaging: FirebaseMessaging) =
        SubscribeTopicUseCase(firebaseMessaging)

    @Provides
    fun provideSendNotificationUseCase(userRepository: UserRepository) =
        SendNotificationUseCase(userRepository)

    @Provides
    fun provideRecordVoiceUseCase(userRepository: UserRepository) =
        RecordVoiceUseCase(userRepository)

    @Provides
    fun provideGoogleSignUseCase(
        @ApplicationContext context: Context,
        userRepository: UserRepository,
        subscribeTopicUseCase: SubscribeTopicUseCase,
    ) = GoogleSignUseCase(context, userRepository, subscribeTopicUseCase)

}