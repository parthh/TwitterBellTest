package com.twitter.twitterbelltest.ui.search

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
import com.twitter.twitterbelltest.model.TweetItem
import com.twitter.twitterbelltest.utils.Const
import com.twitter.twitterbelltest.utils.getLocation
import com.twitter.twitterbelltest.utils.getRadius
import com.twitter.twitterbelltest.utils.getTweetItemFromTweet
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class SearchViewModel : ViewModel() {

    private val tweets: MutableLiveData<List<TweetItem>?> = MutableLiveData()

    fun getTweetsObservable(): LiveData<List<TweetItem>?> = tweets

    fun initialized(query: String="#food", location: Location = getLocation(), radius: Int = getRadius()) {
        fetchTweetsByQuery(query, location, radius)
    }

    fun fetchTweetsByQuery(query: String, location: Location, radius: Int) {
        // load tweets if location is null take montreal
        val geocode =
            Geocode(
                location.latitude, location.longitude,
                radius, Geocode.Distance.KILOMETERS
            )

        var tweetList: Any? = null
        runBlocking {
            tweetList =
                TwitterTestApp.lruCacheWrapper.get(Const.CACHE_SEARCH_RESULT(query, geocode))
                    .await()
        }
        if (tweetList != null) {
            postUpdate(query, geocode, tweetList as List<Tweet>)
        } else {
            loadTweets(query, location, radius, geocode)
        }
    }

    @WorkerThread
    private fun loadTweets(query: String, location: Location, radius: Int, geocode: Geocode) {
        Log.d("tweets", "loadTweets")
        // load tweets if location is null take montreal
        val geocode =
            Geocode(
                location.latitude, location.longitude,
                radius, Geocode.Distance.KILOMETERS
            )
        try {
            TwitterCore.getInstance().apiClient.searchService.tweets(
                query, geocode, null,
                null, null, 100, null, null, null, true
            ).enqueue(object : Callback<Search> {
                override fun onResponse(call: Call<Search>, response: Response<Search>) {
                    val tweets = response.body()?.tweets ?: Collections.emptyList()
                    postUpdate(query, geocode, tweets)
                }

                override fun onFailure(call: Call<Search>, throwable: Throwable) {
                    postUpdate(query, geocode, null)
                }
            })
        } catch (e: Exception) {
            postUpdate(query, geocode, null)
        }

    }

    private fun postUpdate(query: String, geocode: Geocode, tweetList: List<Tweet>?) {
        val tweetListItems = mutableListOf<TweetItem>()
        tweetList?.let {
            it.forEach { tweet: Tweet -> tweetListItems.add(tweet.getTweetItemFromTweet()) }
        }
        setCacheSearchTweet(query, geocode, tweetListItems)
        this.tweets.postValue(tweetListItems.toMutableList())
    }

    fun setCacheSearchTweet(query: String, geocode: Geocode, tweetList: List<TweetItem>?) {
        runBlocking {
            TwitterTestApp.lruCacheWrapper.remove(Const.CACHE_SEARCH_RESULT(query, geocode)).await()
            TwitterTestApp.lruCacheWrapper.set(
                Const.CACHE_SEARCH_RESULT(query, geocode),
                tweetList as Any
            ).await()
        }
    }
}