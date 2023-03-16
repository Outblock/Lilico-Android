package io.outblock.lilico.page.token.detail.widget

import android.annotation.SuppressLint
import android.content.Context
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import io.outblock.lilico.R
import io.outblock.lilico.databinding.LayoutChartMarkerBinding
import io.outblock.lilico.utils.formatDate
import io.outblock.lilico.utils.formatPrice

class ChartMarker(context: Context) : MarkerView(context, R.layout.layout_chart_marker) {

    private var binding: LayoutChartMarkerBinding = LayoutChartMarkerBinding.bind(getChildAt(0))

    @SuppressLint("SetTextI18n")
    override fun refreshContent(entry: Entry?, highlight: Highlight?) {
        super.refreshContent(entry, highlight)
        entry ?: return
        with(binding) {
            priceView.text = entry.y.formatPrice(convertCurrency = false, includeSymbol = true)
            dateView.text = (entry.x * 1000).toLong().formatDate()
        }
    }
}