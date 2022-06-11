package org.neshan.kotlinsample.activity

import android.os.Bundle
import android.view.View
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.carto.graphics.Color
import com.carto.styles.LineStyle
import com.carto.styles.LineStyleBuilder
import org.neshan.common.model.LatLng
import org.neshan.mapsdk.MapView
import org.neshan.mapsdk.model.Polyline
import org.neshan.kotlinsample.R

class DrawPolylineActivity : AppCompatActivity() {

    private lateinit var map: MapView
    private lateinit var toggleDraw: ToggleButton
    var polyline: Polyline? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draw_polyline)
    }

    override fun onStart() {
        super.onStart()
        // everything related to ui is initialized here
        initLayoutReferences()
    }

    // Initializing layout references (views, map and map events)
    fun initLayoutReferences() {
        initViews()

        toggleDraw.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                drawPolyline(toggleDraw)
            }
        })
    }

    // We use findViewByID for every element in our layout file here
    fun initViews() {
        map = findViewById(R.id.mapview)
        toggleDraw = findViewById(R.id.draw)
    }

    // Drawing line on map
    fun drawPolyline(view: View) {
        if (toggleDraw.isChecked) {
            var latLngs = ArrayList<LatLng>()
            latLngs.add(LatLng(35.769368, 51.327650))
            latLngs.add(LatLng(35.756670, 51.323889))
            latLngs.add(LatLng(35.746670, 51.383889))

            polyline = Polyline(latLngs, getLineStyle())
            map.addPolyline(polyline)
            map.moveCamera(LatLng(35.769368, 51.327650), .5f)
        } else {
            removePolyline()
        }
    }

    //Remove line if exists
    fun removePolyline() {
        if (polyline != null)
            map.removePolyline(polyline)
    }

    // In this method we create a LineStyleCreator, set its features and call buildStyle() method
    // on it and return the LineStyle object (the same routine as crating a marker style)
    fun getLineStyle(): LineStyle {
        val lineStyleBuilder = LineStyleBuilder()
        lineStyleBuilder.color = Color(2, 119, 189, 190)
        lineStyleBuilder.width = 4f
        return lineStyleBuilder.buildStyle()
    }
}