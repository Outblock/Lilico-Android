package io.outblock.lilico.page.token.addtoken.presenter

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
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.ActivityAddTokenBinding
import io.outblock.lilico.page.token.addtoken.AddTokenActivity
import io.outblock.lilico.page.token.addtoken.AddTokenViewModel
import io.outblock.lilico.page.token.addtoken.adapter.TokenListAdapter
import io.outblock.lilico.page.token.addtoken.model.AddTokenModel
import io.outblock.lilico.utils.extensions.*
import io.outblock.lilico.widgets.itemdecoration.ColorDividerItemDecoration

class AddTokenPresenter(
    private val activity: AddTokenActivity,
    private val binding: ActivityAddTokenBinding,
) : BasePresenter<AddTokenModel> {

    private val viewModel by lazy { ViewModelProvider(activity)[AddTokenViewModel::class.java] }
    private val adapter by lazy { TokenListAdapter() }

    init {
        setupToolbar()
        setupEditText()
        setupRecyclerView()
    }

    override fun bind(model: AddTokenModel) {
        model.data?.let { adapter.setNewDiffData(it) }
    }

    private fun setupRecyclerView() {
        with(binding.recyclerView) {
            adapter = this@AddTokenPresenter.adapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(
                ColorDividerItemDecoration(Color.TRANSPARENT, 12.dp2px().toInt())
            )
        }
    }

    private fun setupEditText() {
        with(binding.editText) {
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard()
                    viewModel.search(text.toString().trim())
                    clearFocus()
                }
                return@setOnEditorActionListener false
            }
            doOnTextChanged { text, _, _, _ ->
                viewModel.search(text.toString().trim())
            }
            onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus -> onSearchFocusChange(hasFocus) }
        }

        binding.cancelButton.setOnClickListener {
            onSearchFocusChange(false)
            binding.editText.hideKeyboard()
            binding.editText.setText("")
            binding.editText.clearFocus()
            viewModel.clearSearch()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.navigationIcon?.mutate()?.setTint(R.color.neutrals1.res2color())
        activity.setSupportActionBar(binding.toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setDisplayShowHomeEnabled(true)
        activity.title = R.string.add_token.res2String()
    }

    private fun onSearchFocusChange(hasFocus: Boolean) {
        val isVisible = hasFocus || !binding.editText.text.isNullOrBlank()
        val isVisibleChange = isVisible != binding.cancelButton.isVisible()

        if (isVisibleChange) {
            TransitionManager.go(Scene(binding.root as ViewGroup), Slide(Gravity.END).apply { duration = 150 })
            binding.cancelButton.setVisible(isVisible)
        }
    }
}