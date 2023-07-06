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
    private var isBound: Boolean = false
    private var myBinder: MusicService.MusicBinder?=null
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            println("onServiceConnected called")
            myBinder = service as MusicService.MusicBinder
            isBound = true
            myBinder?.startMusic()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.button).setOnClickListener {
            //started service
            val intent = Intent(this, MusicService::class.java)
           // startService(intent)

            //bound service
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onDestroy() {
        unbindService(connection)
        super.onDestroy()
    }
}