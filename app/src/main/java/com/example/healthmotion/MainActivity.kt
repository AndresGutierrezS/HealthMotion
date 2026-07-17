package com.example.healthmotion

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataMapItem

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
            .addListener { dataEvents ->

                for (event in dataEvents) {

                    if (event.type == DataEvent.TYPE_CHANGED) {

                        if (event.dataItem.uri.path == "/healthmotion_test") {

                            val dataMap =
                                DataMapItem
                                    .fromDataItem(event.dataItem)
                                    .dataMap

                            val heartRate =
                                dataMap.getInt("heartRate")

                            val steps =
                                dataMap.getInt("steps")

                            val accelX =
                                dataMap.getFloat("accelX")

                            val accelY =
                                dataMap.getFloat("accelY")

                            val accelZ =
                                dataMap.getFloat("accelZ")


                            Log.d(
                                "HEALTHMOTION_PHONE",
                                """
                                Heart Rate: $heartRate
                                Steps: $steps
                                Accel X: $accelX
                                Accel Y: $accelY
                                Accel Z: $accelZ
                                """.trimIndent()
                            )


                            runOnUiThread {

                                txtMessage.text =
                                    """
                                    Heart Rate: $heartRate BPM
                                    
                                    Steps: $steps
                                    
                                    Accelerometer:
                                    X: ${"%.2f".format(accelX)}
                                    Y: ${"%.2f".format(accelY)}
                                    Z: ${"%.2f".format(accelZ)}
                                    """.trimIndent()
                            }
                        }
                    }
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