package com.pekyurek.emircan.voicemessaging.presentation.ui.record

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pekyurek.emircan.voicemessaging.databinding.BottomSheetConvertVoiceEffectBinding
import com.pekyurek.emircan.voicemessaging.extensions.showToast
import java.io.File

class ConvertVoiceEffectBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetConvertVoiceEffectBinding
    private lateinit var originalFile: File

    private val viewModel by viewModels<ConvertVoiceEffectViewModel>()

    private var onSuccess: ((file: File) -> Unit)? = null
    private var onError: (() -> Unit)? = null

    init {
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetConvertVoiceEffectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getArgs()
        setObservers()
        setClickListeners()
    }

    private fun getArgs() {
        val originalFilePath = arguments?.getString(ARG_FILE_PATH) ?: throw Exception("need file path")
        originalFile = File(originalFilePath)
    }

    private fun setObservers() {
        viewModel.error.observe(this) {
            context?.showToast(it)
            onError?.invoke()
        }
        viewModel.effectedFile.observe(this) {
            onSuccess?.invoke(it)
        }
    }

    private fun setClickListeners() {
        binding.tvNoEffect.setOnClickListener {
            effect(VoiceEffect.NO_EFFECT)
        }
        binding.tvCaveEffect.setOnClickListener {
            effect(VoiceEffect.CAVE)
        }
        binding.tvRadioEffect.setOnClickListener {
            effect(VoiceEffect.RADIO)
        }
        binding.tvRobotEffect.setOnClickListener {
            effect(VoiceEffect.ROBOT)
        }
        binding.tvSquirrelEffect.setOnClickListener {
            effect(VoiceEffect.SQUIRREL)
        }

        binding.tvCancel.setOnClickListener {
            originalFile.delete()
            dismiss()
        }
    }

    private fun effect(voiceEffect: VoiceEffect) {
        viewModel.effect(originalFile, voiceEffect)
        dismiss()
    }

    companion object {
        private const val ARG_FILE_PATH = "ARG_FILE_PATH"

        fun newInstance(
            filePath: String,
            onSuccess: ((file: File) -> Unit)? = null,
            onError: (() -> Unit)? = null,
        ) = ConvertVoiceEffectBottomSheet().apply {
            arguments = Bundle().apply {
                putString(ARG_FILE_PATH, filePath)
            }
            this.onSuccess = onSuccess
            this.onError = onError
        }
    }
}