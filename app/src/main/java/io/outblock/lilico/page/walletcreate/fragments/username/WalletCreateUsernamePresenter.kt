package io.outblock.lilico.page.walletcreate.fragments.username

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.constraintlayout.widget.ConstraintLayout
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

    private val rootView by lazy { fragment.requireActivity().findViewById<View>(R.id.rootView) }

    private val keyboardObserver by lazy { keyboardObserver() }

    init {
        binding.editText.addTextChangedListener(object : SimpleTextWatcher() {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                hideStateView()
                binding.progressBar.setVisible(true)
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
        observeKeyboardVisible()
    }

    override fun bind(model: WalletCreateUsernameModel) {
        model.state?.let { updateState(it) }
    }

    fun unbind() {
        with(rootView.viewTreeObserver) {
            if (isAlive) {
                removeOnGlobalLayoutListener(keyboardObserver)
            }
        }
    }

    private fun updateState(state: Pair<Boolean, String>) {
        if (state.second.isEmpty()) {
            hideStateView()
            return
        }
        binding.progressBar.setVisible(false)
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

    private fun observeKeyboardVisible() {
        rootView.post { rootView.viewTreeObserver.addOnGlobalLayoutListener(keyboardObserver) }
    }

    private fun keyboardObserver(): ViewTreeObserver.OnGlobalLayoutListener {
        return ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val contentHeight = rootView.rootView.height

            val isKeyboardVisible = contentHeight - rect.bottom > contentHeight * 0.15f
            with(binding.placeholder.layoutParams as ConstraintLayout.LayoutParams) {
                dimensionRatio = if (isKeyboardVisible) "16:5" else "16:9"
                binding.placeholder.layoutParams = this
            }
            with(binding.contentWrapper.layoutParams as ViewGroup.MarginLayoutParams) {
                bottomMargin = if (isKeyboardVisible) contentHeight - rect.bottom else 0
                binding.contentWrapper.layoutParams = this
            }
        }
    }
}