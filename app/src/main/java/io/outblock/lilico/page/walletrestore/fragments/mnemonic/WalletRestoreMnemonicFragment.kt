package io.outblock.lilico.page.walletrestore.fragments.mnemonic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.outblock.lilico.databinding.FragmentWalletRestoreMnemonicBinding

class WalletRestoreMnemonicFragment : Fragment() {

    private lateinit var binding: FragmentWalletRestoreMnemonicBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentWalletRestoreMnemonicBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    }

}