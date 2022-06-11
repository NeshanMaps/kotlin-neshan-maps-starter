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
import org.neshan.mapsdk.model.Circle
import org.neshan.kotlinsample.R

class DrawCircleActivity : AppCompatActivity() {

    private lateinit var map: MapView
    var circle: Circle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draw_circle)
    }

    override fun onStart() {
        super.onStart()

        initLayoutReferences()
    }

    // Initializing layout references (views, map and map events)
    private fun initLayoutReferences() {
        initViews()
    }

    // We use findViewByID for every element in our layout file here
    private fun initViews() {
        map = findViewById(R.id.mapview)
    }

    fun drawCircle(view: View) {
        var toggleButton = view as ToggleButton
        if (toggleButton.isChecked) {
            circle =
                Circle(LatLng(35.708743, 51.338570), 500.0, Color(2, 119, 189, 190), getLineStyle())
            map.addCircle(circle)
            map.moveCamera(LatLng(35.708743, 51.338570), .5f)
        } else {
            removeCircle()
        }
    }

    //Remove circle if exists
    private fun removeCircle() {
        if (circle != null) {
            map.removeCircle(circle)
        }
    }

    private fun getLineStyle(): LineStyle {
        val lineStyleBuilder = LineStyleBuilder()
        lineStyleBuilder.color = Color(2, 119, 189, 190)
        lineStyleBuilder.width = 4f
        return lineStyleBuilder.buildStyle()
    }
}