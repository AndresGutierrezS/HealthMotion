package com.example.healthmotion

import android.os.Bundle
import android.util.Log
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

        Wearable.getNodeClient(this)
            .connectedNodes
            .addOnSuccessListener { nodes ->

                Log.d(
                    "HEALTHMOTION_PHONE",
                    "Nodos encontrados: ${nodes.size}"
                )

                for (node in nodes) {
                    Log.d(
                        "HEALTHMOTION_PHONE",
                        "Nodo: ${node.displayName}"
                    )
                }
            }

        Wearable.getDataClient(this)
//            .dataItems
//            .addOnSuccessListener { dataItems ->
//
//                Log.d(
//                    "HEALTHMOTION_PHONE",
//                    "DataItems encontrados: ${dataItems.count}"
//                )
//            }

        Wearable.getDataClient(this)
            .addListener { dataEvents ->

                for (event in dataEvents) {

                    Log.d(
                        "HEALTHMOTION_PHONE",
                        "DataEvent recibido: ${event.dataItem.uri.path}"
                    )
                }
            }
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

        Log.d(
            "HEALTHMOTION_PHONE",
            "Mensaje recibido. Path: ${messageEvent.path}"
        )

        runOnUiThread {
            txtMessage.text = String(messageEvent.data)
        }
    }
}