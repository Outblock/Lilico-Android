package io.outblock.lilico.page.profile.subpage.developer.presenter

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.ActivityDeveloperModeSettingBinding
import io.outblock.lilico.manager.app.isMainnet
import io.outblock.lilico.manager.app.isTestnet
import io.outblock.lilico.manager.app.refreshChainNetwork
import io.outblock.lilico.page.profile.subpage.developer.DeveloperModeViewModel
import io.outblock.lilico.page.profile.subpage.developer.model.DeveloperPageModel
import io.outblock.lilico.utils.*
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.widgets.ProgressDialog

class DeveloperModePresenter(
    private val activity: FragmentActivity,
    private val binding: ActivityDeveloperModeSettingBinding,
) : BasePresenter<DeveloperPageModel> {

    private val progressDialog by lazy { ProgressDialog(activity) }

    private val viewModel by lazy { ViewModelProvider(activity)[DeveloperModeViewModel::class.java] }

    init {
        uiScope {
            with(binding) {
                val isDeveloperModeEnable = isDeveloperModeEnable()
                developerModePreference.setChecked(isDeveloperModeEnable)

                group2.setVisible(isDeveloperModeEnable)
                mainnetPreference.setChecked(isMainnet())
                testnetPreference.setChecked(isTestnet())

                mainnetPreference.setOnCheckedChangeListener {
                    testnetPreference.setChecked(!it)
                    changeNetwork(if (it) NETWORK_MAINNET else NETWORK_TESTNET)
                }

                testnetPreference.setOnCheckedChangeListener {
                    mainnetPreference.setChecked(!it)
                    changeNetwork(if (it) NETWORK_TESTNET else NETWORK_MAINNET)
                }

                developerModePreference.setOnCheckedChangeListener {
                    group2.setVisible(it)
                    setDeveloperModeEnable(it)
                    if (!it) {
                        changeNetwork(NETWORK_MAINNET)
                    }
                }
            }
        }
    }

    override fun bind(model: DeveloperPageModel) {
        model.progressDialogVisible?.let { if (it) progressDialog.show() else progressDialog.dismiss() }
        model.result?.let {
            if (!it) {
                toast(msgRes = R.string.switch_network_failed)
                with(binding) {
                    mainnetPreference.setChecked(isTestnet())
                    testnetPreference.setChecked(isMainnet())
                }
            }
        }
    }

    private fun changeNetwork(network: Int) {
        updateChainNetworkPreference(network) {
            refreshChainNetwork {
                viewModel.changeNetwork()
                binding.mainnetPreference.setChecked(isMainnet())
                binding.testnetPreference.setChecked(isTestnet())
            }
        }
    }
}