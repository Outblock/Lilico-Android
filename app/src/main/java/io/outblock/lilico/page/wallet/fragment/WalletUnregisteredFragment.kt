package io.outblock.lilico.page.wallet.fragment

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.outblock.lilico.databinding.FragmentWalletUnregisteredBinding
import io.outblock.lilico.page.walletcreate.WalletCreateActivity
import io.outblock.lilico.page.walletrestore.WalletRestoreActivity

class WalletUnregisteredFragment : Fragment() {

    private lateinit var binding: FragmentWalletUnregisteredBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentWalletUnregisteredBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            createButton.setOnClickListener { WalletCreateActivity.launch(requireContext()) }
            importButton.setOnClickListener { WalletRestoreActivity.launch(requireContext()) }
        }
        setupBackground()
    }

    private fun setupBackground() {
        with(binding.root.background as AnimationDrawable) {
            setEnterFadeDuration(1250)
            setExitFadeDuration(2500)
            start()
        }
    }
}