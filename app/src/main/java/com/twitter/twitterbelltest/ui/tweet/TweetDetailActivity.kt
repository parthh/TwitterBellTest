package com.twitter.twitterbelltest.ui.tweet

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.twitterbelltest.databinding.ActivityTweetLayoutBinding
import com.twitter.twitterbelltest.model.TweetItem
import com.twitter.twitterbelltest.ui.base.BaseTweetActivity
import com.twitter.twitterbelltest.utils.getTweetItemFromTweet
import com.twitter.twitterbelltest.utils.loadUrl
import kotlinx.android.synthetic.main.activity_tweet_layout.*
import kotlinx.android.synthetic.main.item_tweet_action.*
import kotlinx.android.synthetic.main.video_cover.*
import kotlinx.android.synthetic.main.view_userinfo.*


class TweetDetailActivity : BaseTweetActivity() {


    lateinit var tweetDetailViewModel: TweetDetailViewModel
    private lateinit var activityBinding: ActivityTweetLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = DataBindingUtil.setContentView(this, com.twitter.twitterbelltest.R.layout.activity_tweet_layout)
        tweetDetailViewModel =
            ViewModelProviders.of(this).get(TweetDetailViewModel::class.java)

        tweetDetailViewModel.getTweet().observe(this, Observer {
            it?.let {
                initUi(it)
            }
        })
        val tweetId = intent.getLongExtra("tweetId", 0L)
        if (tweetId != 0L) {
            tweetDetailViewModel.fetchTweetDetails(tweetId)
        }
    }

    fun initUi(tweetItem: TweetItem) {
        activityBinding.tweet = tweetItem
        userProfilePicImageView.loadUrl(tweetItem.photo)
        if (tweetItem.tweetFavorited)
            favouriteImageButton.setImageResource(com.twitter.twitterbelltest.R.drawable.ic_favorite_border_indigo_a700_24dp)
        else
            favouriteImageButton.setImageResource(com.twitter.twitterbelltest.R.drawable.ic_favorite_border_green_a700_24dp)

        if (tweetItem.tweetRetweeted)
            retweetImageButton.setImageResource(com.twitter.twitterbelltest.R.drawable.ic_compare_arrows_indigo_a700_24dp)
        else
            retweetImageButton.setImageResource(com.twitter.twitterbelltest.R.drawable.ic_compare_arrows_green_a700_24dp)

        favouriteImageButton.setOnClickListener { t ->
            if (tweetItem.tweetFavorited)
                unfavorite(tweetItem.tweetId)
            else
                favorite(tweetItem.tweetId)
        }

        retweetImageButton.setOnClickListener { t ->
            if (tweetItem.tweetRetweeted)
                unretweet(tweetItem.tweetId)
            else
                retweet(tweetItem.tweetId)
        }

        if (!tweetItem.tweetPhoto.isNullOrEmpty()) {
            tweetPhoto.loadUrl(tweetItem.tweetPhoto)
            tweetPhoto.setOnClickListener { showImage(tweetItem.tweetPhoto) }
        }

        if (!tweetItem.tweetVideoCoverUrl.isNullOrEmpty()) {
            tweetPhoto.loadUrl(tweetItem.tweetVideoCoverUrl)
            playVideoImageButton.setOnClickListener { view ->
                val pair = tweetItem.tweetVideoUrl
                showVideo(pair.first, pair.second)
            }
        }
        activityBinding.executePendingBindings()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun postInteractionSuccessful(tweetAfter: Tweet) {
        initUi(tweetAfter.getTweetItemFromTweet())
    }
}