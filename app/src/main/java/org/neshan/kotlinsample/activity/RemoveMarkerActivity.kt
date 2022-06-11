package org.neshan.kotlinsample.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.carto.styles.AnimationStyle
import com.carto.styles.AnimationStyleBuilder
import com.carto.styles.AnimationType
import com.carto.styles.MarkerStyleBuilder
import com.carto.utils.BitmapUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import org.neshan.common.model.LatLng
import org.neshan.mapsdk.MapView
import org.neshan.mapsdk.MapView.GONE
import org.neshan.mapsdk.MapView.VISIBLE
import org.neshan.mapsdk.model.Marker
import org.neshan.kotlinsample.R

class RemoveMarkerActivity : AppCompatActivity() {

    // map UI element
    private lateinit var map: MapView
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>

    // bottom sheet layout and behavior
    private lateinit var removeMarkerBottomSheet: View

    // markerId bottom sheet
    private lateinit var markerId: TextView

    // save selected Marker for select and deselect function
    private var selectedMarker: Marker? = null

    // remove marker button
    private lateinit var removeMarker: Button

    // Marker that will be added on map
    private var marker: Marker? = null

    // marker animation style
    private var animSt: AnimationStyle? = null

    // Tip Strings
    var firstTipString = "<b>" + "قدم اول: " + "</b> " + "برای ایجاد پین جدید نگهدارید!"
    var secondTipString = "<b>" + "قدم دوم: " + "</b> " + "برای حذف روی پین لمس کنید!"
    private var markerIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remove_marker)
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
        // map listener: Long Click -> add marker Single Click -> deselect marker
        map.setOnMapLongClickListener { var1 -> // check the bottom sheet expanded or collapsed
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                if (selectedMarker == null) {
                    // if bottom sheet is expanded and no marker selected second tip is going up (for just one time)
                    collapseBottomSheet()
                    // delay for collapsing then expanding bottom sheet
                    removeMarkerBottomSheet.postDelayed({ expandBottomSheet() }, 200)
                    removeMarkerBottomSheet.post { markerId.text = Html.fromHtml(secondTipString) }
                } else {
                    // if bottom sheet is expanded and any marker selected deselect that marker by long tap
                    deselectMarker(selectedMarker)
                }
            }
            // addMarker adds a marker (pretty self explanatory :D) to the clicked location
            addMarker(var1, "Marker " + ++markerIndex)
        }

        map.setOnMapClickListener { selectedMarker?.let { deselectMarker(it) } }

        // marker listener for select and deselect markers
        map.setOnMarkerClickListener { marker ->
            if (selectedMarker != null) {
                // deselect marker when tap on a marker and a marker is selected
                deselectMarker(selectedMarker)
            } else {
                // select marker when tap on a marker
                selectMarker(marker)
                removeMarkerBottomSheet.post {
                    markerId.text = "از حدف پین " + marker.title + " اطمینان دارید؟"
                    removeMarker.visibility = VISIBLE
                }
            }
        }

        // remove marker and deselect that marker
        removeMarker.setOnClickListener {
            map.removeMarker(selectedMarker)
            deselectMarker(selectedMarker)
        }
    }

    // We use findViewByID for every element in our layout file here
    private fun initViews() {
        map = findViewById(R.id.mapview)
        removeMarker = findViewById(R.id.remove_marker)
        markerId = findViewById(R.id.marker_id)
        removeMarkerBottomSheet = findViewById(R.id.remove_marker_bottom_sheet_include)
        bottomSheetBehavior = BottomSheetBehavior.from<View>(removeMarkerBottomSheet)

        removeMarker.visibility = GONE
        markerId.text = Html.fromHtml(firstTipString)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        // bottom sheet callback deselect marker for when bottom sheet collapsed manually
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED && selectedMarker != null) {
                    deselectMarker(selectedMarker)
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    // Initializing map
    private fun initMap() {
        // Setting map focal position to a fixed position and setting camera zoom
        map.moveCamera(LatLng(35.767234, 51.330743), 0f)
        map.setZoom(14f, 0f)
    }

    private fun collapseBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun expandBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    // deselect marker and collapsing bottom sheet
    private fun deselectMarker(deselectMarker: Marker?) {
        collapseBottomSheet()
        changeMarkerToBlue(deselectMarker)
        selectedMarker = null
    }

    // change selected marker color to blue
    private fun changeMarkerToBlue(redMarker: Marker?) {
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
        redMarker!!.setStyle(blueMarkSt)
    }

    // This method gets a LatLng as input and adds a marker on that position
    private fun addMarker(loc: LatLng, title: String) {
        // If you want to have only one marker on map at a time, uncomment next line to delete all markers before adding a new marker
//        markerLayer.clear();

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
                resources, R.drawable.ic_marker_blue
            )
        )
        // AnimationStyle object - that was created before - is used here
        markStCr.animationStyle = animSt
        val markSt = markStCr.buildStyle()

        // Creating marker
        marker = Marker(loc, markSt).setTitle(title)
        // Setting a metadata on marker, here we have an id for each marker

        // Adding marker to map!
        map.addMarker(marker)
    }

    // select marker and expanding bottom sheet
    private fun selectMarker(selectMarker: Marker) {
        expandBottomSheet()
        changeMarkerToRed(selectMarker)
        selectedMarker = selectMarker
    }

    private fun changeMarkerToRed(blueMarker: Marker) {
        // create new marker style
        val markStCr = MarkerStyleBuilder()
        markStCr.size = 30f
        // Setting a new bitmap as marker
        markStCr.bitmap = BitmapUtils.createBitmapFromAndroidBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.ic_marker
            )
        )
        markStCr.animationStyle = animSt
        val redMarkSt = markStCr.buildStyle()

        // changing marker style using setStyle
        blueMarker.setStyle(redMarkSt)
    }

    // customize back button for when a marker is selected
    override fun onBackPressed() {
        if (selectedMarker != null) {
            deselectMarker(selectedMarker)
        } else {
            super.onBackPressed()
        }
    }
}