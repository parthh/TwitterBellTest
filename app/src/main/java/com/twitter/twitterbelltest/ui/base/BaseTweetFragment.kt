package com.twitter.twitterbelltest.ui.base

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.twitterbelltest.ui.image.ImageActivity
import com.twitter.twitterbelltest.ui.login.LoginActivity
import com.twitter.twitterbelltest.ui.video.VideoActivity
import retrofit2.Call

abstract class BaseTweetFragment : Fragment(), TwitterActionListener {
    /**
     * Called after success any of these operation favorite, retweet, destroy(retweet, favorite)
     */
    abstract fun postInteractionSuccessful( tweetAfter: Tweet)

    private fun enqueue(call: Call<Tweet>) {
        call.enqueue(object : Callback<Tweet>() {
            override fun success(result: Result<Tweet>?) {
                result?.let {
                    postInteractionSuccessful(result.data)
                }

            }

            override fun failure(exception: TwitterException?) {
                Snackbar.make(
                    activity?.window?.decorView!!,
                    "Action failed, we don't know why", Snackbar.LENGTH_SHORT
                ).show()
            }

        })
    }

    override fun favorite(tweetId: Long) {
        if (TwitterCore.getInstance().sessionManager.activeSession == null)
            startActivity(Intent(activity!!, LoginActivity::class.java))
        else {
            val call = TwitterCore.getInstance().apiClient.favoriteService.create(tweetId, null)
            enqueue(call)
        }
    }

    override fun retweet(tweetId: Long) {
        if (TwitterCore.getInstance().sessionManager.activeSession == null)
            startActivity(Intent(activity!!, LoginActivity::class.java))
        else {
            val call = TwitterCore.getInstance().apiClient.statusesService.retweet(tweetId, null)
            enqueue(call)
        }

    }

    override fun unfavorite(tweetId: Long) {
        if (TwitterCore.getInstance().sessionManager.activeSession == null)
            startActivity(Intent(activity!!, LoginActivity::class.java))
        else {
            val call = TwitterCore.getInstance().apiClient.favoriteService.destroy(tweetId, null)
            enqueue(call)
        }
    }

    override fun unretweet(tweetId: Long) {
        if (TwitterCore.getInstance().sessionManager.activeSession == null)
            startActivity(Intent(activity!!, LoginActivity::class.java))
        else {
            val call = TwitterCore.getInstance().apiClient.statusesService.unretweet(tweetId, null)
            enqueue(call)
        }
    }

    override fun openTweet(tweetId: Long, screenName: String) {
        Toast.makeText(activity!!, "open tweet", Toast.LENGTH_SHORT).show()
        startActivity(
            Intent.createChooser(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://twitter.com/$screenName/status/$tweetId")
                ), "tweet"
            )
        )
    }

    override fun showImage(imageUrl: String) {
        val intent =  Intent(activity!!, ImageActivity::class.java)
        intent.putExtra(ImageActivity.TAG_URL, imageUrl)
        startActivity(intent)
    }

    override fun showVideo(videoUrl: String, videoType: String) {
        val intent =  Intent(activity!!, VideoActivity::class.java)
        intent.putExtra(VideoActivity.TAG_URL, videoUrl)
        intent.putExtra(VideoActivity.TAG_TYPE, videoType)
        startActivity(intent)
    }
}