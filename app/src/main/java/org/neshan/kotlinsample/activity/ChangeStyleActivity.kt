package org.neshan.kotlinsample.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import org.neshan.common.model.LatLng
import org.neshan.mapsdk.MapView
import org.neshan.mapsdk.style.NeshanMapStyle
import org.neshan.kotlinsample.R

class ChangeStyleActivity : AppCompatActivity() {

    private lateinit var map: MapView

    @NeshanMapStyle
    private var mapStyle = 0
    private lateinit var themePreview: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_style)
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
        // Initializing theme preview
        validateThemePreview()
    }

    private fun initViews() {
        map = findViewById(R.id.mapview)
        themePreview = findViewById(R.id.theme_preview)
    }

    private fun initMap() {
        mapStyle = NeshanMapStyle.STANDARD_DAY
        // Setting map focal position to a fixed position and setting camera zoom
        map.moveCamera(LatLng(35.767234, 51.330743), 0f)
        map.setZoom(14f, 0f)
        validateThemePreview()
    }

    private fun validateThemePreview() {
        when (mapStyle) {
            NeshanMapStyle.STANDARD_DAY -> themePreview.setImageDrawable(
                ResourcesCompat.getDrawable(resources, R.drawable.map_style_standard_night, theme)
            )
            NeshanMapStyle.NESHAN_NIGHT -> themePreview.setImageDrawable(
                ResourcesCompat.getDrawable(resources, R.drawable.map_style_standard_day, theme)
            )
            NeshanMapStyle.NESHAN -> themePreview.setImageDrawable(
                ResourcesCompat.getDrawable(resources, R.drawable.map_style_neshan, theme)
            )
        }
    }

    fun changeStyle(view: View) {
        when (mapStyle) {
            NeshanMapStyle.STANDARD_DAY -> mapStyle = NeshanMapStyle.NESHAN_NIGHT
            NeshanMapStyle.NESHAN_NIGHT -> mapStyle = NeshanMapStyle.NESHAN
            NeshanMapStyle.NESHAN -> mapStyle = NeshanMapStyle.STANDARD_DAY
        }
        runOnUiThread { validateThemePreview() }
        map.mapStyle = mapStyle
    }
}