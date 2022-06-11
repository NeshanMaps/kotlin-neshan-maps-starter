package org.neshan.kotlinsample.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.neshan.common.model.LatLng
import org.neshan.mapsdk.MapView
import org.neshan.kotlinsample.R

class CacheActivity : AppCompatActivity() {

    // map UI element
    private lateinit var map:MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cache)
    }

    override fun onStart() {
        super.onStart()

        initLayoutReferences()
    }

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
        // Cache size is 10 MB
        map.cacheSize = 10
        // Set cache location
        map.cachePath = cacheDir

        map.isPoiEnabled = true
        // Setting map focal position to a fixed position and setting camera zoom
        map.moveCamera(LatLng(35.767234, 51.330743), 0f)
        map.setZoom(14f, 0f)
    }
}