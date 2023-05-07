package org.neshan.kotlinsample.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import org.neshan.kotlinsample.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun goToDrawLineActivity(view: View) {
        val intent = Intent(this, DrawPolylineActivity::class.java)
        startActivity(intent)
    }

    fun goToAddMarkerActivity(view: View) {
        val intent = Intent(this, AddMarkerActivity::class.java)
        startActivity(intent)
    }

    fun goToDrawPolygonActivity(view: View) {
        val intent = Intent(this, DrawPolygonActivity::class.java)
        startActivity(intent)
    }

    fun goToChangeCameraTiltActivity(view: View) {
        val intent = Intent(this, TiltCameraActivity::class.java)
        startActivity(intent)
    }

    fun goToChangeCameraBearingActivity(view: View) {
        val intent = Intent(this, CameraBearingActivity::class.java)
        startActivity(intent)
    }

    fun goToChangeStyleActivity(view: View) {
        val intent = Intent(this, ChangeStyleActivity::class.java)
        startActivity(intent)
    }

    fun goToUserLocationActivity(view: View) {
        val intent = Intent(this, UserLocationActivity::class.java)
        startActivity(intent)
    }

    fun goToTrafficLayerActivity(view: View) {
        val intent = Intent(this, TrafficLayerActivity::class.java)
        startActivity(intent)
    }

    fun goToOnlineLayerActivity(view: View) {
        val intent = Intent(this, OnlineLayerActivity::class.java)
        startActivity(intent)
    }

    fun goToPOILayerActivity(view: View) {
        val intent = Intent(this, POILayerActivity::class.java)
        startActivity(intent)
    }

    fun goToDatabaseLayerActivity(view: View) {
        val intent = Intent(this, DatabaseLayerActivity::class.java)
        startActivity(intent)
    }

    fun goToAPIRetrofitActivity(view: View) {
        val intent = Intent(this, org.neshan.kotlinsample.activity.ApiRetrofitActivity::class.java)
        startActivity(intent)
    }

    fun goToAPIVolleyActivity(view: View) {
        val intent = Intent(this, ApiVolleyActivity::class.java)
        startActivity(intent)
    }

    fun goToAPIOkHttpActivity(view: View) {
        val intent = Intent(this, ApiOkHttpActivity::class.java)
        startActivity(intent)
    }

    fun goToLabelActivity(view: View) {
        val intent = Intent(this, AddLabelActivity::class.java)
        startActivity(intent)
    }

    fun goToRemoveMarkerActivity(view: View) {
        val intent = Intent(this, RemoveMarkerActivity::class.java)
        startActivity(intent)
    }

    fun goToRoutingActivity(view: View) {
        val intent = Intent(this, RoutingActivity::class.java)
        startActivity(intent)
    }

    fun goToSearchActivity(view: View) {
        val intent = Intent(this, SearchActivity::class.java)
        startActivity(intent)
    }

    fun goToMarkerClusteringActivity(view: View) {
        val intent = Intent(this, MarkerClusteringActivity::class.java)
        startActivity(intent)
    }

    fun openNeshanLink(view: View) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://developer.neshan.org/")))
    }

    fun openGithubLink(view: View) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://github.com/NeshanMaps/kotlin-neshan-maps-starter")
            )
        )
    }

    fun goToDrawCircleActivity(view: View) {
        val intent = Intent(this, DrawCircleActivity::class.java)
        startActivity(intent)
    }

}
