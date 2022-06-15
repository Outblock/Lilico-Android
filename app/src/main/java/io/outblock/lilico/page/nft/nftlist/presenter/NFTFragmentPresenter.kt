package io.outblock.lilico.page.nft.nftlist.presenter

import android.animation.ArgbEvaluator
import androidx.lifecycle.ViewModelProvider
import com.flyco.tablayout.listener.OnTabSelectListener
import com.zackratos.ultimatebarx.ultimatebarx.statusBarHeight
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.FragmentNftBinding
import io.outblock.lilico.page.nft.collectionlist.NftCollectionListActivity
import io.outblock.lilico.page.nft.nftlist.NFTFragment
import io.outblock.lilico.page.nft.nftlist.NFTFragmentViewModel
import io.outblock.lilico.page.nft.nftlist.adapter.NftListPageAdapter
import io.outblock.lilico.page.nft.nftlist.model.NFTFragmentModel
import io.outblock.lilico.utils.ScreenUtils
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.extensions.res2color
import java.lang.Float.min

class NFTFragmentPresenter(
    private val fragment: NFTFragment,
    private val binding: FragmentNftBinding,
) : BasePresenter<NFTFragmentModel> {

    private val viewModel by lazy { ViewModelProvider(fragment.requireActivity())[NFTFragmentViewModel::class.java] }

    private var isTopSelectionExist = false

    init {
        with(binding) {
            with(toolbar) { post { setPadding(paddingLeft, paddingTop + statusBarHeight, paddingRight, paddingBottom) } }
            viewPager.adapter = NftListPageAdapter(fragment)
            addButton.setOnClickListener { NftCollectionListActivity.launch(fragment.requireContext()) }

            with(refreshLayout) {
                isEnabled = false
                setOnRefreshListener { viewModel.refresh() }
                setColorSchemeColors(R.color.colorSecondary.res2color())
            }
        }

        setupTabs()
    }

    override fun bind(model: NFTFragmentModel) {
        model.topSelection?.let {
            isTopSelectionExist = it.data.isNotEmpty()
            updateToolbarBackground()
        }
        model.onListScrollChange?.let { updateToolbarBackground(it) }
        model.listPageData?.let { binding.refreshLayout.isRefreshing = false }
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
        val maxScrollY = ScreenUtils.getScreenHeight() * 0.25f
        return min(scroll / maxScrollY, 1f)
    }

    private fun updateToolbarBackground(scrollY: Int = -1) {
        val isList = !viewModel.isGridMode()
        if (isList) {
            if (!isTopSelectionExist) {
                // no selection
                binding.toolbar.background.alpha = 255
                binding.tabsBackground.background.setTint(R.color.neutrals4.res2color())
            } else {
                val progress = listPageScrollProgress(scrollY)
                binding.toolbar.background.alpha = (255 * progress).toInt()
                binding.tabsBackground.background.setTint(
                    ArgbEvaluator().evaluate(
                        progress,
                        R.color.white.res2color(),
                        R.color.neutrals4.res2color()
                    ) as Int
                )
            }
        } else {
            binding.toolbar.background.alpha = 255
            binding.tabsBackground.background.setTint(R.color.neutrals4.res2color())
        }
    }

}