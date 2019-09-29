package com.twitter.twitterbelltest.utils

import android.location.Location
import android.text.format.DateUtils
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.twitter.sdk.android.core.models.Coordinates
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.twitterbelltest.TwitterTestApp
import com.twitter.twitterbelltest.model.TweetItem
import com.twitter.twitterbelltest.utils.Const.DEFAULT_LAT
import com.twitter.twitterbelltest.utils.Const.DEFAULT_LNG
import com.twitter.twitterbelltest.utils.Const.DEFAULT_RADIUS
import com.twitter.twitterbelltest.utils.Const.PREF_LATLNG_KEY
import com.twitter.twitterbelltest.utils.Const.PREF_RADIUS_KEY
import kotlinx.coroutines.runBlocking
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


const val INVALID_DATE: Long = -1
private val DATE_TIME: SimpleDateFormat =
    SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH)


fun setRadius(value: Int = DEFAULT_RADIUS) {
    runBlocking {
        TwitterTestApp.sharedPreferenceCache.withInt().set(PREF_RADIUS_KEY, value = value).await()
    }
}

fun getRadius(): Int {
    return runBlocking {
        TwitterTestApp.sharedPreferenceCache.withInt().get(PREF_RADIUS_KEY).await()
            ?: DEFAULT_RADIUS
    }
}

fun setLocation(location: Location) {
    runBlocking {
        TwitterTestApp.lruCacheWrapper.set(PREF_LATLNG_KEY, value = location).await()
    }
}

fun getLocation(): Location {
    return runBlocking {
        var loc = TwitterTestApp.lruCacheWrapper.get(PREF_LATLNG_KEY).await()
        if (loc != null) {
            loc as Location
        } else {
            val location = Location("")
            location.latitude = DEFAULT_LAT
            location.longitude = DEFAULT_LNG
            loc = location
        }
        loc as Location
    }
}

fun String.getLatLng(): LatLng {
    if (this.isNullOrEmpty()) return LatLng(DEFAULT_LAT, DEFAULT_LNG)
    val latlngArray = this.split(",")
    return LatLng(latlngArray[0].toDouble(), latlngArray[1].toDouble())
}

fun Location.getLatLng(): LatLng {
    return LatLng(this.latitude, this.longitude) ?: LatLng(45.4943571, -73.5802513)
}

fun LatLng.getStringLatLng(): String {
    return "${this.latitude}:${this.longitude}" ?: "45.4943571:-73.5802513"
}

fun Tweet.getTweetLatLng(): LatLng? {
    return (this.coordinates?.let { it2 -> LatLng(it2.latitude, it2.longitude) }
        ?: run {
            this.place?.boundingBox?.coordinates?.let { it2 ->
                if (it2.isNotEmpty() && it2[0].isNotEmpty()) LatLng(
                    it2[0][0][Coordinates.INDEX_LATITUDE],
                    it2[0][0][Coordinates.INDEX_LONGITUDE]
                )
                else null
            } ?: run { null }
        })
}

fun String.timeToLong(): Long {
    if (this == null) return INVALID_DATE

    return try {
        DATE_TIME.parse(this).time
    } catch (e: ParseException) {
        INVALID_DATE
    }

}

fun String.formatTime(): CharSequence? {
    val createdAt = this.timeToLong()
    if (createdAt != INVALID_DATE) {
        return DateUtils.getRelativeTimeSpanString(createdAt)
    }
    return null
}

fun getZoomLevel(circle: Circle?): Float {
    var zoomLevel = 11
    val radiusCircle = circle?.radius ?: (getRadius().toDouble() * 1000)
    val radius = radiusCircle + radiusCircle / 2
    val scale = radius / 500
    zoomLevel = (16 - Math.log(scale) / Math.log(2.0)).toInt()
    return zoomLevel + 0.4F
}

fun Marker.parseTweetPinSnippet(): List<String> = this.snippet.split(",")

fun Tweet.getStringTweetItemModel(): String = TweetItem(
    photo = this.user.profileImageUrlHttps,
    tweetId = this.id, tweetText = this.text, tweetTime = this.createdAt
).toString()