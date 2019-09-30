package com.twitter.twitterbelltest.ui.image

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.twitter.twitterbelltest.R
import com.twitter.twitterbelltest.utils.loadUrl
import kotlinx.android.synthetic.main.activity_image.*

class ImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        var imageUrl  = intent.getStringExtra("imageUrl")
        fullImage.loadUrl(imageUrl)
    }
}