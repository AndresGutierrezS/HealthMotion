package com.example.healthmotion

import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class MessageService : WearableListenerService() {

    override fun onCreate() {
        super.onCreate()
        Log.d("HEALTHMOTION_SERVICE", "Service creado")
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {

        Log.d(
            "HEALTHMOTION_SERVICE",
            "PATH = ${messageEvent.path}"
        )

        Log.d(
            "HEALTHMOTION_SERVICE",
            "DATA = ${String(messageEvent.data)}"
        )
    }
}