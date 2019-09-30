package com.twitter.twitterbelltest.ui.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.twitter.twitterbelltest.R
import com.twitter.twitterbelltest.databinding.ListitemSearchBinding
import com.twitter.twitterbelltest.model.TweetItem
import com.twitter.twitterbelltest.ui.base.TwitterActionListener
import com.twitter.twitterbelltest.utils.loadUrl
import kotlinx.android.synthetic.main.item_tweet_action.view.*
import kotlinx.android.synthetic.main.view_userinfo.view.*

class SearchAdapter(
    val twitterActionListener: TwitterActionListener,
    val listener: ((TweetItem) -> Unit)? = null
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {
    var tweetList = mutableListOf<TweetItem>()

    fun setList(data: MutableList<TweetItem>) {
        tweetList.clear()
        tweetList.addAll(data)
        notifyDataSetChanged()
    }

    fun updateItem(tweetItem: TweetItem) {
        val item = tweetList.find { tweet -> tweet.tweetId == tweetItem.tweetId }
        tweetList[tweetList.indexOf(item)] = tweetItem
        notifyItemChanged(tweetList.indexOf(item))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {

        val callBinding = DataBindingUtil
            .inflate<ListitemSearchBinding>(
                LayoutInflater.from(parent.context), R.layout.listitem_search,
                parent, false)
        return SearchViewHolder(callBinding)
    }

    override fun getItemCount() = tweetList.count()

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(tweetList[position])
    }

    inner class SearchViewHolder(val binding: ListitemSearchBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(tweetItem: TweetItem) = with(itemView) {
            binding.tweet = tweetItem

            userProfilePicImageView.loadUrl(tweetItem.photo)
            if (tweetItem.tweetFavorited)
                favouriteImageButton.setImageResource(R.drawable.ic_favorite_border_indigo_a700_24dp)
            else
                favouriteImageButton.setImageResource(R.drawable.ic_favorite_border_green_a700_24dp)

            if (tweetItem.tweetRetweeted)
                retweetImageButton.setImageResource(R.drawable.ic_compare_arrows_indigo_a700_24dp)
            else
                retweetImageButton.setImageResource(R.drawable.ic_compare_arrows_green_a700_24dp)

            favouriteImageButton.setOnClickListener { t ->
                if (tweetItem.tweetFavorited)
                    twitterActionListener.unfavorite(tweetItem.tweetId)
                else
                    twitterActionListener.favorite(tweetItem.tweetId)
            }

            retweetImageButton.setOnClickListener { t ->
                if (tweetItem.tweetRetweeted)
                    twitterActionListener.unretweet(tweetItem.tweetId)
                else
                    twitterActionListener.retweet(tweetItem.tweetId)
            }

            binding.executePendingBindings()

            binding.root.setOnClickListener {
                binding.tweet?.let {
                    listener?.invoke(it)
                }
            }
        }
    }
}