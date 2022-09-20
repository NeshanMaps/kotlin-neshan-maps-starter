package org.neshan.kotlinsample.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.carto.graphics.Color
import com.carto.styles.TextMargins
import com.carto.styles.TextStyleBuilder
import org.neshan.common.model.LatLng
import org.neshan.mapsdk.MapView
import org.neshan.mapsdk.model.Label
import org.neshan.kotlinsample.R

class AddLabelActivity : AppCompatActivity() {

    private lateinit var map: MapView
    private var label: Label? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_label)
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

        // when long clicked on map, a marker is added in clicked location
        map.setOnMapLongClickListener { latLng: LatLng -> addLabel(latLng) }
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
    }

    // This method gets a LatLng as input and adds a label on that position
    private fun addLabel(loc: LatLng) {
        if (label != null) {
            map.removeLabel(label)
        }
        // Creating text style. We should use an object of type TextStyleBuilder, set all features on it
        // and then call buildStyle method on it. This method returns an object of type TextStyle.
        val textStyleBuilder = TextStyleBuilder()
        textStyleBuilder.fontSize = 25f
        textStyleBuilder.color = Color(-0x1)
        textStyleBuilder.strokeWidth = 0.5f
        textStyleBuilder.strokeColor = Color(-0x1)
        textStyleBuilder.textMargins = TextMargins(5, 2, 5, 2)
        textStyleBuilder.backgroundColor = Color(-0x10000)
        val textStyle = textStyleBuilder.buildStyle()

        // Creating label
        label = Label(loc, textStyle, "مکان انتخاب شده")

        // Adding marker to labelLayer, or showing label on map!
        map.addLabel(label)
    }
}