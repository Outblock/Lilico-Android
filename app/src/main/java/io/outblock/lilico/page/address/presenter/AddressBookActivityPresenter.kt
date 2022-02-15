package io.outblock.lilico.page.address.presenter

import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.ActivityAddressBookBinding
import io.outblock.lilico.page.address.AddressBookActivity
import io.outblock.lilico.page.address.model.AddressBookActivityModel
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.extensions.res2color

class AddressBookActivityPresenter(
    private val activity: AddressBookActivity,
    private val binding: ActivityAddressBookBinding,
) : BasePresenter<AddressBookActivityModel> {

    init {
        setupToolbar()
    }

    override fun bind(model: AddressBookActivityModel) {

    }

    private fun setupToolbar() {
        binding.toolbar.navigationIcon?.mutate()?.setTint(R.color.neutrals1.res2color())
        activity.setSupportActionBar(binding.toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setDisplayShowHomeEnabled(true)
        activity.title = R.string.address_book.res2String()
    }
}