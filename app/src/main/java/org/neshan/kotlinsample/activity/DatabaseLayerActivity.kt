package org.neshan.kotlinsample.activity

import android.annotation.SuppressLint
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.carto.core.ScreenBounds
import com.carto.core.ScreenPos
import com.carto.styles.AnimationStyleBuilder
import com.carto.styles.AnimationType
import com.carto.styles.MarkerStyleBuilder
import com.carto.utils.BitmapUtils
import org.neshan.common.model.LatLng
import org.neshan.common.model.LatLngBounds
import org.neshan.kotlinsample.R
import org.neshan.kotlinsample.database_helper.AssetDatabaseHelper
import org.neshan.mapsdk.MapView
import org.neshan.mapsdk.model.Marker
import java.io.IOException

class DatabaseLayerActivity : AppCompatActivity() {

    private lateinit var map: MapView

    private var pointsDB: SQLiteDatabase? = null
    private val markers = ArrayList<Marker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_database_layer)
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

        // do after 1 secend delay
        Handler(Looper.getMainLooper()).postDelayed({
            // copy database.sqlite file from asset folder to /data/data/... and read points and add marker on map
            getDBPoints()
        }, 1000)
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

    // copy database.sqlite file from asset folder to /data/data/... and read points and add marker on map
    @SuppressLint("Range")
    private fun getDBPoints() {
        // we create an AssetDatabaseHelper object, create a new database in mobile storage
        // and copy database.sqlite file into the new created database
        // Then we open the database and return the SQLiteDatabase object
        val myDbHelper = AssetDatabaseHelper(this)
        try {
            myDbHelper.createDataBase()
        } catch (ioe: IOException) {
            throw Error("Unable to create database")
        }
        try {
            pointsDB = myDbHelper.openDataBase()
        } catch (sqle: SQLException) {
            sqle.printStackTrace()
        }


        // creating a cursor and query all rows of points table
        val cursor: Cursor = pointsDB!!.rawQuery("select * from points", null)

        //reading all points and adding a marker for each one
        if (cursor.moveToFirst()) {
            // variable for creating bound
            // min = south-west
            // max = north-east
            var minLat = Double.MAX_VALUE
            var minLng = Double.MAX_VALUE
            var maxLat = Double.MIN_VALUE
            var maxLng = Double.MIN_VALUE
            while (!cursor.isAfterLast) {
                val lng:Double = cursor.getDouble(cursor.getColumnIndex("lng"))
                val lat:Double = cursor.getDouble(cursor.getColumnIndex("lat"))
                Log.i("POINTS", "getDBPoints: $lat $lng")
                val LatLng = LatLng(lat, lng)

                // validating min and max
                minLat = Math.min(LatLng.latitude, minLat)
                minLng = Math.min(LatLng.longitude, minLng)
                maxLat = Math.max(LatLng.latitude, maxLat)
                maxLng = Math.max(LatLng.longitude, maxLng)
                markers.add(addMarker(LatLng))
                cursor.moveToNext()
            }
            map.moveToCameraBounds(
                LatLngBounds(LatLng(minLat, minLng), LatLng(maxLat, maxLng)),
                ScreenBounds(
                    ScreenPos(0f, 0f), ScreenPos(map.height.toFloat(), map.height.toFloat())
                ),
                true, 0.25f
            )
            Log.i("BOUND", "getDBPoints: $minLat $minLng----$maxLat $maxLng")
        }
        cursor.close()
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

        // Adding marker to map!
        map.addMarker(marker)
        return marker
    }

    fun toggleDatabaseLayer(view: View) {
        val toggleButton = view as ToggleButton
        if (toggleButton.isChecked) getDBPoints() else for (marker in markers) {
            map.removeMarker(marker)
        }
    }
}