package io.outblock.lilico.page.main.presenter

import android.content.Context
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.journeyapps.barcodescanner.ScanOptions
import io.outblock.lilico.R
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.cache.userInfoCache
import io.outblock.lilico.cache.walletCache
import io.outblock.lilico.databinding.LayoutMainDrawerLayoutBinding
import io.outblock.lilico.page.main.MainActivityViewModel
import io.outblock.lilico.page.main.model.MainDrawerLayoutModel
import io.outblock.lilico.page.nft.nftlist.utils.NftCache
import io.outblock.lilico.page.profile.subpage.wallet.WalletSettingActivity
import io.outblock.lilico.page.scan.dispatchScanResult
import io.outblock.lilico.utils.*
import org.joda.time.format.ISODateTimeFormat

class DrawerLayoutPresenter(
    private val drawer: DrawerLayout,
    private val binding: LayoutMainDrawerLayoutBinding,
) : BasePresenter<MainDrawerLayoutModel> {

    private lateinit var barcodeLauncher: ActivityResultLauncher<ScanOptions>

    private val activity by lazy { findActivity(drawer) as FragmentActivity }

    init {
        drawer.addDrawerListener(DrawerListener())

        with(binding.root.layoutParams) {
            width = (ScreenUtils.getScreenWidth() * 0.8f).toInt()
            binding.root.layoutParams = this
        }

        with(binding) {
            scanItem.setOnClickListener { launchClick { barcodeLauncher.launch() } }
            importWalletItem.setOnClickListener { }
            createWalletItem.setOnClickListener { }
            walletItem.setOnClickListener { launchClick { WalletSettingActivity.launch(binding.root.context) } }
        }
        bindData()

        barcodeLauncher = activity.registerBarcodeLauncher { result -> dispatchScanResult(activity, result.orEmpty()) }
    }

    override fun bind(model: MainDrawerLayoutModel) {
        model.refreshData?.let { bindData() }
        model.openDrawer?.let { drawer.open() }
    }

    private fun bindData() {
        ioScope {
            val address = walletCache().read()?.primaryWalletAddress()
            drawer.setDrawerLockMode(if (address.isNullOrBlank()) DrawerLayout.LOCK_MODE_LOCKED_CLOSED else DrawerLayout.LOCK_MODE_UNLOCKED)
            address ?: return@ioScope

            val userInfo = userInfoCache().read() ?: return@ioScope
            val nftCount = NftCache(address).grid().read()?.count ?: 0
            val createTime = ISODateTimeFormat.dateTimeParser().parseDateTime(userInfo.created).toString("yyyy")
            uiScope {
                with(binding) {
                    walletAddressView.text = address
                    avatarView.loadAvatar(userInfo.avatar)
                    nickNameView.text = userInfo.nickname
                    descView.text = activity.getString(R.string.drawer_desc, createTime, nftCount)
                }
            }
        }
    }

    private fun launchClick(unit: () -> Unit) {
        unit.invoke()
        drawer.close()
    }

    private inner class DrawerListener : DrawerLayout.SimpleDrawerListener() {
        override fun onDrawerOpened(drawerView: View) {
            super.onDrawerOpened(drawerView)
            bindData()
        }
    }
}

fun openDrawerLayout(context: Context) {
    val activity = context as? FragmentActivity ?: return
    val viewModel = ViewModelProvider(activity)[MainActivityViewModel::class.java]
    viewModel.openDrawerLayout()
}