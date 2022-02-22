package io.outblock.lilico.page.send.subpage.amount.presenter

import android.annotation.SuppressLint
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.databinding.ActivitySendAmountBinding
import io.outblock.lilico.network.model.AddressBookContact
import io.outblock.lilico.page.address.model.AddressBookPersonModel
import io.outblock.lilico.page.address.presenter.AddressBookPersonPresenter
import io.outblock.lilico.page.send.subpage.amount.SendAmountActivity
import io.outblock.lilico.page.send.subpage.amount.SendAmountViewModel
import io.outblock.lilico.page.send.subpage.amount.model.SendAmountModel
import io.outblock.lilico.page.send.subpage.amount.model.SendBalanceModel
import io.outblock.lilico.page.send.subpage.amount.model.TransactionModel
import io.outblock.lilico.page.send.subpage.transaction.TransactionDialog
import io.outblock.lilico.utils.extensions.*
import io.outblock.lilico.utils.formatBalance
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.uiScope
import wallet.core.jni.CoinType

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
            doOnTextChanged { _, _, _, _ ->
                updateTransferAmountConvert()
                binding.nextButton.isEnabled = verifyAmount()
            }
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    hideKeyboard()
                    if (verifyAmount()) showSendDialog()
                }
                return@setOnEditorActionListener false
            }
        }
        binding.nextButton.setOnClickListener { showSendDialog() }
        binding.cancelButton.setOnClickListener { activity.finish() }
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
            transferAmountInput.text = transferAmountInput.text
            updateTransferAmountConvert()
        }
    }

    private fun updateTransferAmountConvert() {
        binding.convertAmountView.text = activity.getString(
            R.string.coin_rate_usd_convert,
            binding.transferAmountInput.text.ifBlank { "0" }.toString().toSafeFloat() * (balance()?.coinRate ?: 0f)
        )
    }

    private fun showSendDialog() {
        ioScope {
            val wallet = walletCache().read() ?: return@ioScope
            uiScope {
                TransactionDialog(
                    activity, TransactionModel(
                        amount = binding.transferAmountInput.text.toString().toSafeFloat(),
                        coinType = CoinType.FLOW.value(),
                        target = viewModel.contact(),
                        fromAddress = wallet.wallets?.first()?.blockchain?.first()?.address.orEmpty(),
                    )
                ).show()
            }
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

    private fun setupToolbar() {
        binding.toolbar.navigationIcon?.mutate()?.setTint(R.color.neutrals1.res2color())
        activity.setSupportActionBar(binding.toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setDisplayShowHomeEnabled(true)
        activity.title = R.string.send_to.res2String()
    }

    private fun verifyAmount(): Boolean {
        val number = binding.transferAmountInput.text.toString().toFloatOrNull()
        return number != null && number > 0
    }
}