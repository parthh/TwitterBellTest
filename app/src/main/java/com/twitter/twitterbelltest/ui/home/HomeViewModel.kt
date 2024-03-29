package com.twitter.twitterbelltest.ui.home

import android.location.Location
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.models.Search
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.core.services.params.Geocode
import com.twitter.twitterbelltest.TwitterTestApp
import com.twitter.twitterbelltest.utils.Const.CACHE_TWEETS
import com.twitter.twitterbelltest.utils.getLocation
import com.twitter.twitterbelltest.utils.getRadius
import com.twitter.twitterbelltest.utils.startCoroutineTimer
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class HomeViewModel : ViewModel() {
    private val tweets: MutableLiveData<MutableList<Tweet>> = MutableLiveData()

    fun getTweetsObservable(): LiveData<MutableList<Tweet>> = tweets

    fun initialized(location: Location = getLocation(), radius: Int = getRadius()) {
        fetchGeoTweets(location, radius)
    }

    fun fetchGeoTweets(location: Location, radius: Int) {
        var tweetList: Any? = null
        runBlocking {
            tweetList =
                TwitterTestApp.lruCacheWrapper.get(CACHE_TWEETS(radius)).await()
        }
        tweetList?.let {
            postUpdate(radius, tweetList = tweetList as List<Tweet>)
            Log.d("tweet", "cached tweet returned")
        } ?: startCoroutineTimer(timeToInitialMap, timeToRefreshMap, action = {
            loadTweets(location, radius)
        })
    }


    @WorkerThread
    private fun loadTweets(location: Location, radius: Int) {
        Log.d("tweets", "loadTweets")
        val geocode =
            Geocode(
                location.latitude, location.longitude,
                radius, Geocode.Distance.KILOMETERS
            )
        try {
            TwitterCore.getInstance().apiClient.searchService.tweets(
                "#food", geocode, null,
                null, null, 100, null, null, null, true
            ).enqueue(object : Callback<Search> {
                override fun onResponse(call: Call<Search>, response: Response<Search>) {
                    val tweets = response.body()?.tweets ?: Collections.emptyList()
                    postUpdate(radius, tweets)
                }

                override fun onFailure(call: Call<Search>, throwable: Throwable) {
                    postUpdate(radius, null)
                }
            })
        } catch (e: Exception) {
            postUpdate(radius, null)
        }

    }

    /**
     * post fetched tweets to view by updating livedata observer
     * @param radius radius for which this tweets was being fetched
     * @param tweetList list of tweets
     */
    private fun postUpdate(radius: Int, tweetList: List<Tweet>?) {
        setCacheTweet(radius, tweetList)
        this.tweets.postValue(tweetList?.toMutableList())
    }

    /**
     * update the lrucache in memory
     * @param radius radius for which this tweets was being fetched
     * @param tweetList list of tweets
     */
    private fun setCacheTweet(radius: Int, tweetList: List<Tweet>?) {
        runBlocking {
            TwitterTestApp.lruCacheWrapper.remove(CACHE_TWEETS(radius)).await()
            TwitterTestApp.lruCacheWrapper.set(CACHE_TWEETS(radius), tweetList as Any).await()
        }
    }

    companion object {
        private const val timeToRefreshMap: Long = 30000L // 30 sec
        private const val timeToInitialMap: Long = 0L
    }
}