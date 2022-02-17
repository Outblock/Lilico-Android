package io.outblock.lilico.page.address.presenter

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.FragmentAddressBookBinding
import io.outblock.lilico.page.address.AddressBookFragment
import io.outblock.lilico.page.address.AddressBookViewModel
import io.outblock.lilico.page.address.adapter.AddressBookAdapter
import io.outblock.lilico.page.address.model.AddressBookFragmentModel
import io.outblock.lilico.utils.extensions.dp2px
import io.outblock.lilico.utils.extensions.res2color
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.widgets.itemdecoration.ColorDividerItemDecoration

class AddressBookFragmentPresenter(
    private val fragment: AddressBookFragment,
    private val binding: FragmentAddressBookBinding,
) : BasePresenter<AddressBookFragmentModel> {

    private val adapter by lazy { AddressBookAdapter() }

    private val activity = fragment.requireActivity()

    private val viewModel by lazy { ViewModelProvider(fragment.requireActivity())[AddressBookViewModel::class.java] }

    init {
        with(binding.recyclerView) {
            adapter = this@AddressBookFragmentPresenter.adapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(
                ColorDividerItemDecoration(Color.TRANSPARENT, 4.dp2px().toInt())
            )
        }

        binding.localEmptyWrapper.setOnClickListener {
            viewModel.searchRemote(viewModel.searchKeyword())
            viewModel.clearInputFocus()
            binding.localEmptyWrapper.setVisible(false)
        }
    }

    override fun bind(model: AddressBookFragmentModel) {
        model.data?.let {
            adapter.setNewDiffData(it)
            updateLocalEmptyState(false)
            updateRemoteEmptyState(false)
        }
        model.isRemoteEmpty?.let { updateRemoteEmptyState(it) }
        model.isLocalEmpty?.let { updateLocalEmptyState(it) }
        model.isSearchStart?.let { binding.progressBar.setVisible(it) }
    }

    private fun updateLocalEmptyState(isEmpty: Boolean) {
        binding.localEmptyWrapper.setVisible(isEmpty)
        if (isEmpty) {
            binding.localEmptyTextView.text = SpannableString(activity.getString(R.string.search_the_id, viewModel.searchKeyword())).apply {
                val keyword = viewModel.searchKeyword()
                val index = lastIndexOf(keyword)
                setSpan(ForegroundColorSpan(R.color.colorSecondary.res2color()), index, index + keyword.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        updateRemoteEmptyState(false)
    }

    private fun updateRemoteEmptyState(isEmpty: Boolean) {
        binding.emptyTipWrapper.setVisible(isEmpty)
        binding.progressBar.setVisible(false)
    }
}