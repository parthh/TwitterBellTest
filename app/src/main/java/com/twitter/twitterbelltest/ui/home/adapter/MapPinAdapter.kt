package com.twitter.twitterbelltest.ui.home.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.squareup.picasso.Picasso
import com.twitter.twitterbelltest.R
import com.twitter.twitterbelltest.ui.tweet.TweetDetailActivity
import com.twitter.twitterbelltest.ui.tweet.TweetDetailActivity.Companion.TWEET_ID
import com.twitter.twitterbelltest.utils.formatTime
import com.twitter.twitterbelltest.utils.parseTweetPinSnippet

/**
 * MapPinAdapter class is adapter for markers on map
 * @param context Context of application
 */
class MapPinAdapter(private val context: Context) : GoogleMap.InfoWindowAdapter,
    GoogleMap.OnInfoWindowClickListener {
    companion object {
        const val INDEX_PHOTO_URL = 0
        const val INDEX_TIME = 1
        const val INDEX_TWEET_TEXT = 2
        const val INDEX_TWEET_ID = 3
    }

    override fun onInfoWindowClick(p0: Marker) {
        val text = p0.parseTweetPinSnippet()
        val id = text[INDEX_TWEET_ID].toLong()
        startTwitterDetailActivity(id)
    }

    override fun getInfoContents(marker: Marker): View {
        val text = marker.parseTweetPinSnippet()
        val photoUrl = text[INDEX_PHOTO_URL]
        val timestamp = text[INDEX_TIME]
        val tweet = text[INDEX_TWEET_TEXT]

        val v = LayoutInflater.from(context).inflate(R.layout.tweet_marker_view, null)
        Picasso.get()
            .load(photoUrl)
            .into(v.findViewById<ImageView>(R.id.avatar))

        v.findViewById<TextView>(R.id.timestamp).text = timestamp.formatTime()
        v.findViewById<TextView>(R.id.name).text = marker.title
        v.findViewById<TextView>(R.id.tweet).text = tweet

        return v
    }

    override fun getInfoWindow(p0: Marker?) = null

    fun startTwitterDetailActivity(id: Long) {
        val intent = Intent(context, TweetDetailActivity::class.java)
        intent.putExtra(TWEET_ID, id)
        context.startActivity(intent)
    }
}