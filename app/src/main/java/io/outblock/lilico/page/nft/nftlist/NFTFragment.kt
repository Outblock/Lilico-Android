package io.outblock.lilico.page.nft.nftlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.databinding.FragmentNftBinding
import io.outblock.lilico.page.nft.nftlist.model.NFTFragmentModel
import io.outblock.lilico.page.nft.nftlist.presenter.NFTFragmentPresenter
import io.outblock.lilico.page.nft.nftlist.presenter.NftEmptyPresenter
import io.outblock.lilico.utils.stopShimmer

class NFTFragment : Fragment() {

    private lateinit var binding: FragmentNftBinding

    private lateinit var presenter: NFTFragmentPresenter
    private lateinit var emptyPresenter: NftEmptyPresenter

    private lateinit var viewModel: NFTFragmentViewModelV0

    private lateinit var viewModelV1: NftViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentNftBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter = NFTFragmentPresenter(this, binding)
        emptyPresenter = NftEmptyPresenter(binding.emptyContainer)
        viewModel = ViewModelProvider(requireActivity())[NFTFragmentViewModelV0::class.java].apply {
            topSelectionLiveData.observe(viewLifecycleOwner) { presenter.bind(NFTFragmentModel(topSelection = it)) }
            listScrollChangeLiveData.observe(viewLifecycleOwner) { presenter.bind(NFTFragmentModel(onListScrollChange = it)) }
        }

        viewModelV1 = ViewModelProvider(requireActivity())[NftViewModel::class.java].apply {
            listNftLiveData.observe(viewLifecycleOwner) { presenter.bind(NFTFragmentModel(listPageData = it)) }
            emptyLiveData.observe(viewLifecycleOwner) { isEmpty ->
                emptyPresenter.setVisible(isEmpty)
                stopShimmer(binding.shimmerLayout.shimmerLayout)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }
}