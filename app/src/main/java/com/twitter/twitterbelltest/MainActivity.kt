package com.twitter.twitterbelltest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.twitter.twitterbelltest.location.LocationProvider
import com.twitter.twitterbelltest.location.LocationProviderImpl
import com.twitter.twitterbelltest.location.model.LocationUpdateRequest
import com.twitter.twitterbelltest.utils.setLocation
import com.twitter.twitterbelltest.utils.setRadius
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val locationProvider: LocationProvider = LocationProviderImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = findNavController(R.id.nav_host_fragment)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_search
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        nav_view.setupWithNavController(navController)
        nav_view.setOnNavigationItemReselectedListener { menuItem ->
            Toast.makeText(this,"Reselected",Toast.LENGTH_SHORT).show()
        }
        startLocationRequest()
    }

    fun startLocationRequest() {
        locationProvider.startLocationTracker(
            context = this@MainActivity,
            config = LocationUpdateRequest(),
            onLocationChange = {
                Log.d(
                    "Location",
                    "New Location - found - lat: " + it.latitude + " lng:" + it.longitude
                )
                setLocation(location = it)
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
