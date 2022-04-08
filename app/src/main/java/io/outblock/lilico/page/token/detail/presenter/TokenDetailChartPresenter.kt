package io.outblock.lilico.page.token.detail.presenter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.github.aachartmodel.aainfographics.aatools.AAGradientColor
import com.github.aachartmodel.aainfographics.aatools.AALinearGradientDirection
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.LayoutTokenDetailChartBinding
import io.outblock.lilico.network.model.CryptowatchSummaryResponse
import io.outblock.lilico.page.token.detail.Period
import io.outblock.lilico.page.token.detail.Quote
import io.outblock.lilico.page.token.detail.QuoteMarket
import io.outblock.lilico.page.token.detail.TokenDetailViewModel
import io.outblock.lilico.page.token.detail.model.TokenDetailChartModel
import io.outblock.lilico.page.token.detail.widget.TokenDetailMarketPopupMenu
import io.outblock.lilico.utils.extensions.res2color
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.extensions.toHexColorString
import io.outblock.lilico.utils.formatPrice
import io.outblock.lilico.utils.getQuoteMarket
import io.outblock.lilico.utils.uiScope
import kotlin.math.max

class TokenDetailChartPresenter(
    private val activity: AppCompatActivity,
    private val binding: LayoutTokenDetailChartBinding,
) : BasePresenter<TokenDetailChartModel> {
    private val viewModel by lazy { ViewModelProvider(activity)[TokenDetailViewModel::class.java] }
    private val chartColor by lazy { R.color.salmon_primary.res2color().toHexColorString(false) }
    private val transparentColor by lazy { R.color.background.res2color().toHexColorString(false) }

    init {
        setupChartView()
        binding.chartPeriodTabs.setOnTabClickListener { viewModel.changePeriod(it) }
        binding.chartPeriodTabs.selectTab(Period.DAY)
        binding.dataFrom.setOnClickListener { TokenDetailMarketPopupMenu(it) { market -> setupMarket(market) }.show() }
        setupMarket()
    }

    private fun setupMarket(market: String? = null) {
        uiScope {
            val market = (market ?: getQuoteMarket()).lowercase()
            var icon = R.drawable.ic_market_binance
            var name = R.string.market_binance
            when (market) {
                QuoteMarket.kraken.value.lowercase() -> {
                    icon = R.drawable.ic_market_kraken
                    name = R.string.market_kraken
                }
                QuoteMarket.huobi.value.lowercase() -> {
                    icon = R.drawable.ic_market_huobi
                    name = R.string.market_huobi
                }
            }
            binding.marketIcon.setImageResource(icon)
            binding.marketView.setText(name)
        }
    }

    override fun bind(model: TokenDetailChartModel) {
        model.chartData?.let { updateChartData(it) }
        model.summary?.let { updateSummary(it) }
    }

    private fun updateChartData(quotes: List<Quote>) {
        binding.chartView.chartView.aa_refreshChartWithChartOptions(chartModel(quotes).aa_toAAOptions())
//        binding.chartView.aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray(quotes.prepareChartData())
    }

    private fun List<Quote>.prepareChartData(): Array<Any> {
        return arrayOf(AASeriesElement().apply {
            data(this@prepareChartData.map { it.closePrice }.toTypedArray())
            showInLegend(false)
            borderColor(R.color.salmon1.res2color().toHexColorString(false))
            fillColor(AAGradientColor.linearGradient(AALinearGradientDirection.ToBottom, chartColor, transparentColor))
        })
    }

    @SuppressLint("SetTextI18n")
    private fun updateSummary(summary: CryptowatchSummaryResponse.Result) {
        with(binding) {
            priceView.text = "$${summary.price.last.formatPrice()}"

            val isRise = summary.price.change.percentage >= 0
            quoteChangeWrapper.setVisible()
            quoteChangeWrapper.backgroundTintList =
                ColorStateList.valueOf(if (isRise) R.color.quote_up_opacity.res2color() else R.color.quote_down_opacity.res2color())
            quoteChangeIconView.setImageResource(if (isRise) R.drawable.ic_triangle_up else R.drawable.ic_triangle_down)
            quoteChangeView.setTextColor(if (isRise) R.color.quote_up.res2color() else R.color.quote_down.res2color())
            quoteChangeView.text = "${summary.price.change.percentage.formatPrice(2)}%"
        }
    }

    private fun setupChartView() {
        val chartModel = chartModel()
        val aaOptions: AAOptions = chartModel.aa_toAAOptions()
        binding.chartView.chartView.aa_drawChartWithChartOptions(aaOptions)
    }

    private fun chartModel(quotes: List<Quote>? = null): AAChartModel {
        val minA = quotes?.minOf { it.closePrice } ?: 0f
        val maxA = quotes?.maxOf { it.closePrice }
        val min = max(0, (minA - minA / 6f).toInt())
        val max = if (maxA == null) maxA else (maxA + maxA / 6f).toInt()
        return AAChartModel.Builder(activity)
            .setChartType(AAChartType.Areaspline)
            .setXAxisVisible(false)
            .setYAxisVisible(true)
            .setBackgroundColor(R.color.background)
            .setDataLabelsEnabled(false)
            .setTitle("")
            .setAxesTextColor(R.color.neutrals8.res2color())
            .setYAxisTitle("")
//            .setYAxisMin(min.toFloat())
//            .setYAxisMax(max?.toFloat())
            .setAnimationDuration(200)
            .setAnimationType(AAChartAnimationType.EaseInCirc)
            .setYAxisLineWidth(0.1f)
            .setYAxisGridLineWidth(0f)
            .setGradientColorEnable(true)
            .setColorsTheme(arrayOf(AAGradientColor.linearGradient(AALinearGradientDirection.ToBottom, chartColor, transparentColor)))
            .setStacking(AAChartStackingType.False)
            .build().apply {
                series((quotes.orEmpty()).prepareChartData())
            }
    }
}