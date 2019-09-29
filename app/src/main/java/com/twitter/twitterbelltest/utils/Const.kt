package com.twitter.twitterbelltest.utils

import com.twitter.sdk.android.core.services.params.Geocode

object Const {
    const val SHARED_PREFERENCE_KEY: String = "TwitterTestSharedPref"
    const val PREF_RADIUS_KEY: String = "radius"
    const val PREF_LATLNG_KEY: String = "latlng"
    const val DEFAULT_RADIUS: Int = 5

    const val DEFAULT_LAT: Double = 45.4943571
    const val DEFAULT_LNG: Double = -73.5802513

    fun CACHE_TWEETS(radius: Int): String = "radius=$radius"
    fun CACHE_SEARCH_RESULT(param:String, geocode: Geocode): String = "query=$param:$geocode"
}