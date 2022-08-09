package io.outblock.lilico.page.nft.nftlist

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.appbar.AppBarLayout
import com.zackratos.ultimatebarx.ultimatebarx.statusBarHeight
import io.outblock.lilico.R
import io.outblock.lilico.cache.NftSelections
import io.outblock.lilico.cache.nftSelectionCache
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.databinding.FragmentNftListBinding
import io.outblock.lilico.page.nft.nftlist.adapter.NFTListAdapter
import io.outblock.lilico.page.nft.nftlist.model.*
import io.outblock.lilico.page.nft.nftlist.presenter.CollectionTabsPresenter
import io.outblock.lilico.page.nft.nftlist.presenter.CollectionTitlePresenter
import io.outblock.lilico.page.nft.nftlist.presenter.SelectionItemPresenter
import io.outblock.lilico.utils.extensions.res2dip
import io.outblock.lilico.utils.extensions.res2pix
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.uiScope
import io.outblock.lilico.widgets.itemdecoration.ColorDividerItemDecoration
import io.outblock.lilico.widgets.itemdecoration.GridSpaceItemDecoration
import jp.wasabeef.glide.transformations.BlurTransformation

internal class NftListFragment : Fragment() {

    private lateinit var binding: FragmentNftListBinding
    private lateinit var viewModel: NFTFragmentViewModelV0
    private lateinit var viewModelV1: NftViewModel

    private val nftAdapter by lazy { NFTListAdapter() }
    private val collectionsAdapter by lazy { NFTListAdapter() }

    private val dividerSize by lazy { R.dimen.nft_list_divider_size.res2dip().toDouble() }

    private val selectionPresenter by lazy { SelectionItemPresenter(binding.topSelectionHeader) }

    private val collectionTabsPresenter by lazy { CollectionTabsPresenter(binding.collectionTabs) }
    private val collectionTitlePresenter by lazy { CollectionTitlePresenter(binding.collectionTitle.root) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentNftListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupNftRecyclerView()
        setupCollectionsRecyclerView()
        setupScrollView()
        binding.root.setBackgroundResource(R.color.background)
        viewModel = ViewModelProvider(requireActivity())[NFTFragmentViewModelV0::class.java].apply {
            topSelectionLiveData.observe(viewLifecycleOwner) { data ->
                selectionPresenter.bind(data)
                if (data.data.isEmpty()) {
                    Glide.with(binding.backgroundImage).clear(binding.backgroundImage)
                }
            }
            selectionIndexLiveData.observe(viewLifecycleOwner) { updateSelection(it) }
        }

        viewModelV1 = ViewModelProvider(requireActivity())[NftViewModel::class.java].apply {
            requestList()
            listNftLiveData.observe(viewLifecycleOwner) { data -> updateListData(data) }
            collectionsLiveData.observe(viewLifecycleOwner) { data -> updateCollections(data) }
            collectionTitleLiveData.observe(viewLifecycleOwner) { collectionTitlePresenter.bind(it) }
        }
    }

    private fun updateListData(data: List<Any>) {
        nftAdapter.setNewDiffData(data)
        binding.nftRecyclerView.setVisible(!viewModelV1.isCollectionExpanded())
    }

    private fun updateCollections(data: List<CollectionItemModel>) {
        collectionsAdapter.setNewDiffData(data)
        collectionTabsPresenter.bind(CollectionTabsModel(data))
        binding.collectionRecyclerView.setVisible(viewModelV1.isCollectionExpanded())
        collectionTabsPresenter.bind(CollectionTabsModel(isExpand = !viewModelV1.isCollectionExpanded()))
    }

    private fun setupScrollView() {
        var preOffset = 0
        binding.appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            binding.backgroundWrapper.translationY = verticalOffset.toFloat()
            viewModel.onListScrollChange(-verticalOffset)
            if (preOffset != verticalOffset) {
                findSwipeRefreshLayout(binding.root)?.isEnabled = verticalOffset >= 0
                logd("xxx", "NftListFragment: ${findSwipeRefreshLayout(binding.root)}")
            }
            preOffset = verticalOffset
        })
        with(binding.scrollView) {
            setPadding(paddingLeft, R.dimen.nft_tool_bar_height.res2pix() + statusBarHeight, paddingRight, paddingBottom)
        }
    }

    private fun setupNftRecyclerView() {
        with(binding.nftRecyclerView) {
            adapter = this@NftListFragment.nftAdapter
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (isSingleLineItem(position)) spanCount else 1
                    }
                }
            }
            addItemDecoration(GridSpaceItemDecoration(vertical = dividerSize, horizontal = dividerSize, start = dividerSize, end = dividerSize))
        }
    }

    private fun setupCollectionsRecyclerView() {
        with(binding.collectionRecyclerView) {
            adapter = this@NftListFragment.collectionsAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(ColorDividerItemDecoration(Color.TRANSPARENT, dividerSize.toInt()))
        }
    }

    private fun isSingleLineItem(position: Int): Boolean {
        val item = nftAdapter.getData().getOrNull(position) ?: return false
        return item is HeaderPlaceholderModel || item is NFTTitleModel || item is NftSelections
          || item is CollectionTitleModel || item is CollectionItemModel || item is CollectionTabsModel
    }

    private fun updateSelection(index: Int) {
        if (index < 0) {
            Glide.with(binding.backgroundImage).clear(binding.backgroundImage)
        }
        ioScope {
            val data = nftSelectionCache(walletCache().read()?.primaryWalletAddress()).read()?.data?.reversed() ?: return@ioScope
            val nft = data.getOrNull(index) ?: return@ioScope
            uiScope {
                if (viewModel.selectionIndexLiveData.value != index) {
                    return@uiScope
                }
                val oldUrl = binding.backgroundImage.tag as? String
                Glide.with(binding.backgroundImage)
                    .load(nft.cover())
                    .thumbnail(Glide.with(requireContext()).load(oldUrl).transform(BlurTransformation(10, 20)))
                    .transition(DrawableTransitionOptions.withCrossFade(250))
                    .transform(BlurTransformation(10, 20))
                    .into(binding.backgroundImage)
                binding.backgroundImage.tag = nft.cover()
            }
        }
    }

    companion object {
        fun newInstance(): NftListFragment {
            return NftListFragment()
        }
    }
}