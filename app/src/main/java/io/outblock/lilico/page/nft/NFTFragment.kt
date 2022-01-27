package io.outblock.lilico.page.nft

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.databinding.FragmentNftBinding
import io.outblock.lilico.page.nft.model.NFTFragmentModel
import io.outblock.lilico.page.nft.presenter.NFTFragmentPresenter
import io.outblock.lilico.page.nft.presenter.NftEmptyPresenter

class NFTFragment : Fragment() {

    private lateinit var binding: FragmentNftBinding

    private lateinit var presenter: NFTFragmentPresenter
    private lateinit var emptyPresenter: NftEmptyPresenter

    private lateinit var viewModel: NFTFragmentViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentNftBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter = NFTFragmentPresenter(this, binding)
        emptyPresenter = NftEmptyPresenter(binding.emptyContainer)
        viewModel = ViewModelProvider(requireActivity())[NFTFragmentViewModel::class.java].apply {
            listDataLiveData.observe(viewLifecycleOwner) { presenter.bind(NFTFragmentModel(listPageData = it)) }
            topSelectionLiveData.observe(viewLifecycleOwner) { presenter.bind(NFTFragmentModel(topSelection = it)) }
            listScrollChangeLiveData.observe(viewLifecycleOwner) { presenter.bind(NFTFragmentModel(onListScrollChange = it)) }
            emptyLiveData.observe(viewLifecycleOwner) { isEmpty -> emptyPresenter.setVisible(isEmpty) }
            load()
        }
    }
}