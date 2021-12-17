package io.outblock.lilico.page.walletcreate.fragments.pincode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.outblock.lilico.databinding.FragmentWalletCreateUsernameBinding

class WalletCreatePinCodeFragment : Fragment() {

    private lateinit var binding: FragmentWalletCreateUsernameBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentWalletCreateUsernameBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    }
}