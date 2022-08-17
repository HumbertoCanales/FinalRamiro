package com.example.calculator

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class MainActivity : AppCompatActivity() {
    var msgNumber = 1
    lateinit var txtV: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        txtV = findViewById(R.id.txt)

        val intentFilter = IntentFilter(Intent.ACTION_SEND)
        val messageReceiver = Receiver()
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, intentFilter)
    }

    inner class Receiver : BroadcastReceiver() {

        override fun onReceive(p0: Context?, p1: Intent?) {
            val data = p1?.getStringExtra("message")
            val message = "Mensaje recibido N°${msgNumber++} ($data)"
            txtV.text = message
        }
    }
}