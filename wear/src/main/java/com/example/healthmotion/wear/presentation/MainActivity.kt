package com.example.healthmotion.presentation

import android.app.Activity
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.example.healthmotion.R
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable

class MainActivity : Activity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager

    private var heartRateSensor: Sensor? = null
    private var accelerometerSensor: Sensor? = null
    private var stepCounterSensor: Sensor? = null

    private var currentHeartRate = 75

    private var currentAccelX = 0f
    private var currentAccelY = 0f
    private var currentAccelZ = 0f

    private var currentSteps = 5234

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val txtStatus = findViewById<TextView>(R.id.txtStatus)

        sensorManager =
            getSystemService(SENSOR_SERVICE) as SensorManager

        heartRateSensor =
            sensorManager.getDefaultSensor(
                Sensor.TYPE_HEART_RATE
            )

        accelerometerSensor =
            sensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER
            )

        stepCounterSensor =
            sensorManager.getDefaultSensor(
                Sensor.TYPE_STEP_COUNTER
            )

        if (heartRateSensor != null) {

            currentHeartRate = 75

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

        if (accelerometerSensor != null) {

            Log.d(
                "HEALTHMOTION_WEAR",
                "Accelerometer disponible"
            )

            sensorManager.registerListener(
                this,
                accelerometerSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )

        } else {

            Log.e(
                "HEALTHMOTION_WEAR",
                "Accelerometer NO disponible"
            )
        }

        if (stepCounterSensor != null) {

            Log.d(
                "HEALTHMOTION_WEAR",
                "Step Counter disponible"
            )

        } else {

            Log.e(
                "HEALTHMOTION_WEAR",
                "Step Counter NO disponible"
            )
        }

        txtStatus.text = "Sensores listos"

        val btnSend = findViewById<Button>(R.id.btnSend)

        btnSend.setOnClickListener {

            Log.d(
                "HEALTHMOTION_WEAR",
                "Botón presionado"
            )

            Wearable.getNodeClient(this)
                .connectedNodes
                .addOnSuccessListener { nodes ->

                    Log.d(
                        "HEALTHMOTION_WEAR",
                        "Nodos encontrados: ${nodes.size}"
                    )

                    for (node in nodes) {

                        Log.d(
                            "HEALTHMOTION_WEAR",
                            "Enviando a nodo: ${node.displayName}"
                        )

                        Wearable.getDataClient(this)
                            .putDataItem(
                                PutDataMapRequest
                                    .create("/healthmotion_test")
                                    .run {

                                        dataMap.putInt(
                                            "heartRate",
                                            currentHeartRate
                                        )

                                        dataMap.putInt(
                                            "steps",
                                            currentSteps
                                        )

                                        dataMap.putFloat(
                                            "accelX",
                                            currentAccelX
                                        )

                                        dataMap.putFloat(
                                            "accelY",
                                            currentAccelY
                                        )

                                        dataMap.putFloat(
                                            "accelZ",
                                            currentAccelZ
                                        )

                                        dataMap.putLong(
                                            "timestamp",
                                            System.currentTimeMillis()
                                        )

                                        asPutDataRequest()
                                            .setUrgent()
                                    }
                            )
                            .addOnSuccessListener {

                                Log.d(
                                    "HEALTHMOTION_WEAR",
                                    "DataItem enviado"
                                )
                            }
                            .addOnFailureListener {

                                Log.e(
                                    "HEALTHMOTION_WEAR",
                                    "Error DataItem",
                                    it
                                )
                            }
                    }
                }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {

        if (event?.sensor?.type == Sensor.TYPE_HEART_RATE) {

            val bpm = event.values[0]

            Log.d(
                "HEALTHMOTION_WEAR",
                "BPM: $bpm"
            )
        }

        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {

            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            currentAccelX = x
            currentAccelY = y
            currentAccelZ = z
        }
    }

    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int
    ) {
    }
}