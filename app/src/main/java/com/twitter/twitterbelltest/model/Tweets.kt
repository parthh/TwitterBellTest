package com.twitter.twitterbelltest.model

import com.twitter.twitterbelltest.utils.formatTime

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
    val tweetVideoUrl: Pair<String, String> = Pair("","")
) {
    override fun toString(): String {
        return "$photo^$tweetTime^$tweetText^$tweetId"
    }

    val formattedTime: String
        get() = tweetTime.formatTime().toString()
}

