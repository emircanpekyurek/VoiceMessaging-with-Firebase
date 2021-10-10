package com.pekyurek.emircan.voicemessaging.presentation.ui.message

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.pekyurek.emircan.voicemessaging.R
import com.pekyurek.emircan.voicemessaging.databinding.ActivityMessageBinding
import com.pekyurek.emircan.voicemessaging.domain.model.User
import com.pekyurek.emircan.voicemessaging.extensions.showToast
import com.pekyurek.emircan.voicemessaging.presentation.ui.base.BaseActivity
import com.pekyurek.emircan.voicemessaging.presentation.ui.record.ConvertVoiceEffectBottomSheet
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MessageActivity : BaseActivity<ActivityMessageBinding>() {

    private val viewModel: MessageViewModel by viewModels()

    private val messageAdapter by lazy { MessageAdapter() }

    private lateinit var awayUser: User

    override fun onInit(savedInstanceState: Bundle?) {
        getArguments()
        setToolbar()
        initViews()
        setClickListeners()
        setObservers()

        viewModel.loadData(awayUser)
    }

    private fun setToolbar() {
        supportActionBar?.title = awayUser.nickname
    }

    private fun getArguments() {
        awayUser =
            intent.getParcelableExtra(ARG_AWAY_USER) ?: throw Exception("away user not found")
    }

    private fun initViews() {
        binding.rvMessages.apply {
            adapter = messageAdapter
            itemAnimator = null
        }
        lifecycle.addObserver(messageAdapter)
    }

    private fun setClickListeners() {
        binding.btnRecord.setOnClickListener {
            if (checkRecordPermission()) {
                startRecord()
            } else {
                requestRecordPermission()
            }
        }

        binding.btnCancel.setOnClickListener {
            recordState(false)
            viewModel.cancelRecording()
        }

        binding.btnSend.setOnClickListener {
            recordState(false)
            viewModel.saveRecording()
        }
    }

    private fun startRecord() {
        recordState(true)
        viewModel.startRecording(externalCacheDir)
    }

    private fun checkRecordPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this,
            Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestRecordPermission() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            REQUEST_RECORD_PERMISSION)
    }

    private fun recordState(started: Boolean) = binding.run {
        btnRecord.isEnabled = started.not()
        btnRecord.text = getString(if (started) R.string.label_recording else R.string.label_record)
        btnCancel.isVisible = started
        btnSend.isVisible = started
    }

    private fun setObservers() {
        viewModel.userId.observe(this) {
            messageAdapter.userId = it
        }
        viewModel.messageList.observe(this) {
            messageAdapter.setData(it)
            scrollToBottom(false)
        }
        viewModel.addedNewMessage.observe(this) {
            messageAdapter.addData(it)
            if (it.userId == awayUser.id) {
                showToast(R.string.new_message_added)
            } else {
                scrollToBottom(true)
            }
        }
        viewModel.recordedVoiceFile.observe(this) { recordedFile ->
            ConvertVoiceEffectBottomSheet.newInstance(recordedFile.path, { effectedFile ->
                viewModel.uploadFile(effectedFile)
            }).show(supportFragmentManager, System.currentTimeMillis().toString())
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_RECORD_PERMISSION) {
            if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
                startRecord()
            } else {
                showToast(R.string.record_permission_warning)
            }
        }
    }

    private fun scrollToBottom(smooth: Boolean) {
        val itemCount = messageAdapter.itemCount
        when {
            itemCount < 1 -> return
            smooth -> binding.rvMessages.smoothScrollToPosition(itemCount - 1)
            else -> binding.rvMessages.scrollToPosition(itemCount - 1)
        }
    }

    override fun inflateLayout(layoutInflater: LayoutInflater) =
        ActivityMessageBinding.inflate(layoutInflater)

    companion object {
        private const val ARG_AWAY_USER = "ARG_AWAY_USER"
        private const val REQUEST_RECORD_PERMISSION = 11

        fun newIntent(context: Context, awayUser: User) =
            Intent(context, MessageActivity::class.java).apply {
                putExtra(ARG_AWAY_USER, awayUser)
            }
    }
}