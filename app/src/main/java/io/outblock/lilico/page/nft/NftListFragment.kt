package io.outblock.lilico.page.nft

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import io.outblock.lilico.R
import io.outblock.lilico.cache.NftSelections
import io.outblock.lilico.cache.nftSelectionCache
import io.outblock.lilico.databinding.FragmentNftListBinding
import io.outblock.lilico.page.nft.adapter.NFTListAdapter
import io.outblock.lilico.page.nft.model.*
import io.outblock.lilico.page.nft.presenter.SelectionItemPresenter
import io.outblock.lilico.utils.ScreenUtils
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

    private val selectionPresenter by lazy { SelectionItemPresenter(binding.topSelectionHeader) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentNftListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()
        setupScrollView()
        binding.root.setBackgroundResource(if (isList) R.color.neutrals12 else R.color.colorPrimary)
        binding.backgroundWrapper.layoutParams.height = (ScreenUtils.getScreenHeight() * 0.55).toInt()
        viewModel = ViewModelProvider(requireActivity())[NFTFragmentViewModel::class.java].apply {
            if (isList) {
                listDataLiveData.observe(viewLifecycleOwner) { data ->
                    adapter.setNewDiffData(data)
                }
                topSelectionLiveData.observe(viewLifecycleOwner) { data ->
                    selectionPresenter.bind(data)
                    if (data.data.isEmpty()) {
                        Glide.with(binding.backgroundImage).clear(binding.backgroundImage)
                    }
                }
                selectionIndexLiveData.observe(viewLifecycleOwner) { updateSelection(it) }
            } else {
                gridDataLiveData.observe(viewLifecycleOwner) { adapter.setNewDiffData(it) }
            }
            load()
        }
    }

    private fun setupScrollView() {
        binding.root.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
            if (isList) {
                viewModel.onListScrollChange(scrollY)
            }
        })
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
                            return !(item is NftSelections || item is CollectionTabsModel
                              || (isList && item is NFTTitleModel) || item is NFTItemModel)
                        }
                    })
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

            if (index < 0) {
                Glide.with(binding.backgroundImage).clear(binding.backgroundImage)
            }

            val data = nftSelectionCache().read()?.data?.reversed() ?: return@ioScope
            val nft = data.getOrNull(index) ?: return@ioScope
            uiScope {
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
        private const val EXTRA_IS_LIST = "extra_is_list"
        fun newInstance(isList: Boolean): NftListFragment {
            return NftListFragment().apply {
                arguments = Bundle().apply { putBoolean(EXTRA_IS_LIST, isList) }
            }
        }
    }
}