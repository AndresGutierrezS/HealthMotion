package com.example.healthmotion.presentation

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.example.healthmotion.R
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import android.hardware.Sensor
import android.hardware.SensorManager
import android.hardware.SensorEvent
import android.hardware.SensorEventListener

class MainActivity : Activity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager

    private var heartRateSensor: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val txtStatus = findViewById<android.widget.TextView>(
            R.id.txtStatus
        )

        sensorManager =
            getSystemService(SENSOR_SERVICE) as SensorManager

        heartRateSensor =
            sensorManager.getDefaultSensor(
                Sensor.TYPE_HEART_RATE
            )

        if (heartRateSensor != null) {

//            txtStatus.text = "Heart Rate disponible"

            val simulatedBpm = 75

            txtStatus.text =
                "BPM: $simulatedBpm"

            Log.d(
                "HEALTHMOTION_WEAR",
                "Sensor Heart Rate disponible"
            )

            sensorManager.registerListener(
                this,
                heartRateSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )

        } else {

            txtStatus.text = "Heart Rate NO disponible"

            Log.e(
                "HEALTHMOTION_WEAR",
                "Sensor Heart Rate NO disponible"
            )
        }

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

                                    dataMap.putInt("heartRate", 75)
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

    override fun onSensorChanged(event: SensorEvent?) {

        Log.d(
            "HEALTHMOTION_WEAR",
            "onSensorChanged ejecutado"
        )

        if (event?.sensor?.type == Sensor.TYPE_HEART_RATE) {

            val bpm = event.values[0]

            Log.d(
                "HEALTHMOTION_WEAR",
                "BPM: $bpm"
            )

            val txtStatus =
                findViewById<android.widget.TextView>(
                    R.id.txtStatus
                )

            txtStatus.text = "BPM: $bpm"
        }
    }

    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int
    ) {
    }
}