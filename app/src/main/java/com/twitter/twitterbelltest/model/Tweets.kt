package com.twitter.twitterbelltest.model

import com.twitter.twitterbelltest.utils.formatTime

/**
 * TweetItem model based from Tweet object from tweeter SDK
 */
open class TweetItem(
    val photo: String? = null,
    val tweetTime: String,
    val tweetText: String,
    val tweetId: Long,
    val tweetFavoriteCount: Int = 0,
    val tweetFavorited: Boolean = false,
    val reTweetCount: Int = 0,
    val tweetRetweeted: Boolean = false,
    val userName: String = "",
    val userScreenName: String = "",
    val tweetPhoto: String = "",
    val tweetVideoCoverUrl: String = "",
    val tweetVideoUrl: Pair<String, String> = Pair("", "")
) {
    /**
     * provideMapMarkerSnippet function provide the encoded snippet for marker on the map
     */
    val provideMapMarkerSnippet: String
        get() = "$photo^$tweetTime^$tweetText^$tweetId"

    val formattedTime: String
        get() = tweetTime.formatTime().toString()
}

