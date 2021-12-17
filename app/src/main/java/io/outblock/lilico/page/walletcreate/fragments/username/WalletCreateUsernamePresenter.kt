package io.outblock.lilico.page.walletcreate.fragments.username

import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.FragmentWalletCreateUsernameBinding
import io.outblock.lilico.utils.extensions.res2color
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.listeners.SimpleTextWatcher

class WalletCreateUsernamePresenter(
    private val fragment: Fragment,
    private val binding: FragmentWalletCreateUsernameBinding,
) : BasePresenter<WalletCreateUsernameModel> {

    private val viewModel by lazy { ViewModelProvider(fragment)[WalletCreateUsernameViewModel::class.java] }

    private val usernameCheckTask = Runnable { viewModel.verifyUsername(binding.editText.text.toString()) }

    private val handler = Handler(Looper.getMainLooper())

    init {
        binding.editText.addTextChangedListener(object : SimpleTextWatcher() {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                hideStateView()
                binding.nextButton.isEnabled = false
                handler.removeCallbacks(usernameCheckTask)
                handler.postDelayed(usernameCheckTask, 1000)
            }
        })
        with(binding.root) {
            post { layoutParams.height = height }
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