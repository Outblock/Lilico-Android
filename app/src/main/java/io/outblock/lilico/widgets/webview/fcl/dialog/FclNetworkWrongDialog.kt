package io.outblock.lilico.widgets.webview.fcl.dialog

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.outblock.lilico.R
import io.outblock.lilico.databinding.DialogFclWrongNetworkBinding
import io.outblock.lilico.manager.account.AccountManager
import io.outblock.lilico.manager.app.chainNetWorkString
import io.outblock.lilico.manager.app.doNetworkChangeTask
import io.outblock.lilico.manager.app.networkId
import io.outblock.lilico.manager.app.refreshChainNetworkSync
import io.outblock.lilico.manager.flowjvm.FlowApi
import io.outblock.lilico.manager.wallet.WalletManager
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.retrofit
import io.outblock.lilico.page.main.MainActivity
import io.outblock.lilico.utils.NETWORK_MAINNET
import io.outblock.lilico.utils.extensions.capitalizeV2
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.loge
import io.outblock.lilico.utils.uiScope
import io.outblock.lilico.utils.updateChainNetworkPreference
import io.outblock.lilico.widgets.ProgressDialog
import io.outblock.lilico.widgets.webview.fcl.model.FclDialogModel
import kotlinx.coroutines.delay

class FclNetworkWrongDialog : BottomSheetDialogFragment() {

    private val data by lazy { arguments?.getParcelable<FclDialogModel>(EXTRA_DATA) }

    private lateinit var binding: DialogFclWrongNetworkBinding

    private val progressDialog by lazy { ProgressDialog(requireContext()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogFclWrongNetworkBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        data ?: return
        val data = data ?: return
        with(binding) {
            val descSb = SpannableStringBuilder(getString(R.string.network_error_dialog_desc, data.network, chainNetWorkString()))
            val index1 = descSb.indexOf(data.network.orEmpty())
            descSb.setSpan(StyleSpan(Typeface.BOLD), index1, index1 + (data.network?.length ?: 0), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            val index2 = descSb.indexOf(chainNetWorkString())
            descSb.setSpan(StyleSpan(Typeface.BOLD), index2, index2 + (chainNetWorkString().length), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            descView.text = descSb

            fromNetworkNameView.text = data.network?.capitalizeV2()
            toNetworkNameView.text = chainNetWorkString().capitalizeV2()
            val isFromMainnet = networkId(data.network.orEmpty().lowercase()) == NETWORK_MAINNET
            fromNetworkIcon.setImageResource(if (isFromMainnet) R.drawable.ic_network_mainnet else R.drawable.ic_network_testnet)
            toNetworkIcon.setImageResource(if (isFromMainnet) R.drawable.ic_network_testnet else R.drawable.ic_network_mainnet)

            cancelButton.setOnClickListener {
                dismiss()
            }
            approveButton.setOnClickListener {
                changeNetwork(networkId(data.network!!.lowercase()))
            }
        }
    }

    private fun changeNetwork(network: Int) {
        updateChainNetworkPreference(network) {
            ioScope {
                delay(200)
                refreshChainNetworkSync()
                doNetworkChangeTask()
                uiScope {
                    delay(200)
                    changeNetworkFetchServer()
                }
            }
        }
    }

    private fun changeNetworkFetchServer() {
        ioScope {
            FlowApi.refreshConfig()
            val cacheExist = WalletManager.wallet() != null && !WalletManager.wallet()?.walletAddress().isNullOrBlank()
            if (!cacheExist) {
                uiScope { progressDialog.show() }
                try {
                    val service = retrofit().create(ApiService::class.java)
                    val resp = service.getWalletList()

                    // request success & wallet list is empty (wallet not create finish)
                    if (!resp.data!!.wallets.isNullOrEmpty()) {
                        AccountManager.updateWalletInfo(resp.data)
                    }
                } catch (e: Exception) {
                    loge(e)
                }
                uiScope { progressDialog.dismiss() }
            }
            uiScope {
                MainActivity.relaunch(requireContext(), clearTop = true)
                dismiss()
            }
        }
    }

    companion object {
        private const val EXTRA_DATA = "extra_data"

        fun show(fragmentManager: FragmentManager, data: FclDialogModel) {
            FclNetworkWrongDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(EXTRA_DATA, data)
                }
            }.show(fragmentManager, "")
        }
    }
}


fun checkAndShowNetworkWrongDialog(
    fragmentManager: FragmentManager,
    data: FclDialogModel,
): Boolean {
    return if (data.network != null && data.network.lowercase() != chainNetWorkString()) {
        FclNetworkWrongDialog.show(fragmentManager, data)
        true
    } else false
}