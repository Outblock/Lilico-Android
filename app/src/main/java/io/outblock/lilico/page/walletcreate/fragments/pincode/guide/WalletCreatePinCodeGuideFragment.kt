package io.outblock.lilico.page.walletcreate.fragments.pincode.guide

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.outblock.lilico.databinding.FragmentWalletCreatePinGuideBinding

class WalletCreatePinCodeGuideFragment : Fragment() {

    private lateinit var binding: FragmentWalletCreatePinGuideBinding
    private lateinit var presenter: WalletCreatePinCodeGuidePresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentWalletCreatePinGuideBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter = WalletCreatePinCodeGuidePresenter(this, binding)
    }
}