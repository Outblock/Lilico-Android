package io.outblock.lilico.page.wallet.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.databinding.DialogSwapCoinListBinding
import io.outblock.lilico.manager.app.isTestnet
import io.outblock.lilico.network.functions.FUNCTION_MOON_PAY_SIGN
import io.outblock.lilico.network.functions.executeHttpFunction
import io.outblock.lilico.page.browser.openBrowser
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.viewModelIOScope
import java.net.URLEncoder

class SwapDialog : BottomSheetDialogFragment() {

    private lateinit var binding: DialogSwapCoinListBinding

    private lateinit var viewModel: SwapViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogSwapCoinListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.closeButton.setOnClickListener { dismiss() }
        viewModel = ViewModelProvider(this)[SwapViewModel::class.java].apply { load() }

        with(binding) {
            moonpayButton.setOnClickListener { openUrl(viewModel.moonPayUrl) }
            coinbaseButton.setOnClickListener { openUrl(coinBaseUrl()) }
        }
    }

    private fun openUrl(url: String?) {
        url ?: return
        openBrowser(requireActivity(), url)
        dismiss()
    }

    private fun coinBaseUrl(): String {
        // https://pay.coinbase.com/buy/input?appId=d22a56bd-68b7-4321-9b25-aa357fc7f9ce&destinationWallets=[{"address":"0x7d2b880d506db7cc","blockchains":["flow"]}]
        val json = """[{"address":"${viewModel.address}","blockchains":["flow"]}]"""
        return "https://pay.coinbase.com/buy/input?appId=d22a56bd-68b7-4321-9b25-aa357fc7f9ce&destinationWallets=${URLEncoder.encode(json, "UTF-8")}"
    }

    companion object {

        fun show(fragmentManager: FragmentManager) {
            SwapDialog().show(fragmentManager, "")
        }
    }
}

private val TAG = SwapViewModel::class.java.simpleName

internal class SwapViewModel : ViewModel() {

    lateinit var address: String

    var moonPayUrl: String? = null

    private val moonPayApiKey = if (isTestnet()) "pk_test_F0Y1SznEgbvGOWxFYJqStfjLeZ7XT" else "pk_test_F0Y1SznEgbvGOWxFYJqStfjLeZ7XT"
    private val moonPayHost = if (isTestnet()) "https://buy-sandbox.moonpay.com" else "https://buy-sandbox.moonpay.com"

    fun load() {
        viewModelIOScope(this) {
            address = walletCache().read()?.primaryWalletAddress()!!
            val response = executeHttpFunction(FUNCTION_MOON_PAY_SIGN, """{"url":"${buildMoonPayUrl()}"}""")
            logd(TAG, "moon pay response:$response")
            moonPayUrl = Gson().fromJson(response, MoonPaySignResponse::class.java).data?.url
            logd(TAG, "moon pay url:${moonPayUrl}")
        }
    }

    private fun buildMoonPayUrl(): String {
        // https://buy-sandbox.moonpay.com?apiKey=pk_test_F0Y1SznEgbvGOWxFYJqStfjLeZ7XT&defaultCurrencyCode=FLOW&colorCode=%23FC814A&walletAddress=0x9f871c373ff892c0
        return "${moonPayHost}?apiKey=$moonPayApiKey&defaultCurrencyCode=FLOW&colorCode=%23FC814A&walletAddress=$address"
    }

}

data class MoonPaySignResponse(
    @SerializedName("data")
    val data: Data?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Int?
) {
    data class Data(
        @SerializedName("url")
        val url: String?
    )
}