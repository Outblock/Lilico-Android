package io.outblock.lilico.page.walletcreate.fragments.username

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.FragmentWalletCreateUsernameBinding
import io.outblock.lilico.page.walletcreate.WalletCreateViewModel
import io.outblock.lilico.utils.extensions.res2color
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.listeners.SimpleTextWatcher
import io.outblock.lilico.utils.updateUsername

class WalletCreateUsernamePresenter(
    private val fragment: Fragment,
    private val binding: FragmentWalletCreateUsernameBinding,
) : BasePresenter<WalletCreateUsernameModel> {

    private val viewModel by lazy { ViewModelProvider(fragment)[WalletCreateUsernameViewModel::class.java] }

    private val pageViewModel by lazy { ViewModelProvider(fragment.requireActivity())[WalletCreateViewModel::class.java] }

    init {
        binding.editText.addTextChangedListener(object : SimpleTextWatcher() {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                hideStateView()
                binding.nextButton.isEnabled = false
                viewModel.verifyUsername(s.toString())
            }
        })
        with(binding.root) {
            post { layoutParams.height = height }
        }
        binding.nextButton.setOnClickListener {
            updateUsername(binding.editText.text.toString())
            pageViewModel.nextStep()
        }
    }

    override fun bind(model: WalletCreateUsernameModel) {
        model.state?.let { updateState(it) }
    }

    private fun updateState(state: Pair<Boolean, String>) {
        if (state.second.isEmpty()) {
            hideStateView()
            return
        }
        with(binding) {
            stateText.setVisible(true)
            stateIcon.setVisible(true)
            nextButton.isEnabled = state.first
            if (state.first) {
                stateText.setTextColor(R.color.text.res2color())
                stateIcon.setImageResource(R.drawable.ic_username_success)
            } else {
                stateText.setTextColor(R.color.text_sub.res2color())
                stateIcon.setImageResource(R.drawable.ic_username_error)
            }
            stateText.text = state.second
        }
    }

    private fun hideStateView() {
        binding.stateText.setVisible(false)
        binding.stateIcon.setVisible(false)
    }
}