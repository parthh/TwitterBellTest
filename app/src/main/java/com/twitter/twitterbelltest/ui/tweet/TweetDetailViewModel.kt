package com.twitter.twitterbelltest.ui.tweet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.twitterbelltest.model.TweetItem
import com.twitter.twitterbelltest.utils.getTweetItemFromTweet
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TweetDetailViewModel : ViewModel() {
    private val tweet: MutableLiveData<TweetItem> = MutableLiveData()
    fun getTweet(): LiveData<TweetItem> = tweet

    fun fetchTweetDetails(tweetId: Long) {
        try {
            TwitterCore.getInstance().apiClient.statusesService.show(tweetId, null, null, null
            ).enqueue(object : Callback<Tweet> {
                override fun onResponse(call: Call<Tweet>?, response: Response<Tweet>?) {
                    if(response?.isSuccessful == true){
                        postData(response.body()?.getTweetItemFromTweet())
                    }
                }

                override fun onFailure(call: Call<Tweet>?, t: Throwable?) {
                    postData(null)
                }
            })
        } catch (e: Exception) {

        }
    }

    fun postData(tweet:TweetItem?){
        this.tweet.value = tweet
    }
}