package com.twitter.twitterbelltest.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.twitterbelltest.model.TweetItem
import com.twitter.twitterbelltest.ui.base.BaseTweetFragment
import com.twitter.twitterbelltest.ui.search.adapter.SearchAdapter
import com.twitter.twitterbelltest.ui.tweet.TweetDetailActivity
import com.twitter.twitterbelltest.ui.tweet.TweetDetailActivity.Companion.TWEET_ID
import com.twitter.twitterbelltest.utils.getLocation
import com.twitter.twitterbelltest.utils.getRadius
import com.twitter.twitterbelltest.utils.getTweetItemFromTweet
import kotlinx.android.synthetic.main.fragment_search.*


class SearchFragment : BaseTweetFragment() {
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var adapter: SearchAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        searchViewModel =
            ViewModelProviders.of(this).get(SearchViewModel::class.java)
        val root = inflater.inflate(com.twitter.twitterbelltest.R.layout.fragment_search, container, false)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val layoutManager = LinearLayoutManager(activity!!)
        tweetsRecyclerView.layoutManager = layoutManager
        adapter = SearchAdapter( this) {
            startTwitterDetailActivity(it.tweetId)
        }
        tweetsRecyclerView.adapter = adapter
        tweetsRecyclerView.setHasFixedSize(true)


        searchTweets.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query.isNullOrEmpty()){
                    searchViewModel.initialized()
                } else {
                    searchViewModel.fetchTweetsByQuery(
                        query = query,
                        location = getLocation(),
                        radius = getRadius()
                    )
                    activity?.actionBar?.title = query
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

        searchViewModel.getTweetsObservable().observe(this, Observer {
            it?.let {
                if (it == null || it.isEmpty()) {
                    noResultFound.visibility = View.VISIBLE
                } else {
                    noResultFound.visibility = View.GONE
                    fillTweets(it)
                }
            }
        })
        searchViewModel.initialized()

    }

    fun fillTweets(tweetItems: List<TweetItem>) {
        adapter.setList(tweetItems.toMutableList())
        adapter.notifyDataSetChanged()
    }


    fun startTwitterDetailActivity(id: Long) {
        val intent = Intent(context, TweetDetailActivity::class.java)
        intent.putExtra(TWEET_ID, id)
        context?.startActivity(intent)
    }

    override fun postInteractionSuccessful(tweetAfter: Tweet) {
        adapter.updateItem(tweetAfter.getTweetItemFromTweet())
    }
}