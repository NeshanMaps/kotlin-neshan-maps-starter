package org.neshan.kotlinsample.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.carto.graphics.Color
import com.carto.styles.*
import com.carto.utils.BitmapUtils
import org.neshan.common.model.LatLng
import org.neshan.common.utils.PolylineEncoding
import org.neshan.mapsdk.MapView
import org.neshan.mapsdk.model.Marker
import org.neshan.mapsdk.model.Polyline
import org.neshan.kotlinsample.R
import org.neshan.servicessdk.direction.NeshanDirection
import org.neshan.servicessdk.direction.model.NeshanDirectionResult
import org.neshan.servicessdk.direction.model.Route
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RoutingActivity : AppCompatActivity() {

    // map UI element
    private lateinit var map: MapView

    // define two toggle button and connecting together for two type of routing
    private lateinit var overviewToggleButton: ToggleButton
    private lateinit var stepByStepToggleButton: ToggleButton

    // we save decoded Response of routing encoded string because we don't want request every time we clicked toggle buttons
    private var routeOverviewPolylinePoints: ArrayList<LatLng>? = null
    private var decodedStepByStepPath: ArrayList<LatLng>? = null

    // value for difference mapSetZoom
    private var overview = false

    // Marker that will be added on map
    private lateinit var marker: Marker

    // List of created markers
    private val markers: ArrayList<Marker> = ArrayList()

    // marker animation style
    private var animSt: AnimationStyle? = null

    // drawn path of route
    private var onMapPolyline: Polyline? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_routing)
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
        map.setOnMapLongClickListener {
            if (markers.size < 2) {
                markers.add(addMarker(it));
                if (markers.size == 2) {
                    runOnUiThread {
                        overviewToggleButton.isChecked = true
                        neshanRoutingApi();
                    }
                }
            } else {
                runOnUiThread {
                    Toast.makeText(this@RoutingActivity,"مسیریابی بین دو نقطه انجام میشود!",Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    // We use findViewByID for every element in our layout file here
    private fun initViews() {
        map = findViewById(R.id.mapview)

        // CheckChangeListener for Toggle buttons
        val changeChecker = CompoundButton.OnCheckedChangeListener { toggleButton, isChecked -> // if any toggle button checked:
                if (isChecked) {
                    // if overview toggle button checked other toggle button is uncheck
                    if (toggleButton === overviewToggleButton) {
                        stepByStepToggleButton.isChecked = false
                        overview = true
                    }
                    if (toggleButton === stepByStepToggleButton) {
                        overviewToggleButton.isChecked = false
                        overview = false
                    }
                }
                if (!isChecked && onMapPolyline != null) {
                    map.removePolyline(onMapPolyline)
                }
            }

        // each toggle button has a checkChangeListener for uncheck other toggle button
        overviewToggleButton = findViewById(R.id.overviewToggleButton)
        overviewToggleButton.setOnCheckedChangeListener(changeChecker)

        stepByStepToggleButton = findViewById(R.id.stepByStepToggleButton)
        stepByStepToggleButton.setOnCheckedChangeListener(changeChecker)
    }

    // Initializing map
    private fun initMap() {
        // Setting map focal position to a fixed position and setting camera zoom
        map.moveCamera(LatLng(35.767234, 51.330743), 0f)
        map.setZoom(14f, 0f)
    }

    // call this function with clicking on toggle buttons and draw routing line depend on type of routing requested
    fun findRoute(view: View?) {
        if (markers.size < 2) {
            Toast.makeText(this, "برای مسیریابی باید دو نقطه انتخاب شود", Toast.LENGTH_SHORT).show()
            overviewToggleButton.isChecked = false
            stepByStepToggleButton.isChecked = false
        } else if (overviewToggleButton.isChecked) {
            try {
                map.removePolyline(onMapPolyline)
                onMapPolyline = Polyline(routeOverviewPolylinePoints, getLineStyle())
                //draw polyline between route points
                map.addPolyline(onMapPolyline)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (stepByStepToggleButton.isChecked) {
            try {
                map.removePolyline(onMapPolyline)
                onMapPolyline = Polyline(decodedStepByStepPath, getLineStyle())
                //draw polyline between route points
                map.addPolyline(onMapPolyline)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun addMarker(loc: LatLng): Marker {
        // Creating animation for marker. We should use an object of type AnimationStyleBuilder, set
        // all animation features on it and then call buildStyle() method that returns an object of type
        // AnimationStyle
        val animStBl = AnimationStyleBuilder()
        animStBl.fadeAnimationType = AnimationType.ANIMATION_TYPE_SMOOTHSTEP
        animStBl.sizeAnimationType = AnimationType.ANIMATION_TYPE_SPRING
        animStBl.phaseInDuration = 0.5f
        animStBl.phaseOutDuration = 0.5f
        animSt = animStBl.buildStyle()

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
        marker = Marker(loc, markSt)

        // Adding marker to markerLayer, or showing marker on map!
        map.addMarker(marker)
        return marker
    }

    // request routing method from Neshan Server
    private fun neshanRoutingApi() {
        NeshanDirection.Builder(
            "service.VNlPhrWb3wYRzEYmstQh3GrAXyhyaN55AqUSRR3V",
            markers[0].latLng,
            markers[1].latLng
        )
            .build().call(object : Callback<NeshanDirectionResult?> {
                override fun onResponse(
                    call: Call<NeshanDirectionResult?>,
                    response: Response<NeshanDirectionResult?>
                ) {

                    // two type of routing
                    if (response.body() != null && response.body()!!.routes != null && !response.body()!!.routes.isEmpty()
                    ) {
                        val route: Route = response.body()!!.routes[0]
                        routeOverviewPolylinePoints = java.util.ArrayList(
                            PolylineEncoding.decode(
                                route.overviewPolyline.encodedPolyline
                            )
                        )
                        decodedStepByStepPath = java.util.ArrayList()

                        // decoding each segment of steps and putting to an array
                        for (step in route.legs[0].directionSteps) {
                            decodedStepByStepPath!!.addAll(PolylineEncoding.decode(step.encodedPolyline))
                        }
                        onMapPolyline = Polyline(routeOverviewPolylinePoints, getLineStyle())
                        //draw polyline between route points
                        map.addPolyline(onMapPolyline)
                        // focusing camera on first point of drawn line
                        mapSetPosition(overview)
                    } else {
                        Toast.makeText(this@RoutingActivity, "مسیری یافت نشد", Toast.LENGTH_LONG)
                            .show()
                    }
                }

                override fun onFailure(call: Call<NeshanDirectionResult?>, t: Throwable) {}
            })
    }

    // In this method we create a LineStyleCreator, set its features and call buildStyle() method
    // on it and return the LineStyle object (the same routine as crating a marker style)
    private fun getLineStyle(): LineStyle {
        val lineStCr = LineStyleBuilder()
        lineStCr.color = Color(
            2.toShort(), 119.toShort(), 189.toShort(),
            190.toShort()
        )
        lineStCr.width = 10f
        lineStCr.stretchFactor = 0f
        return lineStCr.buildStyle()
    }

    // for overview routing we zoom out and review hole route and for stepByStep routing we just zoom to first marker position
    private fun mapSetPosition(overview: Boolean) {
        val centerFirstMarkerX = markers[0].latLng.latitude
        val centerFirstMarkerY = markers[0].latLng.longitude
        if (overview) {
            val centerFocalPositionX = (centerFirstMarkerX + markers[1].latLng.latitude) / 2
            val centerFocalPositionY = (centerFirstMarkerY + markers[1].latLng.longitude) / 2
            map.moveCamera(LatLng(centerFocalPositionX, centerFocalPositionY), 0.5f)
            map.setZoom(14f, 0.5f)
        } else {
            map.moveCamera(LatLng(centerFirstMarkerX, centerFirstMarkerY), 0.5f)
            map.setZoom(14f, 0.5f)
        }
    }
}