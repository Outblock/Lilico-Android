package io.outblock.lilico.page.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.outblock.lilico.databinding.FragmentWalletBinding
import io.outblock.lilico.page.walletcreate.WalletCreateActivity

class WalletFragment : Fragment() {

    private lateinit var binding: FragmentWalletBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentWalletBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.createButton.setOnClickListener { WalletCreateActivity.launch(requireContext()) }
    }
}