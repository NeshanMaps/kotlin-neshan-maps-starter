package org.neshan.kotlinsample.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.carto.styles.AnimationStyle
import com.carto.styles.AnimationStyleBuilder
import com.carto.styles.AnimationType
import com.carto.styles.MarkerStyleBuilder
import com.carto.utils.BitmapUtils
import org.neshan.common.model.LatLng
import org.neshan.mapsdk.MapView
import org.neshan.mapsdk.model.Marker
import org.neshan.kotlinsample.R

class MarkerClusteringActivity : AppCompatActivity() {

    // map UI element
    private lateinit var map: MapView

    // marker animation style
    private var animSt: AnimationStyle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marker_clustering)
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

        // when clicked on map, a red marker will added in clicked location on map and it's clusterable
        map.setOnMapClickListener { latLng: LatLng ->
            map.addMarker(createMarker(latLng, true))
        }

        // when long click on map, a blue marker will added to clicked location on map and it's not clusterable
        map.setOnMapLongClickListener {
            map.addMarker(createMarker(it, false), false)
        }

        // Remove marker when click on it
        map.setOnMarkerClickListener {
            map.removeMarker(it)
        }
    }

    // We use findViewByID for every element in our layout file here
    private fun initViews() {
        map = findViewById(R.id.mapview)
    }

    // Initializing map
    private fun initMap() {
        map.moveCamera(LatLng(35.767234, 51.330743), 0f)
        map.setZoom(14f, 0f)
    }

    fun toggleClustering(view: View) {
        val toggleButton = view as ToggleButton
        map.settings.isMarkerClusteringEnabled = toggleButton.isChecked
    }

    // This method gets a LatLng as input and adds a marker on that position
    private fun createMarker(loc: LatLng, clusterable: Boolean): Marker {
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
        if (clusterable) {
            markStCr.bitmap = BitmapUtils.createBitmapFromAndroidBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.ic_marker
                )
            )
        } else {
            markStCr.bitmap = BitmapUtils.createBitmapFromAndroidBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.ic_marker_blue
                )
            )
        }
        // AnimationStyle object - that was created before - is used here
        markStCr.animationStyle = animSt
        val markSt = markStCr.buildStyle()

        // Creating marker
        return Marker(loc, markSt)
    }

}