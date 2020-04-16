package com.giphy.browser.gif_detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.MenuItem
import androidx.annotation.ColorRes
import androidx.databinding.DataBindingUtil
import com.facebook.drawee.backends.pipeline.Fresco
import com.giphy.browser.R
import com.giphy.browser.common.BaseActivity
import com.giphy.browser.databinding.ActivityGifDetailBinding
import kotlinx.android.parcel.Parcelize
import java.util.*

class GifDetailActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityGifDetailBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_gif_detail)

        val args = intent.getParcelableExtra(ARGS) as Args

        setTitle(R.string.details)
        Objects.requireNonNull(supportActionBar)!!.setDisplayHomeAsUpEnabled(true)

        binding.gif.aspectRatio = args.aspectRatio
        binding.gif.hierarchy.setPlaceholderImage(args.backgroundColor)
        binding.gif.controller = Fresco.newDraweeControllerBuilder()
            .setAutoPlayAnimations(true)
            .setOldController(binding.gif.controller)
            .setUri(args.webp)
            .build()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @Parcelize
    data class Args(
        val webp: String,
        val aspectRatio: Float,
        @ColorRes val backgroundColor: Int
    ) : Parcelable

    companion object {
        private const val ARGS = "args"
        fun newIntent(context: Context, args: Args): Intent {
            return Intent(context, GifDetailActivity::class.java)
                .putExtra(ARGS, args)
        }
    }
}
