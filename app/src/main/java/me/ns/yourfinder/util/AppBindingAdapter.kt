package me.ns.yourfinder.util

import android.databinding.BindingAdapter
import android.net.Uri
import android.widget.ImageView
import com.squareup.picasso.Picasso
import me.ns.yourfinder.R
import java.io.File

/**
 * AppBindingAdapter
 *
 * Created by shintaro.nosaka on 2017/09/13.
 */
object AppBindingAdapter {

    private val IMAGE_SIZE_SMALL: Int = 120

    private val IMAGE_SIZE_MIDDLE: Int = (IMAGE_SIZE_SMALL * 1.5).toInt()

    private val IMAGE_SIZE_LARGE: Int = (IMAGE_SIZE_SMALL * 2).toInt()

    @BindingAdapter("bind:imageUrl")
    @JvmStatic
    fun loadImage(view: ImageView, url: Uri?) {
        url?.let {
            Picasso.with(view.context)
                    .load(it)
                    .placeholder(R.drawable.drawable_round_placeholder)
                    .error(R.drawable.ic_unknown)
                    .into(view)
        }
    }

    @BindingAdapter("bind:imageUrl")
    @JvmStatic
    fun loadImage(view: ImageView, url: String?) {
        url?.let {
            Picasso.with(view.context)
                    .load(File(it))
                    .placeholder(R.drawable.drawable_round_placeholder)
                    .error(R.drawable.ic_unknown)
                    .into(view)
        }
    }

    @BindingAdapter("bind:smallImageUrl")
    @JvmStatic
    fun loadSmallImage(view: ImageView, url: Uri?) {
        url?.let {
            Picasso.with(view.context)
                    .load(it)
                    .placeholder(R.drawable.drawable_round_placeholder)
                    .error(R.drawable.ic_unknown)
                    .resize(IMAGE_SIZE_SMALL, IMAGE_SIZE_SMALL)
                    .onlyScaleDown()
                    .centerCrop()
                    .into(view)
        }
    }

    @BindingAdapter("bind:smallImageUrl")
    @JvmStatic
    fun loadSmallImage(view: ImageView, url: String?) {
        url?.let {
            Picasso.with(view.context)
                    .load(File(it))
                    .placeholder(R.drawable.drawable_round_placeholder)
                    .error(R.drawable.ic_unknown)
                    .resize(IMAGE_SIZE_SMALL, IMAGE_SIZE_SMALL)
                    .onlyScaleDown()
                    .centerCrop()
                    .into(view)
        }
    }

    @BindingAdapter("bind:middleImageUrl")
    @JvmStatic
    fun loadMiddleImage(view: ImageView, url: Uri?) {
        url?.let {
            Picasso.with(view.context)
                    .load(it)
                    .placeholder(R.drawable.drawable_round_placeholder)
                    .error(R.drawable.ic_unknown)
                    .resize(IMAGE_SIZE_MIDDLE, IMAGE_SIZE_MIDDLE)
                    .onlyScaleDown()
                    .centerCrop()
                    .into(view)
        }
    }

    @BindingAdapter("bind:middleImageUrl")
    @JvmStatic
    fun loadMiddleImage(view: ImageView, url: String?) {
        url?.let {
            Picasso.with(view.context)
                    .load(File(it))
                    .placeholder(R.drawable.drawable_round_placeholder)
                    .error(R.drawable.ic_unknown)
                    .resize(IMAGE_SIZE_MIDDLE, IMAGE_SIZE_MIDDLE)
                    .onlyScaleDown()
                    .centerCrop()
                    .into(view)
        }
    }

    @BindingAdapter("bind:largeImageUrl")
    @JvmStatic
    fun loadLargeImage(view: ImageView, url: Uri?) {
        url?.let {
            Picasso.with(view.context)
                    .load(it)
                    .placeholder(R.drawable.drawable_round_placeholder)
                    .error(R.drawable.ic_unknown)
                    .resize(IMAGE_SIZE_LARGE, IMAGE_SIZE_LARGE)
                    .onlyScaleDown()
                    .centerCrop()
                    .into(view)
        }
    }

    @BindingAdapter("bind:largeImageUrl")
    @JvmStatic
    fun loadLargeImage(view: ImageView, url: String?) {
        url?.let {
            Picasso.with(view.context)
                    .load(File(it))
                    .placeholder(R.drawable.drawable_round_placeholder)
                    .error(R.drawable.ic_unknown)
                    .resize(IMAGE_SIZE_LARGE, IMAGE_SIZE_LARGE)
                    .onlyScaleDown()
                    .centerCrop()
                    .into(view)
        }
    }
}