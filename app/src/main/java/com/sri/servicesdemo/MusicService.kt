package com.sri.servicesdemo
// MusicService.kt

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class MusicService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying: Boolean = false
    private val channelId = "music_channel"

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer.create(this, R.raw.your_song) // Replace with your audio file
    }

    @SuppressLint("NewApi")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent?.action==null){
            createNotificationChannel()

            val notificationIntent = Intent(this, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
            )

            val playIntent = Intent(this, MusicService::class.java)
            playIntent.action = ACTION_PLAY
            val playPendingIntent = PendingIntent.getService(
                this,
                0,
                playIntent,
                PendingIntent.FLAG_IMMUTABLE
            )

            val stopIntent = Intent(this, MusicService::class.java)
            stopIntent.action = ACTION_STOP
            val stopPendingIntent = PendingIntent.getService(
                this,
                0,
                stopIntent,
                PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Music Service")
                .setContentText("Music is playing...")
                .setSmallIcon(R.drawable.baseline_music_note_24)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.baseline_music_note_24))
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.baseline_play_arrow_24, "Play", playPendingIntent)
                .addAction(R.drawable.baseline_stop_24, "Stop", stopPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(null)
                .build()

            startForeground(NOTIFICATION_ID, notification)
            togglePlayback()
        }

       println("intent got is ${intent?.action}")
        if (intent?.action == ACTION_PLAY || intent?.action== ACTION_STOP) {
            togglePlayback()
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer != null) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
       return null
    }

    private fun togglePlayback() {
        if (mediaPlayer != null) {
            isPlaying = if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                false
            } else {
                mediaPlayer?.start()
                true
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            channelId,
            "Music Channel",
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }
    companion object {
        private const val CHANNEL_ID = "music_channel"
        private const val NOTIFICATION_ID = 1
        const val ACTION_PLAY = "com.example.myapp.ACTION_PLAY"
        const val ACTION_STOP = "com.example.myapp.ACTION_STOP"
    }
}
