package org.neshan.kotlinsample.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.carto.styles.MarkerStyleBuilder
import com.carto.utils.BitmapUtils
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.karumi.dexter.BuildConfig
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import org.neshan.common.model.LatLng
import org.neshan.kotlinsample.R
import org.neshan.mapsdk.MapView
import org.neshan.mapsdk.model.Marker
import java.text.DateFormat
import java.util.*

class UserLocationActivity : AppCompatActivity() {

    private val TAG: String = UserLocationActivity::class.java.name

    // location updates interval - 1 sec
    private val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 1000

    // fastest updates interval - 1 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS: Long = 1000

    // used to track request permissions
    private val REQUEST_CODE = 123

    // map UI element
    private lateinit var map: MapView

    // User's current location
    private var userLocation: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var settingsClient: SettingsClient
    private lateinit var locationRequest: LocationRequest
    private var locationSettingsRequest: LocationSettingsRequest? = null
    private var locationCallback: LocationCallback? = null
    private var lastUpdateTime: String? = null

    // boolean flag to toggle the ui
    private var mRequestingLocationUpdates: Boolean? = null
    private var marker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_location)
    }

    override fun onStart() {
        super.onStart()

        // everything related to ui is initialized here
        initLayoutReferences()
        // Initializing user location
        initLocation()
        startReceivingLocationUpdates()
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    // Initializing layout references (views, map and map events)
    private fun initLayoutReferences() {
        // Initializing views
        initViews()
        // Initializing mapView element
        initMap()
    }

    private fun initMap() {
        // Setting map focal position to a fixed position and setting camera zoom
        map.moveCamera(LatLng(35.767234, 51.330743), 0f)
        map.setZoom(14f, 0f)
    }

    private fun initViews() {
        map = findViewById(R.id.mapview)
    }

    private fun initLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        settingsClient = LocationServices.getSettingsClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                // location is received
                userLocation = locationResult.lastLocation
                lastUpdateTime = DateFormat.getTimeInstance().format(Date())
                onLocationChange()
            }
        }
        mRequestingLocationUpdates = false
        locationRequest = LocationRequest()
        locationRequest.numUpdates = 10
        locationRequest.interval = UPDATE_INTERVAL_IN_MILLISECONDS
        locationRequest.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        locationSettingsRequest = builder.build()
    }

    fun startReceivingLocationUpdates() {
        // Requesting ACCESS_FINE_LOCATION using Dexter library
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    mRequestingLocationUpdates = true
                    startLocationUpdates()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    if (response.isPermanentlyDenied()) {
                        // open device settings when the permission is
                        // denied permanently
                        openSettings()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    /**
     * Starting location updates
     * Check whether location settings are satisfied and then
     * location updates will be requested
     */
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        settingsClient.checkLocationSettings(locationSettingsRequest).addOnSuccessListener(this) {
            Log.i(TAG, "All location settings are satisfied.")
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.myLooper()
            )
            onLocationChange()
        }
            .addOnFailureListener(this)
            { e ->
                val statusCode = (e as ApiException).statusCode
                when (statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        Log.i(
                            TAG,
                            "Location settings are not satisfied. Attempting to upgrade " +
                                    "location settings "
                        )
                        if (mRequestingLocationUpdates == true) {
                            try {
                                // Show the dialog by calling startResolutionForResult(), and check the
                                // result in onActivityResult().
                                val rae = e as ResolvableApiException
                                rae.startResolutionForResult(this, REQUEST_CODE)
                            } catch (sie: SendIntentException) {
                                Log.i(
                                    TAG,
                                    "PendingIntent unable to execute request."
                                )
                            }
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        val errorMessage = "Location settings are inadequate, and cannot be " +
                                "fixed here. Fix in Settings."
                        Log.e(
                            TAG,
                            errorMessage
                        )
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
                onLocationChange()
            }
    }

    fun stopLocationUpdates() {
        // Removing location updates
        fusedLocationClient.removeLocationUpdates(locationCallback)
            .addOnCompleteListener(
                this
            ) {
                Toast.makeText(applicationContext, "Location updates stopped!", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun onLocationChange() {
        if (userLocation != null) {
            addUserMarker(LatLng(userLocation!!.latitude, userLocation!!.longitude))
        }
    }

    private fun openSettings() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts(
            "package",
            BuildConfig.APPLICATION_ID, null
        )
        intent.data = uri
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun addUserMarker(loc: LatLng) {
        //remove existing marker from map
        if (marker != null) {
            map.removeMarker(marker)
        }
        // Creating marker style. We should use an object of type MarkerStyleCreator, set all features on it
        // and then call buildStyle method on it. This method returns an object of type MarkerStyle
        val markStCr = MarkerStyleBuilder()
        markStCr.size = 30f
        markStCr.bitmap = BitmapUtils.createBitmapFromAndroidBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.ic_marker
            )
        )
        val markSt = markStCr.buildStyle()

        // Creating user marker
        marker = Marker(loc, markSt)

        // Adding user marker to map!
        map.addMarker(marker)
    }

    fun focusOnUserLocation(view: View?) {
        if (userLocation != null) {
            map.moveCamera(
                LatLng(userLocation!!.latitude, userLocation!!.longitude), 0.25f
            )
            map.setZoom(15f, 0.25f)
        }
    }
}