package org.neshan.kotlinsample.activity

import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.carto.core.ScreenBounds
import com.carto.core.ScreenPos
import com.carto.styles.AnimationStyleBuilder
import com.carto.styles.AnimationType
import com.carto.styles.MarkerStyleBuilder
import com.carto.utils.BitmapUtils
import org.json.JSONException
import org.json.JSONObject
import org.neshan.common.model.LatLng
import org.neshan.common.model.LatLngBounds
import org.neshan.mapsdk.MapView
import org.neshan.mapsdk.model.Marker
import org.neshan.kotlinsample.R
import org.neshan.kotlinsample.task.DownloadTask

class OnlineLayerActivity : AppCompatActivity(), DownloadTask.Callback {

    // map UI element
    private lateinit var map: MapView
    private val markers = ArrayList<Marker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online_layer)
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

        if (checkInternet()) {
            val downloadTask = DownloadTask(this@OnlineLayerActivity)
            downloadTask.execute("https://api.neshan.org/points.geojson")
        }
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

    // This method gets a LatLng as input and adds a marker on that position
    private fun addMarker(loc: LatLng): Marker {
        // Creating animation for marker. We should use an object of type AnimationStyleBuilder, set
        // all animation features on it and then call buildStyle() method that returns an object of type
        // AnimationStyle
        val animStBl = AnimationStyleBuilder()
        animStBl.fadeAnimationType = AnimationType.ANIMATION_TYPE_SMOOTHSTEP
        animStBl.sizeAnimationType = AnimationType.ANIMATION_TYPE_SPRING
        animStBl.phaseInDuration = 0.5f
        animStBl.phaseOutDuration = 0.5f
        val animSt = animStBl.buildStyle()

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
        val marker = Marker(loc, markSt)

        // Adding marker to markerLayer, or showing marker on map!
        map.addMarker(marker)
        return marker
    }

    //Download markers and add to map
    fun toggleOnlineLayer(view: View) {
        val toggleButton = view as ToggleButton
        if (toggleButton.isChecked) {
            if (checkInternet()) {
                val downloadTask = DownloadTask(this@OnlineLayerActivity)
                downloadTask.execute("https://api.neshan.org/points.geojson")
            }
        } else {
            for (marker in markers) {
                map.removeMarker(marker)
            }
        }
    }

    // Check for Internet connectivity.
    private fun checkInternet(): Boolean {
        val connectivityManager =
            (this.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager)
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null
    }

    override fun onJsonDownloaded(jsonObject: JSONObject?) {
        try {
            val features = jsonObject?.getJSONArray("features")
            // variable for creating bound
            // min = south-west
            // max = north-east
            var minLat = Double.MAX_VALUE
            var minLng = Double.MAX_VALUE
            var maxLat = Double.MIN_VALUE
            var maxLng = Double.MIN_VALUE
            for (i in 0 until features!!.length()) {
                val geometry = features.getJSONObject(i)?.getJSONObject("geometry")
                val coordinates = geometry?.getJSONArray("coordinates")
                val LatLng = LatLng(coordinates?.getDouble(1)!!, coordinates.getDouble(0))

                // validating min and max
                minLat = Math.min(LatLng.latitude, minLat)
                minLng = Math.min(LatLng.longitude, minLng)
                maxLat = Math.max(LatLng.latitude, maxLat)
                maxLng = Math.max(LatLng.longitude, maxLng)
                markers.add(addMarker(LatLng))
            }
            map.moveToCameraBounds(
                LatLngBounds(LatLng(minLat, minLng), LatLng(maxLat, maxLng)),
                ScreenBounds(
                    ScreenPos(0f, 0f),
                    ScreenPos(map.width.toFloat(), map.height.toFloat())
                ),
                true, 0.25f
            )
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}