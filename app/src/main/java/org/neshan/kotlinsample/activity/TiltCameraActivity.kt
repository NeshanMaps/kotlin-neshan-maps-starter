package org.neshan.kotlinsample.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import org.neshan.common.model.LatLng
import org.neshan.kotlinsample.R
import org.neshan.mapsdk.MapView

class TiltCameraActivity : AppCompatActivity() {

    private lateinit var map: MapView
    private lateinit var tiltSeekBar: SeekBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tilt_camera)
    }

    override fun onStart() {
        super.onStart()
        // everything related to ui is initialized here
        initLayoutReferences()
    }

    // Initializing layout references (views, map and map events)
    private fun initLayoutReferences() {
        // Initializing views
        initViews()
        // Initializing mapView element
        initMap()

        //tilt camera
        tiltSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                map.setTilt(tiltSeekBar.progress + 30f, 0f)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                Log.d("asd", tiltSeekBar.progress.toString());
            }
        })

        map.setOnCameraMoveListener {
            runOnUiThread {
                tiltSeekBar.progress = Math.round(map.tilt) - 30
            }
        }
    }

    // We use findViewByID for every element in our layout file here
    private fun initViews() {
        map = findViewById(R.id.mapview)
        tiltSeekBar = findViewById(R.id.tilt_seek_bar)
    }

    // Initializing map
    private fun initMap() {
        // Setting map focal position to a fixed position and setting camera zoom
        map.moveCamera(LatLng(35.767234, 51.330743), 0f)
        map.setZoom(14f, 0f)
    }

    fun toggleCameraTilt(view: View) {
        val toggleButton = view as ToggleButton
        if (toggleButton.isChecked) {
            //set tilt range from 30 to 90 degrees
            map.settings.minTiltAngle = 30f
            map.settings.maxTiltAngle = 90f
        } else {
            //set tilt range to 1 degree (only current tilt degree)
            map.settings.minTiltAngle = map.tilt
            map.settings.maxTiltAngle = map.tilt
        }
    }
}