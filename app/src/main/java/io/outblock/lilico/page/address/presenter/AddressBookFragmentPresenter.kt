package io.outblock.lilico.page.address.presenter

import android.graphics.Color
import androidx.recyclerview.widget.LinearLayoutManager
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.FragmentAddressBookBinding
import io.outblock.lilico.page.address.AddressBookFragment
import io.outblock.lilico.page.address.adapter.AddressBookAdapter
import io.outblock.lilico.page.address.model.AddressBookFragmentModel
import io.outblock.lilico.utils.extensions.dp2px
import io.outblock.lilico.widgets.itemdecoration.ColorDividerItemDecoration

class AddressBookFragmentPresenter(
    private val fragment: AddressBookFragment,
    private val binding: FragmentAddressBookBinding,
) : BasePresenter<AddressBookFragmentModel> {

    private val adapter by lazy { AddressBookAdapter() }

    init {
        with(binding.recyclerView) {
            adapter = this@AddressBookFragmentPresenter.adapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(
                ColorDividerItemDecoration(Color.TRANSPARENT, 4.dp2px().toInt())
            )
        }
    }

    override fun bind(model: AddressBookFragmentModel) {
        model.data?.let { adapter.setNewDiffData(it) }
        model.isEmpty?.let { updateEmptyState(it) }
    }

    private fun updateEmptyState(isEmpty: Boolean) {

    }
}