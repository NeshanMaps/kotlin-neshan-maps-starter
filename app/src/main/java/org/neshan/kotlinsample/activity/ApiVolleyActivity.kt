package org.neshan.kotlinsample.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.carto.styles.AnimationStyleBuilder
import com.carto.styles.AnimationType
import com.carto.styles.MarkerStyleBuilder
import com.carto.utils.BitmapUtils
import org.json.JSONObject
import org.neshan.common.model.LatLng
import org.neshan.kotlinsample.R
import org.neshan.mapsdk.MapView
import org.neshan.mapsdk.model.Marker
import java.nio.charset.StandardCharsets

class ApiVolleyActivity : AppCompatActivity() {

    // map UI element
    private lateinit var map: MapView

    private lateinit var addressTitle: TextView
    private lateinit var addressDetails: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_volley)
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
        map.setOnMapLongClickListener { latLng: LatLng ->
            // addMarker adds a marker (pretty self explanatory :D) to the clicked location
            addMarker(latLng)

            //calling NeshanReverseAPI to get address of a location and showing it on a bottom sheet
            neshanReverseAPI(latLng)
        }

    }

    // We use findViewByID for every element in our layout file here
    private fun initViews() {
        map = findViewById(R.id.mapview)
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
    }

    private fun neshanReverseAPI(loc: LatLng) {
        val requestURL =
            "https://api.neshan.org/v1/reverse?lat=" + loc.latitude + "&lng=" + loc.longitude
        val latLngAddr =
            String.format("%.6f", loc.latitude) + "," + String.format("%.6f", loc.longitude)
        val requestQueue = Volley.newRequestQueue(this)

        val reverseGeoSearchRequest: StringRequest = object : StringRequest(
            Method.GET,
            requestURL,
            Response.Listener { response: String? ->
                try {
                    val obj = JSONObject(response)
                    val neighbourhood = String(
                        obj.getString("neighbourhood").toByteArray(StandardCharsets.ISO_8859_1),
                        StandardCharsets.UTF_8
                    )
                    val address = String(
                        obj.getString("address").toByteArray(StandardCharsets.ISO_8859_1),
                        StandardCharsets.UTF_8
                    )

                    // if server was able to return neighbourhood and address to us
                    if (neighbourhood != "null" && address != "null") {
                        addressTitle.text = neighbourhood
                        addressDetails.text = address
                    } else {
                        addressTitle.text = "آدرس نامشخص"
                        addressDetails.text = latLngAddr
                    }
                } catch (e: Exception) {
                    addressTitle.text = "آدرس نامشخص"
                    addressDetails.text = latLngAddr
                }
            }, Response.ErrorListener {
                    error: VolleyError -> error.printStackTrace()
            }) {
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                // TODO: replace "YOUR_API_KEY" with your api key
                params["Api-Key"] = "service.kREahwU7lND32ygT9ZgPFXbwjzzKukdObRZsnUAJ"
                return params
            }
        }

//         Add the request to the queue
        requestQueue.add(reverseGeoSearchRequest)
    }
}