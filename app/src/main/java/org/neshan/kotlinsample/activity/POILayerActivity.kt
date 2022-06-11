package org.neshan.kotlinsample.activity

import android.os.Bundle
import android.view.View
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import org.neshan.common.model.LatLng
import org.neshan.mapsdk.MapView
import org.neshan.kotlinsample.R

class POILayerActivity : AppCompatActivity() {

    // map UI element
    private lateinit var map: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poi_layer)
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
    }

    // We use findViewByID for every element in our layout file here
    private fun initViews() {
        map = findViewById(R.id.mapview)
    }

    // Initializing map
    private fun initMap() {
        // Setting map focal position to a fixed position and setting camera zoom
        map.moveCamera(LatLng(35.767234, 51.330743), 0f)
        map.setZoom(14f, 0f)
        map.isPoiEnabled = true
    }

    fun togglePOILayer(view: View) {
        var toggleButton: ToggleButton = view as ToggleButton
        map.isPoiEnabled = toggleButton.isChecked
    }

}