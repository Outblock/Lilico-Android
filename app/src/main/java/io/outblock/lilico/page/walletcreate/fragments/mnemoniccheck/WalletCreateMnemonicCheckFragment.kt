package io.outblock.lilico.page.walletcreate.fragments.mnemoniccheck

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.databinding.FragmentWalletCreateMnemonicCheckBinding

class WalletCreateMnemonicCheckFragment : Fragment() {

    private lateinit var binding: FragmentWalletCreateMnemonicCheckBinding
    private lateinit var presenter: WalletCreateMnemonicCheckPresenter
    private lateinit var viewModel: WalletCreateMnemonicCheckViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentWalletCreateMnemonicCheckBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter = WalletCreateMnemonicCheckPresenter(this, binding)
        viewModel = ViewModelProvider(this)[WalletCreateMnemonicCheckViewModel::class.java].apply {
            mnemonicList.observe(viewLifecycleOwner) { presenter.bind(WalletCreateMnemonicCheckModel(mnemonicList = it)) }
        }
    }
}