package com.example.glidesample

import android.app.Activity
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val roundedCorners = toDp(10f).toInt()
        Timber.d("Corner Size=$roundedCorners")

        // 1,214px x 2,475px 이미지 로드
        iv1.loadDefault(R.drawable.sample_over, roundedCorners, sub1) // Loaded Drawable=275x275
        iv2.loadCustomTarget(
            R.drawable.sample_over,
            roundedCorners,
            sub2
        ) // Loaded Drawable=1214x2475
        iv3.loadImageViewTarget(
            R.drawable.sample_over,
            roundedCorners,
            sub3
        ) // Loaded Drawable=275x275

        // 80px 이미지 로드
        iv4.loadDefault(R.drawable.sample_samll, roundedCorners, sub4) // Loaded Drawable=275x275
        iv5.loadCustomTarget(R.drawable.sample_samll, roundedCorners, sub5) // Loaded Drawable=80x80
        iv6.loadImageViewTarget(
            R.drawable.sample_samll,
            roundedCorners,
            sub6
        ) // Loaded Drawable=275x275

        iv5.doOnLayout {
            Timber.d("Rect Size=${it.width}x${it.height}")
        }
    }
}

///////////////////////////////////////////////////////////////////////////
// Glide Util Method
///////////////////////////////////////////////////////////////////////////

private fun RequestBuilder<Drawable>.applyDefault(
    cornerRadius: Int,
    subView: TextView
) = apply {
    transform(CenterCrop(), RoundedCorners(cornerRadius))
    listener(object : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
        ): Boolean {
            Timber.e(e)
            return false
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            resource?.let {
                Timber.d("Loaded Drawable=${resource.intrinsicWidth}x${resource.intrinsicHeight}")
                subView.text = "Loaded=${resource.intrinsicWidth}x${resource.intrinsicHeight}"
            }
            return false
        }
    })
}

private fun ImageView.loadDefault(@DrawableRes resId: Int, corner: Int, subView: TextView) {
    Glide.with(context)
        .load(resId)
        .applyDefault(corner, subView)
        .into(this)
}

private fun ImageView.loadCustomTarget(@DrawableRes resId: Int, corner: Int, subView: TextView) {
    Glide.with(context)
        .load(resId)
        .applyDefault(corner, subView)
        .into(object : CustomTarget<Drawable>() {
            override fun onResourceReady(
                resource: Drawable,
                transition: Transition<in Drawable>?
            ) {
                this@loadCustomTarget.setImageDrawable(resource)
            }

            override fun onLoadCleared(placeholder: Drawable?) {}
        })
}

private fun ImageView.loadImageViewTarget(@DrawableRes resId: Int, corner: Int, subView: TextView) {
    Glide.with(context)
        .load(resId)
        .applyDefault(corner, subView)
        .into(DrawableImageViewTarget(this))
}

///////////////////////////////////////////////////////////////////////////
// DP Convert
///////////////////////////////////////////////////////////////////////////

fun Int.toDp(displayMetrics: DisplayMetrics) = toFloat().toDp(displayMetrics).toInt()

fun Float.toDp(displayMetrics: DisplayMetrics) =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, displayMetrics)

fun <T> T.toDp(value: Float) where T : Activity = value.toDp(resources.displayMetrics)

fun <T> T.dp(value: Int) where T : Fragment = value.toDp(resources.displayMetrics)

fun <T> T.dp(value: Int) where T : View = value.toDp(resources.displayMetrics)