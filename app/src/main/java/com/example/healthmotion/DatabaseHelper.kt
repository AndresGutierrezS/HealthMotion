package com.example.healthmotion

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DatabaseHelper(
    context: Context
) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {


    companion object {

        private const val DATABASE_NAME =
            "healthmotion.db"

        private const val DATABASE_VERSION =
            1


        const val TABLE_MEASUREMENTS =
            "measurements"

        const val COLUMN_ID =
            "id"

        const val COLUMN_HEART_RATE =
            "heartRate"

        const val COLUMN_STEPS =
            "steps"

        const val COLUMN_ACCEL_X =
            "accelX"

        const val COLUMN_ACCEL_Y =
            "accelY"

        const val COLUMN_ACCEL_Z =
            "accelZ"

        const val COLUMN_TIMESTAMP =
            "timestamp"
    }


    override fun onCreate(db: SQLiteDatabase) {


        val createTable = """
            
            CREATE TABLE $TABLE_MEASUREMENTS (
            
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                
                $COLUMN_HEART_RATE INTEGER,
                
                $COLUMN_STEPS INTEGER,
                
                $COLUMN_ACCEL_X REAL,
                
                $COLUMN_ACCEL_Y REAL,
                
                $COLUMN_ACCEL_Z REAL,
                
                $COLUMN_TIMESTAMP LONG
            
            )
            
        """.trimIndent()


        db.execSQL(createTable)
    }

    fun insertMeasurement(
        heartRate: Int,
        steps: Int,
        accelX: Float,
        accelY: Float,
        accelZ: Float,
        timestamp: Long
    ) {

        val db = writableDatabase


        val values = android.content.ContentValues().apply {

            put(
                COLUMN_HEART_RATE,
                heartRate
            )

            put(
                COLUMN_STEPS,
                steps
            )

            put(
                COLUMN_ACCEL_X,
                accelX
            )

            put(
                COLUMN_ACCEL_Y,
                accelY
            )

            put(
                COLUMN_ACCEL_Z,
                accelZ
            )

            put(
                COLUMN_TIMESTAMP,
                timestamp
            )
        }


        db.insert(
            TABLE_MEASUREMENTS,
            null,
            values
        )


        db.close()
    }

    fun getLastMeasurement(): String {

        val db = readableDatabase


        val cursor = db.rawQuery(
            """
        SELECT *
        FROM $TABLE_MEASUREMENTS
        ORDER BY $COLUMN_ID DESC
        LIMIT 1
        """.trimIndent(),
            null
        )


        var result = "Sin datos"


        if (cursor.moveToFirst()) {

            val heartRate =
                cursor.getInt(
                    cursor.getColumnIndexOrThrow(
                        COLUMN_HEART_RATE
                    )
                )

            val steps =
                cursor.getInt(
                    cursor.getColumnIndexOrThrow(
                        COLUMN_STEPS
                    )
                )

            val accelX =
                cursor.getFloat(
                    cursor.getColumnIndexOrThrow(
                        COLUMN_ACCEL_X
                    )
                )

            val accelY =
                cursor.getFloat(
                    cursor.getColumnIndexOrThrow(
                        COLUMN_ACCEL_Y
                    )
                )

            val accelZ =
                cursor.getFloat(
                    cursor.getColumnIndexOrThrow(
                        COLUMN_ACCEL_Z
                    )
                )


            result =
                """
            Última medición:
            
            Heart Rate: $heartRate BPM
            
            Steps: $steps
            
            Accel:
            X: $accelX
            Y: $accelY
            Z: $accelZ
            """.trimIndent()
        }


        cursor.close()
        db.close()


        return result
    }

    fun getMeasurementsHistory(): String {

        val db = readableDatabase

        val cursor = db.rawQuery(
            """
        SELECT *
        FROM $TABLE_MEASUREMENTS
        ORDER BY $COLUMN_ID DESC
        LIMIT 20
        """.trimIndent(),
            null
        )

        val result = StringBuilder()

        if (cursor.moveToFirst()) {

            do {

                val heartRate =
                    cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                            COLUMN_HEART_RATE
                        )
                    )

                val steps =
                    cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                            COLUMN_STEPS
                        )
                    )

                val accelX =
                    cursor.getFloat(
                        cursor.getColumnIndexOrThrow(
                            COLUMN_ACCEL_X
                        )
                    )

                val accelY =
                    cursor.getFloat(
                        cursor.getColumnIndexOrThrow(
                            COLUMN_ACCEL_Y
                        )
                    )

                val accelZ =
                    cursor.getFloat(
                        cursor.getColumnIndexOrThrow(
                            COLUMN_ACCEL_Z
                        )
                    )

                val timestamp =
                    cursor.getLong(
                        cursor.getColumnIndexOrThrow(
                            COLUMN_TIMESTAMP
                        )
                    )

                val date =
                    java.text.SimpleDateFormat(
                        "dd/MM/yyyy HH:mm:ss",
                        java.util.Locale.getDefault()
                    ).format(
                        java.util.Date(timestamp)
                    )

                result.append(
                    """
                Heart Rate: $heartRate BPM
                Steps: $steps
                Accel:
                X: $accelX
                Y: $accelY
                Z: $accelZ
                Fecha: $date                
                --------------------
                
                """.trimIndent()
                )

            } while (cursor.moveToNext())

        } else {

            result.append("Sin mediciones")

        }

        cursor.close()
        db.close()

        return result.toString()
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {

        db.execSQL(
            "DROP TABLE IF EXISTS $TABLE_MEASUREMENTS"
        )

        onCreate(db)
    }
}