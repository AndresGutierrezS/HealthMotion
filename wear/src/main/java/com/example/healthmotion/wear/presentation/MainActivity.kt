package com.example.healthmotion.presentation

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.example.healthmotion.R
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSend = findViewById<Button>(R.id.btnSend)

        btnSend.setOnClickListener {

            Log.d("HEALTHMOTION_WEAR", "Botón presionado")

            Wearable.getNodeClient(this)
                .connectedNodes
                .addOnSuccessListener { nodes ->

                    Log.d("HEALTHMOTION_WEAR", "Nodos encontrados: ${nodes.size}")

                    for (node in nodes) {

                        Log.d("HEALTHMOTION_WEAR", "Enviando a nodo: ${node.displayName}")

//                        Wearable.getMessageClient(this)
//                            .sendMessage(
//                                node.id,
//                                "/healthmotion",
//                                "Hola desde HealthMotion".toByteArray()
//                            )
//                            .addOnSuccessListener {
//                                Log.d("HEALTHMOTION_WEAR", "Mensaje enviado")
//                            }
//                            .addOnFailureListener {
//                                Log.e("HEALTHMOTION_WEAR", "Error enviando", it)
//                            }
                        Wearable.getDataClient(this)
                            .putDataItem(
                                PutDataMapRequest.create("/healthmotion_test").run {

                                    dataMap.putInt("heartRate", 78)
                                    dataMap.putInt("steps", 5234)
                                    dataMap.putInt("calories", 210)
                                    dataMap.putLong("timestamp", System.currentTimeMillis())

                                    asPutDataRequest().setUrgent()
                                }
                            )
                            .addOnSuccessListener {
                                Log.d("HEALTHMOTION_WEAR", "DataItem enviado")
                            }
                            .addOnFailureListener {
                                Log.e("HEALTHMOTION_WEAR", "Error DataItem", it)
                            }
                    }
                }
        }
    }
}