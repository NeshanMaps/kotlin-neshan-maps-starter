package org.neshan.kotlinsample.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.carto.styles.AnimationStyle
import com.carto.styles.AnimationStyleBuilder
import com.carto.styles.AnimationType
import com.carto.styles.MarkerStyleBuilder
import org.neshan.common.model.LatLng
import org.neshan.kotlinsample.R
import org.neshan.mapsdk.MapView
import org.neshan.mapsdk.internal.utils.BitmapUtils
import org.neshan.mapsdk.model.Marker


class AddMarkerActivity : AppCompatActivity() {

    // map UI element
    private lateinit var map: MapView

    // marker animation style
    private lateinit var animSt: AnimationStyle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_marker)
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

        // when long clicked on map, a marker is added in clicked location
        map.setOnMapLongClickListener {
            val marker: Marker = createMarker(it)
            marker.title = "Title"
            map.addMarker(marker)
            marker.showInfoWindow()
        }
        // when on marker clicked, change marker style to blue
        map.setOnMarkerClickListener { marker1: Marker ->
            changeMarkerToBlue(marker1)
        }
    }

    // We use findViewByID for every element in our layout file here
    private fun initViews() {
        map = findViewById(R.id.mapview)
    }

    // This method gets a LatLng as input and adds a marker on that position
    private fun createMarker(loc: LatLng): Marker {
        // Creating animation for marker. We should use an object of type AnimationStyleBuilder, set
        // all animation features on it and then call buildStyle() method that returns an object of type
        // AnimationStyle
        val animStBl = AnimationStyleBuilder()
        animStBl.fadeAnimationType = AnimationType.ANIMATION_TYPE_SMOOTHSTEP
        animStBl.sizeAnimationType = AnimationType.ANIMATION_TYPE_SPRING
        animStBl.phaseInDuration = 0.5f
        animStBl.phaseOutDuration = 0.5f
        animSt = animStBl.buildStyle()

        // Creating marker style. We should use an object of type MarkerStyleCreator, set all features on it
        // and then call buildStyle method on it. This method returns an object of type MarkerStyle
        val markStCr = MarkerStyleBuilder()
        markStCr.size = 30f
        markStCr.bitmap = BitmapUtils.createBitmapFromAndroidBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.ic_marker
            )
        )
        // AnimationStyle object - that was created before - is used here
        markStCr.animationStyle = animSt
        val markSt = markStCr.buildStyle()

        // Creating marker
        return Marker(loc, markSt)
    }

    private fun changeMarkerToBlue(redMarker: Marker) {
        // create new marker style
        val markStCr = MarkerStyleBuilder()
        markStCr.size = 30f
        // Setting a new bitmap as marker
        markStCr.bitmap = BitmapUtils.createBitmapFromAndroidBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.ic_marker_blue
            )
        )
        markStCr.animationStyle = animSt
        val blueMarkSt = markStCr.buildStyle()

        // changing marker style using setStyle
        redMarker.setStyle(blueMarkSt)
    }

    fun clearMarkers(view: View) {
        map.clearMarkers()
    }
}