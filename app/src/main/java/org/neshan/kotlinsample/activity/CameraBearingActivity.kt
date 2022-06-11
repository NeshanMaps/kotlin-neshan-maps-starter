package org.neshan.kotlinsample.activity

import android.os.Bundle
import android.view.View
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import org.neshan.common.model.LatLng
import org.neshan.kotlinsample.R
import org.neshan.kotlinsample.customView.CircularSeekBar
import org.neshan.mapsdk.MapView

class CameraBearingActivity : AppCompatActivity() {

    // map UI element
    private lateinit var map: MapView

    // camera bearing control
    private lateinit var bearingSeekBar: CircularSeekBar

    // variable that hold camera bearing
    private var cameraBearing = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_bearing)
    }

    override fun onStart() {
        super.onStart()
        // everything related to ui is initialized here
        initLayoutReferences()
    }

    // Initializing layout references (views, map and map events)
    private fun initLayoutReferences() {
        initViews()

        initMap()

        // connect bearing seek bar to camera
        bearingSeekBar.setOnSeekBarChangeListener(object :
            CircularSeekBar.OnCircularSeekBarChangeListener {
            override fun onProgressChanged(
                circularSeekBar: CircularSeekBar?,
                progress: Float,
                fromUser: Boolean
            ) {
                // change camera bearing programmatically
                map.setBearing(progress, 0f)
            }

            override fun onStopTrackingTouch(seekBar: CircularSeekBar?) {}
            override fun onStartTrackingTouch(seekBar: CircularSeekBar?) {}
        })

        map.setOnCameraMoveListener {
            // updating seek bar with new camera bearing value
            if (map.bearing < 0) {
                cameraBearing = 180 + map.bearing + 180
            } else {
                cameraBearing = map.bearing
            }
            // updating own ui element must run on ui thread not in map ui thread
            runOnUiThread { bearingSeekBar.progress = cameraBearing }
        }
    }

    // Initializing map
    private fun initMap() {
        // Setting map focal position to a fixed position and setting camera zoom
        map.moveCamera(LatLng(35.767234, 51.330743), 0f)
        map.setZoom(14f, 0f)
    }

    // We use findViewByID for every element in our layout file here
    private fun initViews() {
        map = findViewById(R.id.mapview)
        bearingSeekBar = findViewById(R.id.bearing_seek_bar)

    }

    fun toggleCameraRotation(view: View) {
        val toggleButton = view as ToggleButton
        map.settings.isMapRotationEnabled = toggleButton.isChecked
    }
}