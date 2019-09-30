package com.twitter.twitterbelltest.ui.video

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.twitter.twitterbelltest.R
import kotlinx.android.synthetic.main.activity_video.*

class VideoActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)


        val mc = MediaController(this)
        mc.setAnchorView(videoView)
        mc.setMediaPlayer(videoView)

        videoView.setOnPreparedListener { loadingProgressBar.visibility = View.GONE }
        if ("animated_gif" == intent.getStringExtra(TAG_TYPE))
            videoView.setOnCompletionListener { videoView.start() }

        videoView.setMediaController(mc)
        videoView.setVideoURI(Uri.parse(intent.getStringExtra(TAG_URL)))
        videoView.requestFocus()
        videoView.start()
    }

    companion object {
        const val TAG_URL = "video"
        const val TAG_TYPE = "videoType"
    }
}