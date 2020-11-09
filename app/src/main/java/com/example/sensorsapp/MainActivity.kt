package com.example.sensorsapp

import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.abs

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var sensorAccelerometer: Sensor? = null
    private var sensorMagnetometer: Sensor? = null

    private var accelerometerData: FloatArray = FloatArray(3)
    private var magnetometerData: FloatArray = FloatArray(3)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        accelerometerData

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // initialize sensor related instance variables
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorMagnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }

    override fun onStart() {
        super.onStart()

        sensorAccelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

        sensorMagnetometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onStop() {
        super.onStop()

        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.apply {
            when (sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    accelerometerData = values.clone()
                }
                Sensor.TYPE_MAGNETIC_FIELD -> {
                    magnetometerData = values.clone()
                }
            }

            val rotationMatrix = FloatArray(9)
            val rotationOK = SensorManager.getRotationMatrix(
                rotationMatrix,
                FloatArray(3),
                accelerometerData,
                magnetometerData
            )
            val orientationValues = FloatArray(3)
            if (rotationOK)
                SensorManager.getOrientation(rotationMatrix, orientationValues)

            var azimuth = orientationValues[0]
            var pitch = orientationValues[1]
            var roll = orientationValues[2]
            if (abs(pitch) < 0.05)
                pitch = 0f
            if (abs(roll) < 0.05)
                roll = 0f
            tvAzimuthValue.text = resources.getString(R.string.value_format, azimuth)
            tvPitchValue.text = resources.getString(R.string.value_format, pitch)
            tvRollValue.text = resources.getString(R.string.value_format, roll)

            ivSpotTop.alpha = 0f
            ivSpotBottom.alpha = 0f
            ivSpotRight.alpha = 0f
            ivSpotLeft.alpha = 0f

            if (pitch > 0)
                ivSpotBottom.alpha = pitch
            else
                ivSpotTop.alpha = abs(pitch)
            if (roll > 0)
                ivSpotLeft.alpha = roll
            else
                ivSpotRight.alpha = abs(roll)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}