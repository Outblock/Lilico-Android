package io.outblock.lilico.page.walletcreate.fragments.username

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.databinding.FragmentWalletCreateUsernameBinding

class WalletCreateUsernameFragment : Fragment() {

    private lateinit var binding: FragmentWalletCreateUsernameBinding

    private lateinit var presenter: WalletCreateUsernamePresenter

    private lateinit var viewModel: WalletCreateUsernameViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentWalletCreateUsernameBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter = WalletCreateUsernamePresenter(this, binding)

        viewModel = ViewModelProvider(this)[WalletCreateUsernameViewModel::class.java].apply {
            usernameStateLiveData.observe(viewLifecycleOwner, { presenter.bind(WalletCreateUsernameModel(state = it)) })
        }
    }
}