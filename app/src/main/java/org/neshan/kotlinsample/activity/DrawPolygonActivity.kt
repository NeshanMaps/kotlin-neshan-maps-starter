package org.neshan.kotlinsample.activity

import android.os.Bundle
import android.view.View
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.carto.graphics.Color
import com.carto.styles.LineStyle
import com.carto.styles.LineStyleBuilder
import com.carto.styles.PolygonStyle
import com.carto.styles.PolygonStyleBuilder
import org.neshan.common.model.LatLng
import org.neshan.mapsdk.MapView
import org.neshan.mapsdk.model.Polygon
import org.neshan.kotlinsample.R

class DrawPolygonActivity : AppCompatActivity() {

    // map UI element
    private lateinit var map: MapView
    private lateinit var drawPolygon: ToggleButton

    private var polygon: Polygon? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draw_polygon)
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

    // Initializing map
    private fun initMap() {
        // Setting map focal position to a fixed position and setting camera zoom
        map.moveCamera(LatLng(35.767234, 51.330743), 0f)
        map.settings.isZoomControlsEnabled = true
        map.setZoom(14f, 0f)
    }

    // We use findViewByID for every element in our layout file here
    private fun initViews() {
        map = findViewById(R.id.mapview)
        drawPolygon = findViewById(R.id.drawPolygon)
    }

    // In this method we create a LineStyleCreator, set its features and call buildStyle() method
    // on it and return the LineStyle object (the same routine as crating a marker style)
    private fun getLineStyle(): LineStyle {
        val lineStyleBuilder = LineStyleBuilder()
        lineStyleBuilder.color = Color(2, 119, 189, 190)
        lineStyleBuilder.width = 4f
        return lineStyleBuilder.buildStyle()
    }

    // In this method we create a PolygonStyleCreator and set its features.
    // One feature is its lineStyle, getLineStyle() method is used to get polygon's line style
    // By calling buildStyle() method on polygonStrCr, an object of type PolygonStyle is returned
    private fun getPolygonStyle(): PolygonStyle {
        var polygonStyleBuilder = PolygonStyleBuilder()
        polygonStyleBuilder.lineStyle = getLineStyle()
        polygonStyleBuilder.color = Color(2, 130, 189, 190)
        return polygonStyleBuilder.buildStyle()
    }

    // Drawing polygon on map
    fun drawPolygon(view: View) {
        var toggleButton = view as ToggleButton
        if (toggleButton.isChecked) {
            var latLngs = ArrayList<LatLng>()
            latLngs.add(LatLng(35.762294, 51.325525))
            latLngs.add(LatLng(35.756548, 51.323768))
            latLngs.add(LatLng(35.755394, 51.328617))
            latLngs.add(LatLng(35.760905, 51.330666))

            polygon = Polygon(latLngs, getPolygonStyle())

            map.addPolygon(polygon)

            map.moveCamera(LatLng(35.762294, 51.325525), .5f)
        } else {
            if (polygon != null) {
                map.removePolygon(polygon)
            }
        }
    }
}