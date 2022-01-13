package io.outblock.lilico.page.nft.model

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes

data class NFTTitleModel(
    @DrawableRes val icon: Int,
    @ColorInt val iconTint: Int = Color.WHITE,
    val text: String,
)