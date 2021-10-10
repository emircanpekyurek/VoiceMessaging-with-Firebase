package com.pekyurek.emircan.voicemessaging.presentation.ui.record

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arthenica.mobileffmpeg.Config.*
import com.arthenica.mobileffmpeg.FFmpeg
import java.io.File

class ConvertVoiceEffectViewModel : ViewModel() {

    val effectedFile = MutableLiveData<File>()
    val error = MutableLiveData<String>()

    fun effect(file: File, effect: VoiceEffect) {
        if (effect == VoiceEffect.NO_EFFECT) {
            effectedFile.postValue(file)
        } else {
            applyEffect(file, effect)
        }
    }

    private fun cmdList(originalFile: File, effectFile: File, voiceEffect: VoiceEffect): Array<String> {
        return arrayOf(
            "-y",
            "-i",
            originalFile.path,
            "-af",
            voiceEffect.cmd.toString(),
            effectFile.path
        )
    }

    private fun applyEffect(originalFile: File, effect: VoiceEffect) {
        val originalFileName = originalFile.name
        val effectFile =
            File(originalFile.path.replace(originalFileName, "effected_$originalFileName"))
        val cmd = cmdList(originalFile, effectFile, effect)
        FFmpeg.execute(cmd)

        when (val returnCode = getLastReturnCode()) {
            RETURN_CODE_SUCCESS -> {
                effectedFile.postValue(effectFile)
            }
            else -> {
                error.postValue("Error code $returnCode")
            }
        }
    }

}