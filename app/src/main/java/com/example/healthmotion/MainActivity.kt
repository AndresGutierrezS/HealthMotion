package com.example.healthmotion

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable

class MainActivity : AppCompatActivity(), MessageClient.OnMessageReceivedListener {

    private lateinit var txtMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtMessage = findViewById(R.id.txtMessage)
    }

    override fun onResume() {
        super.onResume()
        Wearable.getMessageClient(this)
            .addListener(this)
    }

    override fun onPause() {
        super.onPause()
        Wearable.getMessageClient(this)
            .removeListener(this)
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {

        runOnUiThread {
            txtMessage.text = String(messageEvent.data)
        }
    }
}