package com.sri.servicesdemo
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var musicService: MusicService? = null
    private var isBound: Boolean = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicBinder
            musicService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, MusicService::class.java)
//        startService(intent)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
       //  // Start the service explicitly
    }

    fun onStartService(view: View) {
        val intent = Intent(this, MusicService::class.java)
        startService(intent) // Start the service as a foreground service
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }

    fun onTogglePlayback(view: View) {
        println("inside toggle playback , isBound=$isBound")
        if (isBound) {
            musicService?.togglePlayback()
            updatePlaybackButton()
        }
    }

    private fun updatePlaybackButton() {
        // Update your playback button based on the playback state
        val playbackButton = findViewById<Button>(R.id.button)
        if (isBound && musicService?.isPlaying() == true) {
            playbackButton.text = "Pause"
        } else {
            playbackButton.text = "Play"
        }
    }
}