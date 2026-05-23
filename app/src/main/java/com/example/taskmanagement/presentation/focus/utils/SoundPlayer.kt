package com.example.taskmanagement.presentation.focus.utils

import android.content.Context
import android.media.MediaPlayer
import android.provider.Settings

object SoundPlayer {
    fun play(context: Context) {
        val uri = Settings.System.DEFAULT_NOTIFICATION_URI
        val mediaPlayer = MediaPlayer.create(context, uri)
        mediaPlayer?.setOnCompletionListener { player ->
            player.release()
        }
        mediaPlayer?.start()
    }
}
