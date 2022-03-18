package io.outblock.lilico.page.receive.presenter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.ActivityReceiveBinding
import io.outblock.lilico.page.receive.ReceiveActivity
import io.outblock.lilico.page.receive.model.ReceiveData
import io.outblock.lilico.page.receive.model.ReceiveModel
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.extensions.res2color
import io.outblock.lilico.utils.textToClipboard
import io.outblock.lilico.utils.toast
import io.outblock.lilico.wallet.toAddress

class ReceivePresenter(
    private val activity: ReceiveActivity,
    private val binding: ActivityReceiveBinding,
) : BasePresenter<ReceiveModel> {

    init {
        setupToolbar()
    }

    override fun bind(model: ReceiveModel) {
        model.data?.let { updateWallet(it) }
        model.qrcode?.let { updateQrcode(it) }
    }

    @SuppressLint("SetTextI18n")
    private fun updateWallet(data: ReceiveData) {
        fun copy() {
            textToClipboard(data.address)
            toast(msgRes = R.string.copy_address_toast)
        }

        with(binding) {
            walletNameView.text = data.walletName.ifBlank { R.string.wallet.res2String() }
            walletAddressView.text = "(${data.address.toAddress()})"
            copyButton.setOnClickListener { copy() }
            copyDataButton.setOnClickListener { copy() }
        }
    }

    private fun updateQrcode(qrcode: Bitmap) {
        binding.qrcodeImageView.setImageBitmap(qrcode)
    }

    private fun setupToolbar() {
        binding.toolbar.navigationIcon?.mutate()?.setTint(R.color.neutrals1.res2color())
        activity.setSupportActionBar(binding.toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setDisplayShowHomeEnabled(true)
        activity.title = R.string.receive.res2String()
    }
}