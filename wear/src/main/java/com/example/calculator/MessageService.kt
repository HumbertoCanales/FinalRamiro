package com.example.calculator

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class MessageService : WearableListenerService() {

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if(messageEvent.path.equals("/my_path")){
            val message = messageEvent.data
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra("message", message)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }else{
            super.onMessageReceived(messageEvent)
        }
    }
}