package com.twitter.twitterbelltest.location.model


/**
 * <p>LocationLastKnownRequest is use when required  one time last known location from the LocationManager</p>
 *
 */
data class LocationLastKnownRequest(
    val locationRequestSource: LocationRequestSource = LocationRequestSource.ALL
)

/**
 * <p>LocationUpdateRequest is use when required location udpate from the LocationManager</p>
 *
 */
data class LocationUpdateRequest(
    val locationRequestSource: LocationRequestSource = LocationRequestSource.ALL,
    val refreshTimeMilliSec: Long = 60000L,
    val refreshDistanceMeter: Float = 5000F
)