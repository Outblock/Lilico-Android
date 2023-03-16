package io.outblock.lilico.page.browser.presenter

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.base.recyclerview.BaseViewHolder
import io.outblock.lilico.page.browser.model.RecommendModel
import io.outblock.lilico.page.browser.toSearchUrl
import io.outblock.lilico.utils.extensions.res2color

class BrowserRecommendWordPresenter(
    private val view: View,
) : BaseViewHolder(view), BasePresenter<RecommendModel> {

    private val textView by lazy { view.findViewById<TextView>(R.id.text_view) }

    override fun bind(model: RecommendModel) {
        val text = SpannableString(model.text).apply {
            val index = indexOf(model.query)
            if (index >= 0) {
                setSpan(
                    ForegroundColorSpan(R.color.note.res2color()),
                    index,
                    index + model.query.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
        textView.text = text
        view.setOnClickListener { model.viewModel.updateUrl(model.text.toSearchUrl()) }
    }
}