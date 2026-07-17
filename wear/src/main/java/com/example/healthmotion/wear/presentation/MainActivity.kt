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
import android.os.Handler
import android.os.Looper
import com.google.android.gms.wearable.Node

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

    private val handler =
        Handler(Looper.getMainLooper())

    private var monitoring = false

    private val sendRunnable =
        object : Runnable {

            override fun run() {

                if (monitoring) {

                    sendSensorData()

                    handler.postDelayed(
                        this,
                        3000
                    )
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val txtStatus = findViewById<TextView>(R.id.txtStatus)

        val txtHeartRate = findViewById<TextView>(
            R.id.txtHeartRate
        )

        val txtSteps = findViewById<TextView>(
            R.id.txtSteps
        )

        val txtAccelerometer = findViewById<TextView>(
            R.id.txtAccelerometer
        )

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

             txtHeartRate.text =
                "Heart Rate: $currentHeartRate BPM"

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

//        txtStatus.text = "Sensores listos"

        txtSteps.text =
            "Steps: $currentSteps"

        checkConnection()

        val btnStart =
            findViewById<Button>(
                R.id.btnStart
            )

        val btnStop =
            findViewById<Button>(
                R.id.btnStop
            )

        btnStart.setOnClickListener {

            monitoring = true

            handler.post(sendRunnable)

            Log.d(
                "HEALTHMOTION_WEAR",
                "Monitoreo iniciado"
            )
        }


        btnStop.setOnClickListener {

            monitoring = false

            handler.removeCallbacks(
                sendRunnable
            )

            Log.d(
                "HEALTHMOTION_WEAR",
                "Monitoreo detenido"
            )
        }


    }


    private fun sendSensorData() {

        Wearable.getNodeClient(this)
            .connectedNodes
            .addOnSuccessListener { nodes ->

                for (node in nodes) {

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
                                "Datos enviados automáticamente"
                            )
                        }
                }
            }
    }

    private fun checkConnection() {

        val txtStatus =
            findViewById<TextView>(
                R.id.txtStatus
            )

        Wearable.getNodeClient(this)
            .connectedNodes
            .addOnSuccessListener { nodes ->

                if (nodes.isNotEmpty()) {

                    txtStatus.text =
                        "Conectado"

                    Log.d(
                        "HEALTHMOTION_WEAR",
                        "Teléfono conectado"
                    )

                } else {

                    txtStatus.text =
                        "Sin conexión"

                    Log.d(
                        "HEALTHMOTION_WEAR",
                        "Sin teléfonos conectados"
                    )
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

            currentHeartRate = bpm.toInt()

            val txtHeartRate =
                findViewById<TextView>(
                    R.id.txtHeartRate
                )

            txtHeartRate.text =
                "Heart Rate: $currentHeartRate BPM"
        }

        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {

            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            currentAccelX = x
            currentAccelY = y
            currentAccelZ = z

            val txtAccelerometer =
                findViewById<TextView>(
                    R.id.txtAccelerometer
                )

            txtAccelerometer.text =
                "ACC: X:${"%.1f".format(currentAccelX)} " +
                        "Y:${"%.1f".format(currentAccelY)} " +
                        "Z:${"%.1f".format(currentAccelZ)}"
        }
    }

    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int
    ) {
    }
}