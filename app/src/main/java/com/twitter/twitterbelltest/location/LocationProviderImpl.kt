package com.twitter.twitterbelltest.location


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.util.Log
import androidx.core.app.ActivityCompat.startActivityForResult
import com.twitter.twitterbelltest.MainActivity
import com.twitter.twitterbelltest.location.model.LocationLastKnownRequest
import com.twitter.twitterbelltest.location.model.LocationRequestSource
import com.twitter.twitterbelltest.location.model.LocationUpdateRequest
import com.twitter.twitterbelltest.permission.PermissionProvider
import com.twitter.twitterbelltest.permission.PermissionProviderImpl


/**
 * <p>LocationProviderImpl is based on LocationProvider and contains all logic for handling location changes and last known location</p>
 */
class LocationProviderImpl : LocationProvider {


    private val REQUEST_PERMISSION_FINE_LOCATION_FOR_LAST_POSITION = 123
    private val REQUEST_PERMISSION_FINE_LOCATION_FOR_LOCATION_TRACKER = 345
    private val REQUEST_ENABLE_GPS = 434
    private var locationManager: LocationManager? = null
    private var onNoLocationFound: (() -> Unit)? = null
    private var onLocationChange: ((Location) -> Unit)? = null
    private val permissionProvider: PermissionProvider = PermissionProviderImpl
    private var context: Context?=null
    /**
     * get last known location
     *
     */
    @SuppressLint("MissingPermission")
    override fun getLastKnownPosition(
        context: Context,
        config: LocationLastKnownRequest,
        onLocationChange: ((Location) -> Unit)?,
        onNoLocationFound: (() -> Unit)?
    ) {
        this.context =  context
        if (locationManager == null) {
            locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }

        this.onLocationChange = onLocationChange
        this.onNoLocationFound = onNoLocationFound

        permissionProvider.checkPermissions(
            activity = context as MainActivity,
            permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            onPermissionResult = { permissionResult ->
                if (permissionResult.areAllGranted()) {
                    if(isGpsIsEnabled(context)) {
                        getLastLocation(config.locationRequestSource)
                    }
                }
            },
            requestCode = REQUEST_PERMISSION_FINE_LOCATION_FOR_LAST_POSITION
        )
    }


    /**
     * handle permission request result
     *
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean? {
        return permissionProvider.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    /**
     * start location change tracker
     *
     */
    override fun startLocationTracker(
        context: Context,
        config: LocationUpdateRequest,
        onLocationChange: (Location) -> Unit
    ) {
        this.context =  context
        this.onLocationChange = onLocationChange

        if (locationManager == null) {
            locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }

        permissionProvider.checkPermissions(
            activity = context as MainActivity,
            permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            onPermissionResult = { permissionResult ->
                if (permissionResult.areAllGranted() && isGpsIsEnabled(context)) {
//                    if(isGpsIsEnabled(context)) {
                        requestLocationUpdates(config)
//                    }
                }
            },
            requestCode = REQUEST_PERMISSION_FINE_LOCATION_FOR_LOCATION_TRACKER
        )
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location?) {
            location ?: return
            onLocationChange?.invoke(location)
        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
            Log.d(TAG, "Gps status change$p1")
        }

        override fun onProviderEnabled(p0: String?) {
            Log.d(TAG, "Gps provider enables $p0")
        }

