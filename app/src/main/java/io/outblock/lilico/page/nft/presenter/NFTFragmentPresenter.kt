package io.outblock.lilico.page.nft.presenter

import androidx.lifecycle.ViewModelProvider
import com.flyco.tablayout.listener.OnTabSelectListener
import com.zackratos.ultimatebarx.ultimatebarx.statusBarHeight
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.cache.NftSelections
import io.outblock.lilico.databinding.FragmentNftBinding
import io.outblock.lilico.page.nft.NFTFragment
import io.outblock.lilico.page.nft.NFTFragmentViewModel
import io.outblock.lilico.page.nft.adapter.NftListPageAdapter
import io.outblock.lilico.page.nft.model.NFTFragmentModel
import io.outblock.lilico.utils.ScreenUtils
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.extensions.res2color
import java.lang.Float.min

class NFTFragmentPresenter(
    private val fragment: NFTFragment,
    private val binding: FragmentNftBinding,
) : BasePresenter<NFTFragmentModel> {

    private val viewModel by lazy { ViewModelProvider(fragment.requireActivity())[NFTFragmentViewModel::class.java] }

    private val context = fragment.requireContext()

    private var listPageData: List<Any>? = null

    init {
        with(binding.toolbar) {
            post { setPadding(paddingLeft, paddingTop + statusBarHeight, paddingRight, paddingBottom) }
        }
        with(binding.viewPager) {
            adapter = NftListPageAdapter(fragment.requireActivity())
            isUserInputEnabled = false
        }
        setupTabs()
    }

    override fun bind(model: NFTFragmentModel) {
        model.listPageData?.let {
            updateToolbarBackground()
            listPageData = it
        }
        model.onListScrollChange?.let { updateToolbarBackground(it) }
    }

    private fun setupTabs() {
        with(binding.tabs) {
            setTabData(listOf(R.string.list.res2String(), R.string.grid.res2String()).toTypedArray())
            setOnTabSelectListener(object : OnTabSelectListener {
                override fun onTabSelect(position: Int) {
                    viewModel.updateLayoutMode(position != 0)
                    updateToolbarBackground()
                    binding.viewPager.setCurrentItem(position, false)
                }

                override fun onTabReselect(position: Int) {}
            })
        }
    }

    private fun listPageScrollProgress(scrollY: Int): Float {
        val scroll = if (scrollY < 0) viewModel.listScrollChangeLiveData.value ?: 0 else scrollY
        val maxScrollY = ScreenUtils.getScreenHeight() * 0.3f
        return min(scroll / maxScrollY, 1f)
    }

    private fun updateToolbarBackground(scrollY: Int = -1) {
        val isList = !viewModel.isGridMode()
        if (isList) {
            if (listPageData?.firstOrNull { it is NftSelections } == null) {
                // no selection
                binding.toolbar.background.alpha = 255
                binding.tabsBackground.background.setTint(R.color.neutrals4.res2color())
            } else {
                binding.toolbar.background.alpha = (255 * listPageScrollProgress(scrollY)).toInt()
                binding.tabsBackground.background.setTint(R.color.white.res2color())
            }
        } else {
            binding.toolbar.background.alpha = 255
            binding.tabsBackground.background.setTint(R.color.neutrals4.res2color())
        }
    }

}