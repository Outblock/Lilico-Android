package io.outblock.lilico.page.wallet.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.R
import io.outblock.lilico.databinding.FragmentWalletHomeBinding
import io.outblock.lilico.page.main.MainActivityViewModel
import io.outblock.lilico.page.wallet.WalletFragment
import io.outblock.lilico.utils.isRegistered
import io.outblock.lilico.utils.uiScope


class WalletHomeFragment : Fragment() {

    private lateinit var binding: FragmentWalletHomeBinding

    private val pageViewModel by lazy { ViewModelProvider(requireActivity())[MainActivityViewModel::class.java] }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentWalletHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bindFragment()
        pageViewModel.walletRegisterSuccessLiveData.observe(viewLifecycleOwner) { bindFragment() }
    }

    private fun bindFragment() {
        uiScope {
            childFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, if (isRegistered()) WalletFragment() else WalletUnregisteredFragment())
                .commitAllowingStateLoss()
        }
    }

}