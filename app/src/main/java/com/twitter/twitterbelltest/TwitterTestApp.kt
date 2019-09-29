package com.twitter.twitterbelltest

import android.app.Application
import android.util.Log
import com.twitter.sdk.android.core.DefaultLogger
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.TwitterConfig

class TwitterTestApp: Application() {
    override fun onCreate() {
        super.onCreate()
        initTwitter()
    }

    private fun initTwitter() {
        val config = TwitterConfig.Builder(this)
            .logger(DefaultLogger(Log.DEBUG))//enable logging when app is in debug mode
            .twitterAuthConfig(
                TwitterAuthConfig(
                    BuildConfig.TWITTER_CONSUMER_API_KEY,
                    BuildConfig.TWITTER_CONSUMER_API_SECRET
                )
            )
            .debug(true)//enable debug mode
            .build()

        //initialize twitter
        Twitter.initialize(config)
    }
}
