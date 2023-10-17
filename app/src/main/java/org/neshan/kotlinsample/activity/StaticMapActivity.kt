package org.neshan.kotlinsample.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import org.neshan.kotlinsample.R
import org.neshan.mapsdk.MapView

class StaticMapActivity : AppCompatActivity() {

    // map UI element
    private lateinit var map: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_static_map)
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
    }

    // We use findViewByID for every element in our layout file here
    private fun initViews() {
        map = findViewById(R.id.map)
    }

    public fun toggleStaticMap(view: View?) {
        map.setStaticMap(!map.isStaticMapEnabled)
    }
}