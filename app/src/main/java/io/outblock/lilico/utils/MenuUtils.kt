package io.outblock.lilico.utils

import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ImageSpan
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import io.outblock.lilico.R
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.widgets.CustomTypefaceSpan
import io.outblock.lilico.widgets.VerticalAlignTextSpan
import kotlin.math.min

private val font by lazy { Env.getApp().resources.getFont(R.font.inter) }

fun generateMenuItem(
    @StringRes text: Int,
    @DrawableRes drawableRes: Int,
    @ColorInt drawableColor: Int = 0,
    maxIconWidth: Int = 0,
    maxIconHeight: Int = 0,
): CharSequence {
    return generateMenuItem(text.res2String(), drawableRes, drawableColor, maxIconWidth, maxIconHeight)
}

fun generateMenuItem(
    text: String,
    @DrawableRes drawableRes: Int,
    @ColorInt drawableColor: Int = 0,
    maxIconWidth: Int = 0,
    maxIconHeight: Int = 0,
): CharSequence {
    val resource = Env.getApp().resources
    val drawable = ResourcesCompat.getDrawable(resource, drawableRes, null)?.apply {
        setBounds(0, 0, min(intrinsicWidth, maxIconWidth), min(intrinsicHeight, maxIconHeight))
        if (drawableColor != 0) {
            setTint(drawableColor)
        }
    }
    return SpannableString("${if (drawable == null) "" else "    "}${text}").apply {
        setSpan(VerticalAlignTextSpan(-1, -1), 0, length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        setSpan(CustomTypefaceSpan(font), 0, length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
        drawable?.let { setSpan(ImageSpan(it), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) }
    }
}