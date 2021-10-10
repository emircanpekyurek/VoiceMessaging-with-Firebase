package com.pekyurek.emircan.voicemessaging.domain.usecase

import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storageMetadata
import java.io.File
import java.io.FileInputStream

class UploadFileUseCase(private val storageReference: StorageReference) : UseCase {

    fun uploadFile(file: File, onSuccess: (downloadUrl: String) -> Unit, onError: (ex: Exception?) -> Unit) {
        val voiceRef = storageReference.child(file.name)
        val stream = FileInputStream(file)
        val uploadTask = voiceRef.putStream(stream, storageMetadata { contentType = "audio/mpeg" })
        uploadTask
            .continueWithTask { voiceRef.downloadUrl }
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess.invoke(task.result.toString())
                } else {
                    onError.invoke(task.exception)
                }
            }
    }

}