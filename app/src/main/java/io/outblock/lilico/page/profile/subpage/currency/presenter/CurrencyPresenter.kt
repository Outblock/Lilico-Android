package io.outblock.lilico.page.profile.subpage.currency.presenter

import android.graphics.Color
import android.transition.Scene
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.zackratos.ultimatebarx.ultimatebarx.addStatusBarTopPadding
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.ActivityAddTokenBinding
import io.outblock.lilico.databinding.ActivitySettingsCurrencyBinding
import io.outblock.lilico.page.profile.subpage.currency.CurrencyListActivity
import io.outblock.lilico.page.profile.subpage.currency.CurrencyViewModel
import io.outblock.lilico.page.profile.subpage.currency.adapter.CurrencyListAdapter
import io.outblock.lilico.page.profile.subpage.currency.model.CurrencyModel
import io.outblock.lilico.page.token.addtoken.AddTokenActivity
import io.outblock.lilico.page.token.addtoken.AddTokenViewModel
import io.outblock.lilico.page.token.addtoken.adapter.TokenListAdapter
import io.outblock.lilico.page.token.addtoken.model.AddTokenModel
import io.outblock.lilico.utils.extensions.*
import io.outblock.lilico.widgets.itemdecoration.ColorDividerItemDecoration

class CurrencyPresenter(
    private val activity: CurrencyListActivity,
    private val binding: ActivitySettingsCurrencyBinding,
) : BasePresenter<CurrencyModel> {

    private val adapter by lazy { CurrencyListAdapter() }

    init {
        binding.root.addStatusBarTopPadding()
        setupRecyclerView()
    }

    override fun bind(model: CurrencyModel) {
        model.data?.let { adapter.setNewDiffData(it) }
    }

    private fun setupRecyclerView() {
        with(binding.recyclerView) {
            adapter = this@CurrencyPresenter.adapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }
}