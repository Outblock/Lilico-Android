package io.outblock.lilico.page.walletcreate.fragments.mnemoniccheck

import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.FragmentWalletCreateMnemonicCheckBinding
import io.outblock.lilico.page.walletcreate.WALLET_CREATE_STEP_PIN_GUIDE
import io.outblock.lilico.page.walletcreate.WalletCreateViewModel
import io.outblock.lilico.utils.setBackupManually

class WalletCreateMnemonicCheckPresenter(
    private val fragment: Fragment,
    private val binding: FragmentWalletCreateMnemonicCheckBinding,
) : BasePresenter<WalletCreateMnemonicCheckModel> {

    private val pageViewModel by lazy { ViewModelProvider(fragment.requireActivity())[WalletCreateViewModel::class.java] }

    init {
        binding.nextButton.setOnClickListener {
            setBackupManually()
            pageViewModel.changeStep(WALLET_CREATE_STEP_PIN_GUIDE)
        }
    }

    override fun bind(model: WalletCreateMnemonicCheckModel) {
        model.questionList?.let { setupQuestion(it) }
    }

    private fun setupQuestion(questions: List<MnemonicQuestionModel>) {
        questions.forEach {
            val itemView = MnemonicQuestionView(fragment.requireContext())
            binding.mnemonicContainer.addView(itemView)
            itemView.bindData(it)
            itemView.setOnButtonCheckedListener { updateState() }
        }
    }

    private fun updateState() {
        binding.nextButton.isEnabled = isAllVerified()
    }

    private fun isAllVerified(): Boolean {
        binding.mnemonicContainer.children.forEach {
            val child = it as MnemonicQuestionView
            if (!child.isVerified()) {
                return false
            }
        }
        return true
    }
}