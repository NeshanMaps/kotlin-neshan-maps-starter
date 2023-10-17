package org.neshan.kotlinsample.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.carto.graphics.Color
import com.carto.styles.LineStyle
import com.carto.styles.LineStyleBuilder
import org.neshan.common.model.LatLng
import org.neshan.kotlinsample.R
import org.neshan.mapsdk.MapView
import org.neshan.mapsdk.model.Polyline

class DrawArcActivity : AppCompatActivity() {

    // map UI element
    private lateinit var map: MapView
    private var arc: Polyline? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draw_arc)
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


    fun drawArc(view: View?) {
        if (arc == null) {
            arc = map.drawArc(
                LatLng(35.701029742703604, 51.33399009376021),
                LatLng(35.72488915365864, 51.38092935533464),
                getLineStyle()
            )
        } else {
            map.removePolyline(arc)
            arc = null
        }
    }

    private fun getLineStyle(): LineStyle? {
        val lineStyleBuilder = LineStyleBuilder()
        lineStyleBuilder.color =
            Color(2.toShort(), 50.toShort(), 189.toShort(), 190.toShort())
        lineStyleBuilder.width = 5f
        return lineStyleBuilder.buildStyle()
    }
}