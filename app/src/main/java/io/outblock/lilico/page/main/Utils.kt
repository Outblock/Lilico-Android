package io.outblock.lilico.page.main

import android.R
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.SimpleColorFilter
import com.airbnb.lottie.model.KeyPath
import com.airbnb.lottie.value.LottieValueCallback
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.outblock.lilico.utils.extensions.colorStateList
import io.outblock.lilico.utils.extensions.res2color


enum class HomeTab(val index: Int) {
    WALLET(0),
    NFT(1),
    EXPLORE(2),
    PROFILE(3),
}

private val lottieMenu by lazy {
    listOf(
        io.outblock.lilico.R.raw.lottie_coinhover,
        io.outblock.lilico.R.raw.lottie_grid,
        io.outblock.lilico.R.raw.lottie_compass,
        io.outblock.lilico.R.raw.lottie_avatar,
    )
}

private val menuColor by lazy {
    listOf(
        io.outblock.lilico.R.color.bottom_navigation_color_wallet,
        io.outblock.lilico.R.color.bottom_navigation_color_nft,
        io.outblock.lilico.R.color.bottom_navigation_color_explore,
        io.outblock.lilico.R.color.bottom_navigation_color_profile,
    )
}

fun BottomNavigationView.activeColor(index: Int): Int {
    return menuColor[index].colorStateList(context)?.getColorForState(intArrayOf(R.attr.state_checked), 0)!!
}

fun BottomNavigationView.setLottieDrawable(index: Int, isSelected: Boolean, playAnimation: Boolean = false) {
    menu.getItem(index).icon = LottieDrawable().apply {
        callback = this
        composition = LottieCompositionFactory.fromRawResSync(context, lottieMenu[index]).value
        addValueCallback(
            KeyPath("**"),
            LottieProperty.COLOR_FILTER,
            LottieValueCallback(SimpleColorFilter(if (isSelected) activeColor(index) else io.outblock.lilico.R.color.neutrals8.res2color()))
        )
        if (playAnimation) playAnimation()
    }
}