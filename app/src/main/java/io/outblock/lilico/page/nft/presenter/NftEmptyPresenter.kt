package io.outblock.lilico.page.nft.presenter

import com.bumptech.glide.Glide
import io.outblock.lilico.R
import io.outblock.lilico.databinding.LayoutNftEmptyBinding
import io.outblock.lilico.utils.extensions.setVisible
import jp.wasabeef.glide.transformations.BlurTransformation

class NftEmptyPresenter(
    private val binding: LayoutNftEmptyBinding,
) {

    init {
        with(binding) {
            getNewButton.setOnClickListener { }
            Glide.with(backgroundImage).load(R.drawable.bg_empty).transform(BlurTransformation(15, 30)).into(backgroundImage)
        }
    }

    fun setVisible(isVisible: Boolean) {
        binding.root.setVisible(isVisible)
    }
}