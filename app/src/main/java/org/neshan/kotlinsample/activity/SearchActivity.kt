package org.neshan.kotlinsample.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.carto.core.ScreenBounds
import com.carto.core.ScreenPos
import com.carto.styles.MarkerStyle
import com.carto.styles.MarkerStyleBuilder
import com.carto.utils.BitmapUtils
import org.neshan.common.model.LatLng
import org.neshan.common.model.LatLngBounds
import org.neshan.kotlinsample.R
import org.neshan.kotlinsample.adapter.SearchAdapter
import org.neshan.mapsdk.MapView
import org.neshan.mapsdk.model.Marker
import org.neshan.servicessdk.search.NeshanSearch
import org.neshan.servicessdk.search.model.Item
import org.neshan.servicessdk.search.model.NeshanSearchResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity(), SearchAdapter.OnSearchItemListener {

    private lateinit var map: MapView

    private val TAG = "Search"
    private lateinit var editText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var items: List<Item>
    private lateinit var adapter: SearchAdapter

    // map UI element
    private lateinit var centerMarker: Marker
    private val markers = ArrayList<Marker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
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

        //listen for search text change
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                search(s.toString())
                Log.i(TAG, "afterTextChanged: $s")
            }
        })

        editText.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
                if (p1 == EditorInfo.IME_ACTION_SEARCH) {
                    closeKeyBoard()
                    search(editText.text.toString())
                }
                return false
            }
        })
    }

    // We use findViewByID for every element in our layout file here
    private fun initViews() {
        map = findViewById(R.id.mapview)
        editText = findViewById(R.id.search_editText)
        recyclerView = findViewById(R.id.recyclerView)
        items = java.util.ArrayList()
        adapter = SearchAdapter(items, this@SearchActivity)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun initMap() {
        // Setting map focal position to a fixed position and setting camera zoom
        val latLng = LatLng(35.767234, 51.330743)
        map.moveCamera(latLng, 0f)
        map.setZoom(14f, 0f)
        map.settings.isZoomControlsEnabled = true
        centerMarker = Marker(latLng, getCenterMarkerStyle())
        map.addMarker(centerMarker)
    }

    private fun search(term: String) {
        val searchPosition: LatLng = map.getCameraTargetPosition()
        updateCenterMarker(searchPosition)
        NeshanSearch.Builder("YOUR_API_KEY")
            .setLocation(searchPosition)
            .setTerm(term)
            .build().call(object : Callback<NeshanSearchResult?> {
                override fun onResponse(
                    call: Call<NeshanSearchResult?>,
                    response: Response<NeshanSearchResult?>
                ) {
                    if (response.code() == 403) {
                        Toast.makeText(
                            this@SearchActivity,
                            "کلید دسترسی نامعتبر",
                            Toast.LENGTH_LONG
                        ).show()
                        return
                    }
                    if (response.body() != null) {
                        val result = response.body()
                        items = result!!.items
                        adapter.updateList(items)
                    }
                }

                override fun onFailure(call: Call<NeshanSearchResult?>, t: Throwable) {
                    Log.i(TAG, "onFailure: " + t.message)
                    Toast.makeText(this@SearchActivity, "ارتباط برقرار نشد!", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    private fun updateCenterMarker(LatLng: LatLng) {
        centerMarker.latLng = LatLng
    }

    private fun getCenterMarkerStyle(): MarkerStyle? {
        val markerStyleBuilder = MarkerStyleBuilder()
        markerStyleBuilder.size = 50f
        markerStyleBuilder.bitmap = BitmapUtils.createBitmapFromAndroidBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.center_marker
            )
        )
        return markerStyleBuilder.buildStyle()
    }

    private fun addMarker(LatLng: LatLng, size: Float): Marker? {
        val marker = Marker(LatLng, getMarkerStyle(size))
        map.addMarker(marker)
        markers.add(marker)
        return marker
    }

    private fun getMarkerStyle(size: Float): MarkerStyle? {
        val styleCreator = MarkerStyleBuilder()
        styleCreator.size = size
        styleCreator.bitmap = BitmapUtils.createBitmapFromAndroidBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.ic_marker
            )
        )
        return styleCreator.buildStyle()
    }

    private fun closeKeyBoard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        if (currentFocus != null) {
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }

    fun showSearchClick(view: View?) {
        closeKeyBoard()
        adapter!!.updateList(items!!)
        clearMarkers()
    }

    private fun clearMarkers() {
        map.clearMarkers()
        markers.clear()
    }

    fun showMarkersClick(view: View) {
        adapter.updateList(java.util.ArrayList())
        closeKeyBoard()
        clearMarkers()
        var minLat = Double.MAX_VALUE
        var minLng = Double.MAX_VALUE
        var maxLat = Double.MIN_VALUE
        var maxLng = Double.MIN_VALUE
        for (item in items) {
            val location = item.location
            val latLng = location.latLng
            markers.add(addMarker(latLng, 15f)!!)
            minLat = Math.min(latLng.latitude, minLat)
            minLng = Math.min(latLng.longitude, minLng)
            maxLat = Math.max(latLng.latitude, maxLat)
            maxLng = Math.max(latLng.longitude, maxLng)
        }
        if (items.isNotEmpty()) {
            map.moveToCameraBounds(
                LatLngBounds(LatLng(minLat, minLng), LatLng(maxLat, maxLng)),
                ScreenBounds(
                    ScreenPos(0f, 0f),
                    ScreenPos(map.width.toFloat(), map.height.toFloat())
                ),
                true, 0.5f
            )
        }
    }

    override fun onSearchItemClick(LatLng: LatLng?) {
        closeKeyBoard()
        clearMarkers()
        adapter.updateList(java.util.ArrayList())
        map.moveCamera(LatLng, 0f)
        map.setZoom(16f, 0f)
        addMarker(LatLng!!, 30f)
    }
}