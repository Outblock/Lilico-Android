package io.outblock.lilico.page.walletcreate.fragments.mnemoniccheck

import androidx.core.view.children
import androidx.fragment.app.Fragment
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.FragmentWalletCreateMnemonicCheckBinding
import io.outblock.lilico.page.main.MainActivity

class WalletCreateMnemonicCheckPresenter(
    private val fragment: Fragment,
    private val binding: FragmentWalletCreateMnemonicCheckBinding,
) : BasePresenter<WalletCreateMnemonicCheckModel> {

    init {
        binding.nextButton.setOnClickListener { MainActivity.launch(fragment.requireContext()) }
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