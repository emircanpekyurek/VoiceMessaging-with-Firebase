package com.pekyurek.emircan.voicemessaging.presentation.ui.message

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.RecyclerView
import com.pekyurek.emircan.voicemessaging.R
import com.pekyurek.emircan.voicemessaging.databinding.ItemVoiceMessageBinding
import com.pekyurek.emircan.voicemessaging.domain.model.Message
import kotlinx.coroutines.*
import java.io.IOException


@SuppressLint("NotifyDataSetChanged")
class MessageAdapter : RecyclerView.Adapter<MessageAdapter.ViewHolder>(), LifecycleObserver {

    var userId: String? = null
        set(value) {
            field = value
            if (list.isNotEmpty()) notifyDataSetChanged()
        }

    private val list = mutableListOf<Message>()
    private var mediaPlayer: MediaPlayer? = null
    private var playingPosition: Int? = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemVoiceMessageBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    override fun getItemCount(): Int = list.size

    fun setData(messageList: List<Message>) {
        list.clear()
        list.addAll(messageList)
        notifyDataSetChanged()
    }

    fun addData(message: Message) {
        list.add(message)
        notifyItemChanged(list.size)
    }

    inner class ViewHolder(private val binding: ItemVoiceMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message, position: Int) {
            val isOwned = message.userId == userId
            binding.tvNickname.apply {
                text = if (isOwned) context.getString(R.string.label_you) else message.nickname
            }
            binding.cvMessageContainer.updateLayoutParams<FrameLayout.LayoutParams> {
                gravity = if (isOwned) Gravity.END else Gravity.START
            }
            binding.tvDate.text = message.time
            binding.cbPlayPause.isChecked =
                playingPosition == position && mediaPlayer?.isPlaying == true
            binding.cbPlayPause.setOnClickListener {
                val lastPlayingPosition = playingPosition
                lastPlayingPosition?.let {
                    stopPlayer(it)
                }
                if (lastPlayingPosition != position) {
                    playAudio(message.voiceUrl, position)
                }
            }
        }
    }

    private fun stopPlayer(position: Int) {
        stopMediaPlayer()
        playingPosition = null
        notifyItemChanged(position)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun stopMediaPlayer() {
        mediaPlayer?.run {
            stop()
            release()
        }
        mediaPlayer = null
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun notifyLastItem() {
        playingPosition?.let {
            playingPosition = null
            notifyItemChanged(it)
        }
    }

    private fun playAudio(url: String?, position: Int) =
        GlobalScope.launch(Dispatchers.IO) {
            try {
                playingPosition = position
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(url)
                    setOnPreparedListener { player ->
                        if (position == playingPosition) {
                            player?.start()
                        }
                    }
                    setOnCompletionListener {
                        stopPlayer(position)
                    }

                    setOnErrorListener { mp, what, extra ->
                        stopPlayer(position)
                        return@setOnErrorListener true
                    }
                    prepareAsync()
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) { stopPlayer(position) }
                e.printStackTrace()
            }
        }
}