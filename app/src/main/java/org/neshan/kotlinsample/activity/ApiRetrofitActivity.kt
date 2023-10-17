package org.neshan.kotlinsample.activity

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.neshan.common.model.LatLng
import org.neshan.common.network.RetrofitClientInstance
import org.neshan.kotlinsample.R
import org.neshan.kotlinsample.model.address.NeshanAddress
import org.neshan.mapsdk.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiRetrofitActivity : AppCompatActivity() {

    private lateinit var map: MapView
    private lateinit var addressTitle: TextView
    private lateinit var progressBar: ProgressBar

    private val getDataService: org.neshan.kotlinsample.network.ReverseService =
        RetrofitClientInstance.getRetrofitInstance()
            .create(org.neshan.kotlinsample.network.ReverseService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_api_retrofit)
    }

    override fun onStart() {
        super.onStart()
        // everything related to ui is initialized here
        initLayoutReferences()
    }

    private fun initLayoutReferences() {
        // Initializing views
        initViews()
        // Initializing mapView element
        initMap()
    }

    // We use findViewByID for every element in our layout file here
    private fun initViews() {
        map = findViewById(R.id.mapview)
        addressTitle = findViewById(R.id.addressTitle)
        progressBar = findViewById(R.id.progressBar)
    }

    // Initializing map
    private fun initMap() {
        // Setting map focal position to a fixed position and setting camera zoom
        map.moveCamera(LatLng(35.767234, 51.330743), 0f)
        map.setZoom(14f, 0f)

        map.setOnCameraMoveFinishedListener { i: Int ->
            runOnUiThread { progressBar.visibility = View.VISIBLE }
            getReverseApi(map.cameraTargetPosition)
        }
    }

    private fun getReverseApi(currentLocation: LatLng) {
        getDataService.getReverse(currentLocation.latitude, currentLocation.longitude)
            .enqueue(object : Callback<NeshanAddress> {
                override fun onResponse(
                    call: Call<NeshanAddress>,
                    response: Response<NeshanAddress>
                ) {
                    if (response != null && response.body() != null) {
                        val address: String? = response.body()!!.address
                        if (address != null && !address.isEmpty()) {
                            addressTitle.text = address
                        } else {
                            addressTitle.text = "معبر بی‌نام"
                        }
                        progressBar.visibility = View.INVISIBLE
                    }
                }

                override fun onFailure(call: Call<NeshanAddress>, t: Throwable) {
                    addressTitle.text = "معبر بی‌نام"
                    progressBar.visibility = View.INVISIBLE
                }
            })
    }
}