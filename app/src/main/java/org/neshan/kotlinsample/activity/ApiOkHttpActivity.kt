package org.neshan.kotlinsample.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.carto.styles.AnimationStyleBuilder
import com.carto.styles.AnimationType
import com.carto.styles.MarkerStyleBuilder
import com.carto.utils.BitmapUtils
import okhttp3.*
import org.json.JSONObject
import org.neshan.common.model.LatLng
import org.neshan.kotlinsample.R
import org.neshan.mapsdk.MapView
import org.neshan.mapsdk.MapView.OnMapLongClickListener
import org.neshan.mapsdk.model.Marker
import java.io.IOException

class ApiOkHttpActivity : AppCompatActivity() {

    // map UI element
    private lateinit var map: MapView

    //ui elements in bottom sheet
    private lateinit var addressTitle: TextView
    private lateinit var addressDetails: TextView

    // layer number in which map is added
    private val BASE_MAP_INDEX = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_api_ok_http)
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
        // MapEventListener gets all events on map, including single tap, double tap, long press, etc
        // we should check event type by calling getClickType() on mapClickInfo (from ClickData class)

        // when long clicked on map, a marker is added in clicked location
        // MapEventListener gets all events on map, including single tap, double tap, long press, etc
        // we should check event type by calling getClickType() on mapClickInfo (from ClickData class)
        map.setOnMapLongClickListener(OnMapLongClickListener { clickedLocation: LatLng ->
            addMarker(clickedLocation)

            //calling NeshanReverseAPI to get address of a location and showing it on a bottom sheet
            neshanReverseAPI(clickedLocation)
        })
    }

    // We use findViewByID for every element in our layout file here
    private fun initViews() {
        map = findViewById(R.id.mapview)

        // UI elements in bottom sheet
        addressTitle = findViewById(R.id.title)
        addressDetails = findViewById(R.id.details)
    }

    // Initializing map
    private fun initMap() {
        // Setting map focal position to a fixed position and setting camera zoom
        map.moveCamera(LatLng(35.767234, 51.330743), 0f)
        map.setZoom(14f, 0f)
    }

    // This method gets a LatLng as input and adds a marker on that position
    private fun addMarker(loc: LatLng) {

        // Creating animation for marker. We should use an object of type AnimationStyleBuilder, set
        // all animation features on it and then call buildStyle() method that returns an object of type
        // AnimationStyle
        val animStBl = AnimationStyleBuilder()
        animStBl.fadeAnimationType = AnimationType.ANIMATION_TYPE_SMOOTHSTEP
        animStBl.sizeAnimationType = AnimationType.ANIMATION_TYPE_SPRING
        animStBl.phaseInDuration = 0.5f
        animStBl.phaseOutDuration = 0.5f
        val animSt = animStBl.buildStyle()

        // Creating marker style. We should use an object of type MarkerStyleBuilder, set all features on it
        // and then call buildStyle method on it. This method returns an object of type MarkerStyle
        val markerStyleBuilder = MarkerStyleBuilder()
        markerStyleBuilder.size = 30f
        markerStyleBuilder.bitmap = BitmapUtils.createBitmapFromAndroidBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.ic_marker
            )
        )
        // AnimationStyle object - that was created before - is used here
        markerStyleBuilder.animationStyle = animSt
        val markSt = markerStyleBuilder.buildStyle()

        // Creating marker
        val marker = Marker(loc, markSt)

        // Adding marker to markerLayer, or showing marker on map!
        map.addMarker(marker)
    }

    private fun neshanReverseAPI(loc: LatLng) {
        val requestURL =
            "https://api.neshan.org/v1/reverse?lat=" + loc.latitude + "&lng=" + loc.longitude
        val latLngAddr =
            String.format("%.6f", loc.latitude) + "," + String.format("%.6f", loc.longitude)

        // adding the created certPinner to OkHttpClient
        val client = OkHttpClient.Builder()
            .build()
        val request = Request.Builder() //TODO: replace "YOUR_API_KEY" with your api key
            .header("Api-Key", "YOUR_API_KEY")
            .url(requestURL)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    throw IOException("Unexpected code $response")
                } else {
                    var neighbourhood = "آدرس نامشخص"
                    var address = latLngAddr
                    try {
                        val jsonData = response.body()!!.string()
                        val obj = JSONObject(jsonData)
                        neighbourhood = obj.getString("neighbourhood")
                        address = obj.getString("address")


                        // if server was able to return neighbourhood and address to us
                        if (neighbourhood == "null" && address == "null") {
                            neighbourhood = "آدرس نامشخص"
                            address = latLngAddr
                        }
                    } catch (e: Exception) {
                        Log.d("nehsnaReverse", Log.getStackTraceString(e))
                        neighbourhood = "آدرس نامشخص"
                        address = latLngAddr
                    } finally {
                        val fNeighbourhood = neighbourhood
                        val fAddrees = address
                        runOnUiThread {
                            addressTitle.text = fNeighbourhood
                            addressDetails.text = fAddrees
                        }
                    }
                }
            }
        })
    }
}