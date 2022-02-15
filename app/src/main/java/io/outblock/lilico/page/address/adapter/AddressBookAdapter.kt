package io.outblock.lilico.page.address.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.outblock.lilico.R
import io.outblock.lilico.base.recyclerview.BaseAdapter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.page.address.addressBookDiffCallback
import io.outblock.lilico.page.address.model.AddressBookCharModel
import io.outblock.lilico.page.address.model.AddressBookPersonModel
import io.outblock.lilico.page.address.presenter.AddressBookCharPresenter
import io.outblock.lilico.page.address.presenter.AddressBookPersonPresenter

class AddressBookAdapter : BaseAdapter<Any>(addressBookDiffCallback) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is AddressBookCharModel -> TYPE_CHAR
            else -> TYPE_PERSON
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_CHAR -> AddressBookCharPresenter(parent.inflate(R.layout.item_address_book_char))
            TYPE_PERSON -> AddressBookPersonPresenter(parent.inflate(R.layout.item_address_book_person))
            else -> BaseViewHolder(View(parent.context))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AddressBookCharPresenter -> holder.bind(getItem(position) as AddressBookCharModel)
            is AddressBookPersonPresenter -> holder.bind(getItem(position) as AddressBookPersonModel)
        }
    }

    companion object {
        private const val TYPE_CHAR = 1
        private const val TYPE_PERSON = 2
    }
}