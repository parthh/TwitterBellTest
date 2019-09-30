package com.twitter.twitterbelltest.ui.base

/**
 * Tweet action interface
 */
interface TwitterActionListener {

    fun favorite(tweetId: Long)

    fun unfavorite(tweetId: Long)

    fun retweet(tweetId: Long)

    fun unretweet(tweetId: Long)

    fun openTweet(tweetId: Long, screenName: String)

    fun showImage(imageUrl: String)

    fun showVideo(videoUrl: String, videoType: String)

}