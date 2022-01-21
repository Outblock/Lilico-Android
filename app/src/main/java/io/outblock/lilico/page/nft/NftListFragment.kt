package io.outblock.lilico.page.nft

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import io.outblock.lilico.R
import io.outblock.lilico.cache.NftSelections
import io.outblock.lilico.cache.nftSelectionCache
import io.outblock.lilico.databinding.FragmentNftListBinding
import io.outblock.lilico.page.nft.adapter.NFTListAdapter
import io.outblock.lilico.page.nft.model.*
import io.outblock.lilico.utils.extensions.res2dip
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.uiScope
import io.outblock.lilico.widgets.itemdecoration.DividerVisibleCheck
import io.outblock.lilico.widgets.itemdecoration.GridSpaceItemDecoration
import jp.wasabeef.glide.transformations.BlurTransformation

internal class NftListFragment : Fragment() {

    private val isList by lazy { arguments?.getBoolean(EXTRA_IS_LIST) ?: true }

    private lateinit var binding: FragmentNftListBinding
    private lateinit var viewModel: NFTFragmentViewModel

    private val adapter by lazy { NFTListAdapter() }

    private val dividerSize by lazy { R.dimen.nft_list_divider_size.res2dip().toDouble() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentNftListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()
        viewModel = ViewModelProvider(requireActivity())[NFTFragmentViewModel::class.java].apply {
            if (isList) {
                listDataLiveData.observe(viewLifecycleOwner) { adapter.setNewDiffData(it) }
                selectionIndexLiveData.observe(viewLifecycleOwner) { updateSelection(it) }
            } else {
                gridDataLiveData.observe(viewLifecycleOwner) { adapter.setNewDiffData(it) }
            }
            load()
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
                ).apply {
                    setDividerVisibleCheck(object : DividerVisibleCheck {
                        override fun dividerVisible(position: Int): Boolean {
                            val item = this@NftListFragment.adapter.getData().getOrNull(position) ?: return true
                            return item !is NftSelections
                        }
                    })
                })

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (isList) {
                        viewModel.onListScrollChange(computeVerticalScrollOffset())
                    }
                }
            })
        }
    }

    private fun isSingleLineItem(position: Int): Boolean {
        val item = adapter.getData().getOrNull(position) ?: return false
        return item is HeaderPlaceholderModel || item is NFTTitleModel || item is NftSelections
          || item is CollectionTitleModel || item is CollectionItemModel || item is CollectionTabsModel
    }

    private fun updateSelection(index: Int) {
        ioScope {
            if (viewModel.isGridMode()) {
                return@ioScope
            }
            val data = nftSelectionCache().read()?.data?.reversed() ?: return@ioScope
            val nft = data.getOrNull(index) ?: return@ioScope
            uiScope {
                Glide.with(binding.backgroundImage)
                    .load(nft.cover())
                    .transition(DrawableTransitionOptions.withCrossFade(100))
                    .placeholder(binding.backgroundImage.drawable)
                    .transform(BlurTransformation(10, 20))
                    .into(binding.backgroundImage)
            }
        }
    }

    companion object {
        private const val EXTRA_IS_LIST = "extra_is_list"
        fun newInstance(isList: Boolean): NftListFragment {
            return NftListFragment().apply {
                arguments = Bundle().apply { putBoolean(EXTRA_IS_LIST, isList) }
            }
        }
    }
}