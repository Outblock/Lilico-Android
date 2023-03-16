package io.outblock.lilico.page.nft.nftlist.model

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import io.outblock.lilico.R
import io.outblock.lilico.utils.extensions.res2color

data class NFTTitleModel(
    val isList: Boolean,
    @DrawableRes val icon: Int,
    @ColorInt val iconTint: Int = Color.WHITE,
    val text: String,
    @ColorInt val textColor: Int = R.color.text.res2color(),
)