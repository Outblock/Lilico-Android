package io.outblock.lilico.page.dialog.linkaccount

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.outblock.lilico.cache.userInfoCache
import io.outblock.lilico.databinding.DialogLinkAccountBinding
import io.outblock.lilico.page.browser.loadFavicon
import io.outblock.lilico.page.browser.toFavIcon
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.loadAvatar
import io.outblock.lilico.utils.uiScope
import io.outblock.lilico.widgets.webview.fcl.dialog.authz.FclAuthzDialog
import io.outblock.lilico.widgets.webview.fcl.model.FclDialogModel

class LinkAccountDialog : BottomSheetDialogFragment() {

    private lateinit var binding: DialogLinkAccountBinding
    private val fcl by lazy { arguments?.getParcelable<FclDialogModel>(EXTRA_DATA) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogLinkAccountBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (fcl == null) {
            dismiss()
            return
        }

        val fcl = fcl ?: return

        binding.setup(fcl)

        binding.root.requestFocus()
        binding.closeButton.setOnClickListener { dismiss() }
        binding.startButton.setOnProcessing {
            approveCallback?.invoke(true)
            dismiss()
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        approveCallback?.invoke(false)
    }

    override fun onDestroy() {
        instance = null
        approveCallback = null
        super.onDestroy()
    }

    companion object {
        private const val EXTRA_DATA = "extra_data"

        fun show(fragmentManager: FragmentManager, fcl: FclDialogModel) {
            LinkAccountDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(EXTRA_DATA, fcl)
                }
            }.show(fragmentManager, "")
        }

        private var approveCallback: ((isApprove: Boolean) -> Unit)? = null

        private var instance: FclAuthzDialog? = null

        fun observe(callback: (isApprove: Boolean) -> Unit) {
            this.approveCallback = callback
        }

        fun isShowing() = (instance?.dialog?.isShowing ?: false) && !(instance?.isRemoving ?: true)

        fun dismiss() {
            instance?.dismiss()
        }
    }
}

private fun DialogLinkAccountBinding.setup(fcl: FclDialogModel) {
    dappIcon.loadFavicon(fcl.logo ?: fcl.url?.toFavIcon())
    dappName.text = fcl.title
    ioScope {
        val userinfo = userInfoCache().read()
        uiScope {
            walletIcon.loadAvatar(userinfo?.avatar.orEmpty())
            walletName.text = userinfo?.nickname
        }
    }
}
