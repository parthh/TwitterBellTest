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
import com.twitter.twitterbelltest.utils.Const.CACHE_SEARCH_RESULT
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

    fun initialized(
        query: String = defaultSearchTag,
        location: Location = getLocation(),
        radius: Int = getRadius()
    ) {
        fetchTweetsByQuery(query, location, radius)
    }

    fun fetchTweetsByQuery(query: String, location: Location, radius: Int) {
        val geocode =
            Geocode(
                location.latitude, location.longitude,
                radius, Geocode.Distance.KILOMETERS
            )

        var tweetList: Any? = null
        runBlocking {
            tweetList =
                TwitterTestApp.lruCacheWrapper.get(CACHE_SEARCH_RESULT(query, geocode))
                    .await()
        }
        tweetList?.let {
            postUpdate(query, geocode, tweetList as List<Tweet>)
            Log.d("tweet", "cached search tweet returned")
        } ?: loadTweets(query, geocode)
    }

    @WorkerThread
    private fun loadTweets(query: String, geocode: Geocode) {
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

    /**
     * post fetched tweets to view by updating livedata observer
     * @param query query param that is get searched
     * @param geocode geocode that is being used
     * @param tweetList list of tweets
     */
    private fun postUpdate(query: String, geocode: Geocode, tweetList: List<Tweet>?) {
        val tweetListItems = mutableListOf<TweetItem>()
        tweetList?.let {
            it.forEach { tweet: Tweet -> tweetListItems.add(tweet.getTweetItemFromTweet()) }
        }
        setCacheSearchTweet(query, geocode, tweetList)
        this.tweets.postValue(tweetListItems.toMutableList())
    }

    /**
     * update the lrucache in memory
     * @param query query param that is get searched
     * @param geocode geocode that is being used
     * @param tweetList tweetList after api result
     */
    private fun setCacheSearchTweet(query: String, geocode: Geocode, tweetList: List<Tweet>?) {
        runBlocking {
            TwitterTestApp.lruCacheWrapper.remove(CACHE_SEARCH_RESULT(query, geocode)).await()
            TwitterTestApp.lruCacheWrapper.set(
                CACHE_SEARCH_RESULT(query, geocode),
                tweetList as Any
            ).await()
        }
    }

    companion object {
        private const val defaultSearchTag = "#food"
    }
}