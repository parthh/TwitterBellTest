package com.twitter.twitterbelltest

import android.app.Application
import android.util.Log
import com.twitter.sdk.android.core.DefaultLogger
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.TwitterConfig
import com.twitter.twitterbelltest.cache.Cache
import com.twitter.twitterbelltest.cache.SharedPreferencesCache
import com.twitter.twitterbelltest.cache.createLruCache
import com.twitter.twitterbelltest.utils.Const

class TwitterTestApp: Application() {

    override fun onCreate() {
        super.onCreate()
        initTwitter()
        sharedPreferenceCache = SharedPreferencesCache(this,Const.SHARED_PREFERENCE_KEY)
        lruCacheWrapper  = Cache.createLruCache(3)
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

    companion object{
        lateinit var  sharedPreferenceCache: SharedPreferencesCache
        lateinit var lruCacheWrapper: Cache<Any, Any>
    }

}
