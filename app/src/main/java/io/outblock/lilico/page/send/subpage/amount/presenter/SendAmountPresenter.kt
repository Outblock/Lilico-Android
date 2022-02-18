package io.outblock.lilico.page.send.subpage.amount.presenter

import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.ActivitySendAmountBinding
import io.outblock.lilico.network.model.AddressBookContact
import io.outblock.lilico.page.address.model.AddressBookPersonModel
import io.outblock.lilico.page.address.presenter.AddressBookPersonPresenter
import io.outblock.lilico.page.send.subpage.amount.SendAmountActivity
import io.outblock.lilico.page.send.subpage.amount.model.SendAmountModel
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.extensions.res2color
import io.outblock.lilico.utils.extensions.setVisible

class SendAmountPresenter(
    private val activity: SendAmountActivity,
    private val binding: ActivitySendAmountBinding,
    private val contact: AddressBookContact,
) : BasePresenter<SendAmountModel> {

    init {
        setupToolbar()
        setupContactCard()
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
    }

    private fun setupToolbar() {
        binding.toolbar.navigationIcon?.mutate()?.setTint(R.color.neutrals1.res2color())
        activity.setSupportActionBar(binding.toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setDisplayShowHomeEnabled(true)
        activity.title = R.string.send_to.res2String()
    }
}