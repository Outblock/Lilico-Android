package io.outblock.lilico.page.walletcreate.fragments.cloudpwd

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.outblock.lilico.databinding.FragmentWalletCreateWarningBinding

class WalletCreateCloudPwdFragment : Fragment() {

    private lateinit var binding: FragmentWalletCreateWarningBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentWalletCreateWarningBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    }
}