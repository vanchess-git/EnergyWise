package com.example.energywizeapp

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.provider.Settings

class NotificationService : Service() {
    private lateinit var player: MediaPlayer
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // TODO testing service
        player = MediaPlayer.create( this, Settings.System.DEFAULT_ALARM_ALERT_URI )
        player.isLooping = true
        player.start()
        return START_STICKY
        // TODO /////
    }

    override fun onDestroy() {
        super.onDestroy()
        player.stop()
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}