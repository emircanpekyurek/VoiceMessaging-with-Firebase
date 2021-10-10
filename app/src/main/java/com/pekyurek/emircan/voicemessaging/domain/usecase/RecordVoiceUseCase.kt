package com.pekyurek.emircan.voicemessaging.domain.usecase

import android.media.MediaRecorder
import com.pekyurek.emircan.voicemessaging.data.repository.UserRepository
import java.io.File

class RecordVoiceUseCase(private val userRepository: UserRepository) : UseCase {

    private var mediaRecorder: MediaRecorder? = null
    private var recordFile: File? = null

    fun startRecording(file: File?) {
        recordFile =
            File(file, System.currentTimeMillis().toString() + userRepository.account?.displayName + ".mp3")

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(recordFile!!.path)
            prepare()
            start()
        }
    }

    fun saveRecording(onRecordedFile: (file: File) -> Unit) {
        mediaRecorder?.stop()
        mediaRecorder?.release()
        val file = recordFile ?: return
        recordFile = null
        onRecordedFile.invoke(file)
    }

    fun cancelRecording() {
        mediaRecorder?.stop()
        mediaRecorder?.release()
        recordFile?.delete()
    }
}