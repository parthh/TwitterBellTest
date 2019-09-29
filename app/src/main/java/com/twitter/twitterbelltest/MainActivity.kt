package com.twitter.twitterbelltest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.twitter.twitterbelltest.location.LocationProvider
import com.twitter.twitterbelltest.location.LocationProviderImpl
import com.twitter.twitterbelltest.location.model.LocationLastKnownRequest
import com.twitter.twitterbelltest.utils.setLocation
import com.twitter.twitterbelltest.utils.setRadius

class MainActivity : AppCompatActivity() {

    val locationProvider: LocationProvider = LocationProviderImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startLocationRequest()
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_search
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    fun startLocationRequest() {
        locationProvider.getLastKnownPosition(context = this@MainActivity,
            config = LocationLastKnownRequest(),
            onLocationChange = {
                Log.d(
                    "Location",
                    "Last Location - found - lat: " + it.latitude + " lng:" + it.longitude
                )
                setLocation(location = it)
            },
            onNoLocationFound = {
                Log.d("Location", "Last Location - no location found")
            })
    }

    /**
     * handle permission request result
     *
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        locationProvider.onRequestPermissionsResult(requestCode, permissions, grantResults)
            ?: super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**
     * handle activity result in location provider
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        locationProvider.onActivityResult(requestCode, resultCode, data) ?: super.onActivityResult(
            requestCode,
            resultCode,
            data
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        setRadius()
    }
}
