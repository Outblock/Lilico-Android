package io.outblock.lilico.utils.extensions

import android.graphics.BlurMaskFilter
import android.graphics.MaskFilter
import android.text.SpannableString
import android.text.Spanned
import android.text.style.MaskFilterSpan
import android.widget.TextView


/**
 * @author John
 * @since 2020-04-04 10:01
 */

fun TextView.isEllipsized(): Boolean {
    if (layout != null) {
        val lines: Int = layout.lineCount
        return lines > 0 && layout.getEllipsisCount(lines - 1) > 0 || lines > maxLines
    }
    return false
}

fun TextView.setBlurText(text: CharSequence, blurLevel: Float = 3f) {
    if (blurLevel == 0f) {
        setText(text)
        return
    }
    val blurMask: MaskFilter = BlurMaskFilter(blurLevel, BlurMaskFilter.Blur.NORMAL)
    val string = SpannableString(text)
    string.setSpan(MaskFilterSpan(blurMask), 0, string.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    setText(string)
}