package io.outblock.lilico.page.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.databinding.FragmentWalletBinding
import io.outblock.lilico.page.wallet.model.WalletFragmentModel
import io.outblock.lilico.page.wallet.presenter.WalletFragmentPresenter

class WalletFragment : Fragment() {

    private lateinit var binding: FragmentWalletBinding
    private lateinit var viewModel: WalletFragmentViewModel
    private lateinit var presenter: WalletFragmentPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentWalletBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter = WalletFragmentPresenter(this, binding)

        viewModel = ViewModelProvider(this)[WalletFragmentViewModel::class.java].apply {
            dataListLiveData.observe(viewLifecycleOwner) { presenter.bind(WalletFragmentModel(data = it)) }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.load()
    }
}