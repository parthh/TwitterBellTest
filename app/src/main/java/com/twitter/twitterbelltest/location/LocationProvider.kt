package com.twitter.twitterbelltest.location


import android.content.Context
import android.content.Intent
import android.location.Location
import com.twitter.twitterbelltest.location.model.LocationLastKnownRequest
import com.twitter.twitterbelltest.location.model.LocationUpdateRequest


/**
 * <p>LocationProvider - interface that provide accessibility to location handler</p>
 *
 */
interface LocationProvider {


    /**
     * get last known location
     *
     */
    fun getLastKnownPosition(context: Context, config: LocationLastKnownRequest, onLocationChange: ((Location) -> Unit)?, onNoLocationFound: (() -> Unit)?)


    /**
     * handle permission request result
     *
     */
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray): Boolean?


    /**
     * start location change tracker
     *
     */
    fun startLocationTracker(context: Context, config: LocationUpdateRequest, onLocationChange: (Location) -> Unit)


    /**
     * stop location change tracker
     *
     */
    fun stopLocationTracker()


    /**
     * handle onActivityResult for location settings resolver
     *
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean?

}