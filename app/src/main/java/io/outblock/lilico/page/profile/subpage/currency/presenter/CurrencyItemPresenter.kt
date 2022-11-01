package io.outblock.lilico.page.profile.subpage.currency.presenter

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.databinding.ItemCurrencyListBinding
import io.outblock.lilico.page.profile.subpage.currency.CurrencyViewModel
import io.outblock.lilico.page.profile.subpage.currency.model.CurrencyItemModel
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.findActivity

class CurrencyItemPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<CurrencyItemModel> {

    private val viewModel by lazy { ViewModelProvider(findActivity(view) as FragmentActivity)[CurrencyViewModel::class.java] }

    private val binding by lazy { ItemCurrencyListBinding.bind(view) }

    override fun bind(model: CurrencyItemModel) {
        with(binding) {
            val currency = model.currency
            nameView.text = currency.name
            symbolView.text = currency.symbol
            iconView.setImageResource(currency.icon)
            stateButton.setVisible(model.isSelected)

            view.setOnClickListener { viewModel.updateFlag(currency.flag) }
        }
    }
}