        override fun onProviderDisabled(p0: String?) {
            isGpsIsEnabled(context!!)
        }
    }


    /**
     * stop location change tracker
     *
     */
    override fun stopLocationTracker() {
        locationManager?.removeUpdates(locationListener)
    }


    /**
     * execute last location request
     *
     */
    @SuppressLint("MissingPermission")
    private fun getLastLocation(locationRequestSource: LocationRequestSource) {
        when (locationRequestSource) {
            LocationRequestSource.ALL
            -> {
                val lastLocationGps =
                    locationManager?.getLastKnownLocation(LocationRequestSource.GPS_PROVIDER.locationSourceType)
                val lastLocationNetwork =
                    locationManager?.getLastKnownLocation(LocationRequestSource.NETWORK_PROVIDER.locationSourceType)
                val lastLocationPassive =
                    locationManager?.getLastKnownLocation(LocationRequestSource.PASSIVE_PROVIDER.locationSourceType)
                if (lastLocationGps != null) onLocationChange?.invoke(lastLocationGps) else onNoLocationFound?.invoke()
                if (lastLocationNetwork != null) onLocationChange?.invoke(lastLocationNetwork) else onNoLocationFound?.invoke()
                if (lastLocationPassive != null) onLocationChange?.invoke(lastLocationPassive) else onNoLocationFound?.invoke()
            }
            LocationRequestSource.GPS_PROVIDER
            -> {
                val lastLocationGps = locationManager?.getLastKnownLocation(
                    LocationRequestSource.GPS_PROVIDER.locationSourceType
                )
                if (lastLocationGps != null) onLocationChange?.invoke(lastLocationGps) else onNoLocationFound?.invoke()
            }
            LocationRequestSource.NETWORK_PROVIDER
            -> {
                val lastLocationNetwork =
                    locationManager?.getLastKnownLocation(LocationRequestSource.GPS_PROVIDER.locationSourceType)
                if (lastLocationNetwork != null) onLocationChange?.invoke(lastLocationNetwork) else onNoLocationFound?.invoke()
            }
            LocationRequestSource.PASSIVE_PROVIDER
            -> {
                val lastLocationPassive =
                    locationManager?.getLastKnownLocation(LocationRequestSource.GPS_PROVIDER.locationSourceType)
                if (lastLocationPassive != null) onLocationChange?.invoke(lastLocationPassive) else onNoLocationFound?.invoke()
            }
        }
    }

    private fun isGpsIsEnabled(context: Context): Boolean {
        val isGpsEnabled = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (isGpsEnabled == false) {
            val intent = Intent(ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(context as MainActivity, intent, REQUEST_ENABLE_GPS, null)
            return false
        }
        return true
    }

    /**
     * execute location change updates
     *
     */
    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates(locationUpdateRequest: LocationUpdateRequest) {

        when (locationUpdateRequest.locationRequestSource) {
            LocationRequestSource.ALL
            -> {
                locationManager?.requestLocationUpdates(
                    LocationRequestSource.GPS_PROVIDER.locationSourceType,
                    locationUpdateRequest.refreshTimeMilliSec,
                    locationUpdateRequest.refreshDistanceMeter,
                    locationListener
                )
                locationManager?.requestLocationUpdates(
                    LocationRequestSource.NETWORK_PROVIDER.locationSourceType,
                    locationUpdateRequest.refreshTimeMilliSec,
                    locationUpdateRequest.refreshDistanceMeter,
                    locationListener
                )
                locationManager?.requestLocationUpdates(
                    LocationRequestSource.PASSIVE_PROVIDER.locationSourceType,
                    locationUpdateRequest.refreshTimeMilliSec,
                    locationUpdateRequest.refreshDistanceMeter,
                    locationListener
                )
            }
            LocationRequestSource.GPS_PROVIDER
            -> {
                locationManager?.requestLocationUpdates(
                    LocationRequestSource.GPS_PROVIDER.locationSourceType,
                    locationUpdateRequest.refreshTimeMilliSec,
                    locationUpdateRequest.refreshDistanceMeter,
                    locationListener
                )

            }
            LocationRequestSource.NETWORK_PROVIDER
            -> {
                locationManager?.requestLocationUpdates(
                    LocationRequestSource.NETWORK_PROVIDER.locationSourceType,
                    locationUpdateRequest.refreshTimeMilliSec,
                    locationUpdateRequest.refreshDistanceMeter,
                    locationListener
                )

            }
            LocationRequestSource.PASSIVE_PROVIDER
            -> {
                locationManager?.requestLocationUpdates(
                    LocationRequestSource.PASSIVE_PROVIDER.locationSourceType,
                    locationUpdateRequest.refreshTimeMilliSec,
                    locationUpdateRequest.refreshDistanceMeter,
                    locationListener
                )

            }
        }
    }


    /**
     * handle onActivityResult for location settings resolver
     *
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean? {
        if (requestCode == REQUEST_ENABLE_GPS) {
            requestLocationUpdates(LocationUpdateRequest())
            return true
        }
        return false
    }

    companion object {
        var TAG = this.javaClass.simpleName
    }
}