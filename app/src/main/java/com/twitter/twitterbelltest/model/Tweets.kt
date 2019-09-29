package com.twitter.twitterbelltest.model

open class TweetItem(
    val photo: String? = null,
    val tweetTime: String,
    val tweetText: String,
    val tweetId: Long,
    val tweetFavoriteCount: Int = 0,
    val tweetFavorited: Boolean = false,
    val reTweetCount: Int = 0,
    val tweetRetweeted: Boolean =  false
) {
    override fun toString(): String {
        return "$photo,$tweetTime,$tweetText,$tweetId,$tweetFavoriteCount,$tweetFavorited,$reTweetCount,$tweetRetweeted"
    }
}

