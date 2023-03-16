package io.outblock.lilico.page.nft.nftlist.presenter

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import io.outblock.lilico.R
import io.outblock.lilico.databinding.LayoutNftEmptyBinding
import io.outblock.lilico.page.main.HomeTab
import io.outblock.lilico.page.main.MainActivityViewModel
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.findActivity
import jp.wasabeef.glide.transformations.BlurTransformation

class NftEmptyPresenter(
    private val binding: LayoutNftEmptyBinding,
) {

    init {
        with(binding) {
            getNewButton.setOnClickListener {
                ViewModelProvider(findActivity(binding.root) as FragmentActivity)[MainActivityViewModel::class.java].changeTab(HomeTab.EXPLORE)
            }
            Glide.with(backgroundImage).load(R.drawable.bg_empty).transform(BlurTransformation(10, 20)).into(backgroundImage)
        }
    }

    fun setVisible(isVisible: Boolean) {
        binding.root.setVisible(isVisible)
    }
}