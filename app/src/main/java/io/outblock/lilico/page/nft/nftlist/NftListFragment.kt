package io.outblock.lilico.page.nft.nftlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.appbar.AppBarLayout
import com.zackratos.ultimatebarx.ultimatebarx.statusBarHeight
import io.outblock.lilico.R
import io.outblock.lilico.cache.NftSelections
import io.outblock.lilico.cache.nftSelectionCache
import io.outblock.lilico.databinding.FragmentNftListBinding
import io.outblock.lilico.page.nft.nftlist.adapter.NFTListAdapter
import io.outblock.lilico.page.nft.nftlist.model.*
import io.outblock.lilico.page.nft.nftlist.presenter.CollectionTabsPresenter
import io.outblock.lilico.page.nft.nftlist.presenter.CollectionTitlePresenter
import io.outblock.lilico.page.nft.nftlist.presenter.SelectionItemPresenter
import io.outblock.lilico.utils.extensions.res2color
import io.outblock.lilico.utils.extensions.res2dip
import io.outblock.lilico.utils.extensions.res2pix
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.uiScope
import io.outblock.lilico.widgets.itemdecoration.GridSpaceItemDecoration
import jp.wasabeef.glide.transformations.BlurTransformation

internal class NftListFragment : Fragment() {

    private lateinit var binding: FragmentNftListBinding
    private lateinit var viewModel: NFTFragmentViewModel

    private val adapter by lazy { NFTListAdapter() }

    private val dividerSize by lazy { R.dimen.nft_list_divider_size.res2dip().toDouble() }

    private val selectionPresenter by lazy { SelectionItemPresenter(binding.topSelectionHeader) }

    private val collectionTabsPresenter by lazy { CollectionTabsPresenter(binding.collectionTabs) }
    private val collectionTitlePresenter by lazy { CollectionTitlePresenter(binding.collectionTitle.root) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentNftListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()
        setupScrollView()
        setupRefreshLayout()
        binding.root.setBackgroundResource(R.color.background)
        viewModel = ViewModelProvider(requireActivity())[NFTFragmentViewModel::class.java].apply {
            listDataLiveData.observe(viewLifecycleOwner) { data -> updateListData(data) }
            topSelectionLiveData.observe(viewLifecycleOwner) { data ->
                selectionPresenter.bind(data)
                if (data.data.isEmpty()) {
                    Glide.with(binding.backgroundImage).clear(binding.backgroundImage)
                }
            }
            selectionIndexLiveData.observe(viewLifecycleOwner) { updateSelection(it) }
            collectionTabsLiveData.observe(viewLifecycleOwner) { collectionTabsPresenter.bind(it) }
            collectionExpandChangeLiveData.observe(viewLifecycleOwner) { collectionTabsPresenter.bind(CollectionTabsModel(isExpand = it)) }
            collectionTitleLiveData.observe(viewLifecycleOwner) { collectionTitlePresenter.bind(it) }
            loadList()
        }
    }

    private fun setupRefreshLayout() {
        with(binding.refreshView) {
//            isEnabled = false
            setOnRefreshListener { viewModel.refresh() }
            setColorSchemeColors(R.color.colorSecondary.res2color())
        }
    }

    private fun updateListData(data: List<Any>) {
        adapter.setNewDiffData(data)
        binding.refreshView.isRefreshing = false
        with(binding.recyclerView) {
            if (viewModel.isCollectionExpanded()) {
                setBackgroundResource(R.drawable.bg_top_radius_16dp)
                setPadding(paddingLeft, R.dimen.nft_list_divider_size.res2pix(), paddingRight, paddingBottom)
            } else {
                setBackgroundResource(R.color.transparent)
                setPadding(paddingLeft, 0, paddingRight, paddingBottom)
            }
        }
    }

    private fun setupScrollView() {
        var preOffset = 0
        binding.appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            binding.backgroundWrapper.translationY = verticalOffset.toFloat()
            viewModel.onListScrollChange(-verticalOffset)
            if (preOffset != verticalOffset) {
                binding.refreshView.isEnabled = verticalOffset >= 0
            }
            preOffset = verticalOffset
        })
        with(binding.scrollView) {
            setPadding(paddingLeft, R.dimen.nft_tool_bar_height.res2pix() + statusBarHeight, paddingRight, paddingBottom)
        }
    }

    private fun setupRecyclerView() {
        with(binding.recyclerView) {
            adapter = this@NftListFragment.adapter
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (isSingleLineItem(position)) spanCount else 1
                    }
                }
            }
            addItemDecoration(
                GridSpaceItemDecoration(
                    vertical = dividerSize,
                    horizontal = dividerSize,
                    start = dividerSize,
                    end = dividerSize
                )
            )
        }
    }

    private fun isSingleLineItem(position: Int): Boolean {
        val item = adapter.getData().getOrNull(position) ?: return false
        return item is HeaderPlaceholderModel || item is NFTTitleModel || item is NftSelections
          || item is CollectionTitleModel || item is CollectionItemModel || item is CollectionTabsModel
    }

    private fun updateSelection(index: Int) {
        if (index < 0) {
            Glide.with(binding.backgroundImage).clear(binding.backgroundImage)
        }
        ioScope {
            val data = nftSelectionCache().read()?.data?.reversed() ?: return@ioScope
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