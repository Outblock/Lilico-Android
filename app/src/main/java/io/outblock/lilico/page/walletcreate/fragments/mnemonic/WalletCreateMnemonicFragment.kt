package io.outblock.lilico.page.walletcreate.fragments.mnemonic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import io.outblock.lilico.databinding.FragmentWalletCreateMnemonicBinding
import io.outblock.lilico.page.walletcreate.WALLET_CREATE_STEP_CLOUD_PWD
import io.outblock.lilico.page.walletcreate.WALLET_CREATE_STEP_MNEMONIC_CHECK
import io.outblock.lilico.page.walletcreate.WalletCreateViewModel
import io.outblock.lilico.utils.extensions.dp2px
import io.outblock.lilico.widgets.itemdecoration.GridSpaceItemDecoration

class WalletCreateMnemonicFragment : Fragment() {

    private lateinit var binding: FragmentWalletCreateMnemonicBinding

    private lateinit var viewModel: WalletCreateMnemonicViewModel

    private val pageViewModel by lazy { ViewModelProvider(requireActivity())[WalletCreateViewModel::class.java] }

    private val adapter = MnemonicAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentWalletCreateMnemonicBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding.mnemonicContainer) {
            adapter = this@WalletCreateMnemonicFragment.adapter
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            addItemDecoration(GridSpaceItemDecoration(horizontal = 20.dp2px().toDouble(), vertical = 8.dp2px().toDouble()))
        }

        with(binding) {
            backupCloud.setOnClickListener { pageViewModel.changeStep(WALLET_CREATE_STEP_CLOUD_PWD) }
            backupManually.setOnClickListener { pageViewModel.changeStep(WALLET_CREATE_STEP_MNEMONIC_CHECK) }
        }

        viewModel = ViewModelProvider(this)[WalletCreateMnemonicViewModel::class.java].apply {
            mnemonicList.observe(viewLifecycleOwner, { adapter.setNewDiffData(it) })
            loadMnemonic()
        }
    }
}