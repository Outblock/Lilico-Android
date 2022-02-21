package io.outblock.lilico.page.send.subpage.amount.presenter

import android.annotation.SuppressLint
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.ActivitySendAmountBinding
import io.outblock.lilico.network.model.AddressBookContact
import io.outblock.lilico.page.address.model.AddressBookPersonModel
import io.outblock.lilico.page.address.presenter.AddressBookPersonPresenter
import io.outblock.lilico.page.send.subpage.amount.SendAmountActivity
import io.outblock.lilico.page.send.subpage.amount.SendAmountViewModel
import io.outblock.lilico.page.send.subpage.amount.model.SendAmountModel
import io.outblock.lilico.page.send.subpage.amount.model.SendBalanceModel
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.extensions.res2color
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.extensions.toSafeFloat
import io.outblock.lilico.utils.formatBalance

class SendAmountPresenter(
    private val activity: SendAmountActivity,
    private val binding: ActivitySendAmountBinding,
    private val contact: AddressBookContact,
) : BasePresenter<SendAmountModel> {

    private val viewModel by lazy { ViewModelProvider(activity)[SendAmountViewModel::class.java] }

    private fun balance() = viewModel.balanceLiveData.value

    init {
        setupToolbar()
        setupContactCard()
        with(binding.transferAmountInput) {
            doOnTextChanged { _, _, _, _ -> updateTransferAmountConvert() }
        }
    }

    private fun setupContactCard() {
        with(binding.contactCard.root) {
            AddressBookPersonPresenter(this).bind(AddressBookPersonModel(data = contact))
            setPadding(0, paddingTop, 0, paddingBottom)
            isClickable = false
        }
        binding.contactCard.addButton.setVisible(false)
        binding.closeButton.setOnClickListener { activity.finish() }
    }

    override fun bind(model: SendAmountModel) {
        model.balance?.let { updateBalance(it) }
    }

    @SuppressLint("SetTextI18n")
    private fun updateBalance(balance: SendBalanceModel) {
        with(binding) {
            balanceAmountView.text = "${balance.balance.formatBalance()} Flow "
            balanceAmountConvertView.text =
                activity.getString(
                    R.string.coin_rate_usd_convert,
                    if (balance.coinRate > 0) balance.coinRate * balance.balance.formatBalance() else 0f
                )
            transferAmountInput.setText(transferAmountInput.text.ifBlank { "0" })
            updateTransferAmountConvert()
        }
    }

    private fun updateTransferAmountConvert() {
        binding.convertAmountView.text = activity.getString(
            R.string.coin_rate_usd_convert,
            binding.transferAmountInput.text.ifBlank { "0" }.toString().toSafeFloat() * (balance()?.coinRate ?: 0f)
        )
    }

    private fun setupToolbar() {
        binding.toolbar.navigationIcon?.mutate()?.setTint(R.color.neutrals1.res2color())
        activity.setSupportActionBar(binding.toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setDisplayShowHomeEnabled(true)
        activity.title = R.string.send_to.res2String()
    }
}