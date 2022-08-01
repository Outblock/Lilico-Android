package io.outblock.lilico.page.nft.nftlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import io.outblock.lilico.R
import io.outblock.lilico.databinding.FragmentNftGridBinding
import io.outblock.lilico.page.nft.nftlist.adapter.NFTListAdapter
import io.outblock.lilico.page.nft.nftlist.model.NFTCountTitleModel
import io.outblock.lilico.utils.extensions.res2dip
import io.outblock.lilico.widgets.itemdecoration.GridSpaceItemDecoration

internal class NftGridFragment : Fragment() {

    private lateinit var binding: FragmentNftGridBinding
    private lateinit var viewModel: NFTFragmentViewModel

    private val adapter by lazy { NFTListAdapter() }

    private val dividerSize by lazy { R.dimen.nft_list_divider_size.res2dip().toDouble() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentNftGridBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()
        binding.root.setBackgroundResource(R.color.background)
        viewModel = ViewModelProvider(requireActivity())[NFTFragmentViewModel::class.java].apply {
            gridDataLiveData.observe(viewLifecycleOwner) { adapter.setNewDiffData(it) }
        }
    }

    private fun setupRecyclerView() {
        with(binding.recyclerView) {
            adapter = this@NftGridFragment.adapter
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
        return item is NFTCountTitleModel
    }

    companion object {
        fun newInstance(): NftGridFragment {
            return NftGridFragment()
        }
    }
}