package io.outblock.lilico.page.profile.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.annotation.DrawableRes
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.load
import io.outblock.lilico.R

class ProfilePreferenceImage : ProfilePreference {

    private val imageView by lazy { LayoutInflater.from(context).inflate(R.layout.view_profile_preference_image, this, false) as ImageView }

    private var onCheckedChangeListener: ((isChecked: Boolean) -> Unit)? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet? = null) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr)

    init {
        setExtendView(imageView)
    }

    fun setImageRes(@DrawableRes imageRes: Int) {
        imageView.setImageResource(imageRes)
    }

    fun setImageUrl(imageUrl: String) {
        val loader = ImageLoader.Builder(context).componentRegistry {
            add(SvgDecoder(context))
        }.build()
        imageView.load(imageUrl, loader) {
            placeholder(R.drawable.placeholder)
        }
    }
}