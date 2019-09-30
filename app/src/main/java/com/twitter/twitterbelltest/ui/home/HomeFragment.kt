package com.twitter.twitterbelltest.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.twitter.twitterbelltest.location.LocationProvider
import com.twitter.twitterbelltest.location.LocationProviderImpl
import com.twitter.twitterbelltest.ui.home.adapter.MapPinAdapter
import com.twitter.twitterbelltest.utils.*
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment(), OnMapReadyCallback {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var googleMap: GoogleMap
    private var mapReady = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root =
            inflater.inflate(com.twitter.twitterbelltest.R.layout.fragment_home, container, false)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        homeViewModel.getTweetsObservable().observe(this, Observer {
            it?.let {
                showTweetsOnMap()
            }
        })
        homeViewModel.initialized()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val fragmentManager = activity?.supportFragmentManager
        val mapFragment = SupportMapFragment.newInstance()
        fragmentManager?.beginTransaction()
            ?.replace(com.twitter.twitterbelltest.R.id.map, mapFragment)?.commit()
        mapFragment?.getMapAsync(this)
        radius.text = getString(com.twitter.twitterbelltest.R.string.textview_radius, getRadius())
        seekBar.progress = getRadius()*1000
    }

    private fun showTweetsOnMap() {
        if (homeViewModel.getTweetsObservable().value.isNullOrEmpty() || !mapReady) {
            return
        }
        val queue = homeViewModel.getTweetsObservable().value!!
        // remove all old markers
        googleMap.clear()
        addCircle()

        queue
            .map { Pair(it, it.getTweetLatLng()) }
            .filter { it.second != null }  // if we have a non null latlng add marker
            .forEach {
                val text = it.first.getStringTweetItemModel()
                googleMap.addMarker(
                    MarkerOptions()
                        .position(it.second!!)
                        .title(it.first.user.name)
                        .snippet(text)
                )
            }

        seekBar.visibility = View.VISIBLE
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        mapReady = true

        // move the camera to current position
        val currentLocation = getLocation().getLatLng()
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))

        val adapter = MapPinAdapter(activity!!)
        googleMap.setInfoWindowAdapter(adapter)
        googleMap.setOnInfoWindowClickListener(adapter)
        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                currentLocation,
                getZoomLevel(null)
            )
        )
        if(isLocationGranted(activity!!))
            googleMap.isMyLocationEnabled = true

        showTweetsOnMap()

        addCircle()
    }

    private fun addCircle() {
        val currentLocation = getLocation().getLatLng()

        val circle: Circle = googleMap.addCircle(
            CircleOptions()
                .center(currentLocation)
                .radius((getRadius() * 1000).toDouble()) // Converting KM into Meters...
                .strokeColor(
                    ContextCompat.getColor(
                        activity!!,
                        com.twitter.twitterbelltest.R.color.colorAccent
                    )
                )
                .strokeWidth(5f)
        )

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                circle.radius = p1.toDouble() + Const.DEFAULT_RADIUS * 1000
                googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        currentLocation,
                        getZoomLevel(circle)
                    )
                )
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                circle.isVisible = true
            }

            override fun onStopTrackingTouch(p0: SeekBar) {
                homeViewModel.fetchGeoTweets(getLocation(), (circle.radius / 1000).toInt())
                setRadius((circle.radius / 1000).toInt())
                radius.text = getString(
                    com.twitter.twitterbelltest.R.string.textview_radius,
                    (circle.radius / 1000).toInt()
                )
            }

        })
    }
}