package com.possible.demo.feature.shared

import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

/**
 * Helper method to be used via DataBinding to ease the visibility setting of a View.
 * @param visible when true the view will be [visible][View.VISIBLE] otherwise it's [gone][View.GONE]
 */
@BindingAdapter("visibleOrGone")
fun View.visibleOrGone(visible: Boolean?) {
    visibility = if (visible == true) View.VISIBLE else View.GONE
}

/**
 * Helper method to load an iamge defined via [url] into the [receiver][ImageView].
 *
 * The image will be corped to be circular, that is done using [CircleTransformation]
 */
@BindingAdapter("circleImageFromUrl")
fun ImageView.setCircleImageFromUrl(url: String?) {
    Picasso.with(context)
            .load(url?.let(Uri::parse))
            .transform(CircleTransformation())
            .into(this)
}