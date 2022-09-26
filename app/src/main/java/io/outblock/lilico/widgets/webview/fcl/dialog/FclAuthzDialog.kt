package io.outblock.lilico.widgets.webview.fcl.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.transition.AutoTransition
import androidx.transition.Scene
import androidx.transition.TransitionManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.outblock.lilico.R
import io.outblock.lilico.databinding.DialogFclAuthzBinding
import io.outblock.lilico.manager.app.chainNetWorkString
import io.outblock.lilico.manager.config.isGasFree
import io.outblock.lilico.network.ApiService
import io.outblock.lilico.network.model.CadenceSecurityCheck
import io.outblock.lilico.network.model.CadenceSecurityCheckResponse
import io.outblock.lilico.network.retrofitWithHost
import io.outblock.lilico.page.browser.loadFavicon
import io.outblock.lilico.page.browser.toFavIcon
import io.outblock.lilico.utils.extensions.isVisible
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.uiScope
import io.outblock.lilico.widgets.webview.fcl.model.FclDialogModel


class FclAuthzDialog : BottomSheetDialogFragment() {

    private val data by lazy { arguments?.getParcelable<FclDialogModel>(EXTRA_DATA) }

    private lateinit var binding: DialogFclAuthzBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        instance = this
        binding = DialogFclAuthzBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (data == null) {
            dismiss()
            return
        }
        val data = data ?: return
        with(binding) {
            iconView.loadFavicon(data.logo ?: data.url?.toFavIcon())
            nameView.text = data.title
            uiScope { feeNumber.text = if (isGasFree()) "0" else "0.001" }
            scriptTextView.text = data.cadence?.trimIndent()
            actionButton.setOnProcessing { approveCallback?.invoke(true) }
            scriptHeaderWrapper.setOnClickListener { toggleScriptVisible() }
        }

        ioScope {
            val response = securityCadenceCheck(data.cadence?.trimIndent().orEmpty()) ?: return@ioScope
            if (isShowing()) {
                uiScope {
                    val template = response.data?.template?.data
                    val titleMap = template?.messages?.title?.i18n
                    val descMap = template?.messages?.description?.i18n
                    binding.securityCheckTitle.text = titleMap?.get(titleMap.keys.first())
                    binding.securityCheckDesc.text = descMap?.get(descMap.keys.first())

                    val auditor = response.data?.auditors?.firstOrNull()
                    binding.auditorsTitle.text = requireContext().getString(R.string.auditors_by, auditor?.name.orEmpty())

                    TransitionManager.go(Scene(binding.rootView), AutoTransition().apply { duration = 150 })
                    changeDialogBounds()

                    binding.securityCheckWrapper.setVisible(template != null)
                    binding.auditorsWrapper.setVisible(auditor != null)
                }
            }
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

    private fun toggleScriptVisible() {
        with(binding) {
            TransitionManager.go(Scene(scriptLayout), AutoTransition().apply { duration = 150 })
            changeDialogBounds()

            val toVisible = !scriptTextWrapper.isVisible()
            scriptTextWrapper.setVisible(toVisible)
            scriptArrow.rotation = if (toVisible) 0f else 270f
        }
    }

    private fun changeDialogBounds() {
//        TransitionManager.go(Scene(binding.root.parent as ViewGroup), TransitionSet().apply {
//            addTransition(ChangeBounds().apply { duration = 150 })
//        })
    }

    companion object {
        private const val EXTRA_DATA = "data"

        private var approveCallback: ((isApprove: Boolean) -> Unit)? = null

        private var instance: FclAuthzDialog? = null

        fun observe(callback: (isApprove: Boolean) -> Unit) {
            this.approveCallback = callback
        }

        fun isShowing() = (instance?.dialog?.isShowing ?: false) && !(instance?.isRemoving ?: true)

        fun show(
            fragmentManager: FragmentManager,
            data: FclDialogModel,
        ) {
            if (instance != null) {
                return
            }
            FclAuthzDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(EXTRA_DATA, data)
                }
            }.show(fragmentManager, "")
        }

        fun dismiss() {
            instance?.dismiss()
        }
    }
}

private suspend fun securityCadenceCheck(cadence: String): CadenceSecurityCheckResponse? {
    return runCatching {
        val service = retrofitWithHost("https://lilico.app").create(ApiService::class.java)
        val cadenceB64 = Base64.encodeToString(cadence.toByteArray(), Base64.NO_WRAP)
//        val cadenceB64 =
//            "aW1wb3J0IEZ1bmdpYmxlVG9rZW4gZnJvbSAweDlhMDc2NmQ5M2I2NjA4YjcKaW1wb3J0IEZsb3dUb2tlbiBmcm9tIDB4N2U2MGRmMDQyYTljMDg2OAppbXBvcnQgRmxvd0lEVGFibGVTdGFraW5nIGZyb20gMHg5ZWNhMmIzOGIxOGI1ZGZlCmltcG9ydCBMb2NrZWRUb2tlbnMgZnJvbSAweDk1ZTAxOWExN2QwZTIzZDcKaW1wb3J0IEZsb3dTdGFraW5nQ29sbGVjdGlvbiBmcm9tIDB4OTVlMDE5YTE3ZDBlMjNkNwoKLy8vIFRoaXMgdHJhbnNhY3Rpb24gc2V0cyB1cCBhbiBhY2NvdW50IHRvIHVzZSBhIHN0YWtpbmcgY29sbGVjdGlvbgovLy8gSXQgd2lsbCB3b3JrIHJlZ2FyZGxlc3Mgb2Ygd2hldGhlciB0aGV5IGhhdmUgYSByZWd1bGFyIGFjY291bnQsIGEgdHdvLWFjY291bnQgbG9ja2VkIHRva2VucyBzZXR1cCwKLy8vIG9yIHN0YWtpbmcgb2JqZWN0cyBzdG9yZWQgaW4gdGhlIHVubG9ja2VkIGFjY291bnQKCnRyYW5zYWN0aW9uIHsKICAgIHByZXBhcmUoc2lnbmVyOiBBdXRoQWNjb3VudCkgewoKICAgICAgICAvLyBJZiB0aGVyZSBpc24ndCBhbHJlYWR5IGEgc3Rha2luZyBjb2xsZWN0aW9uCiAgICAgICAgaWYgc2lnbmVyLmJvcnJvdzwmRmxvd1N0YWtpbmdDb2xsZWN0aW9uLlN0YWtpbmdDb2xsZWN0aW9uPihmcm9tOiBGbG93U3Rha2luZ0NvbGxlY3Rpb24uU3Rha2luZ0NvbGxlY3Rpb25TdG9yYWdlUGF0aCkgPT0gbmlsIHsKCiAgICAgICAgICAgIC8vIENyZWF0ZSBwcml2YXRlIGNhcGFiaWxpdGllcyBmb3IgdGhlIHRva2VuIGhvbGRlciBhbmQgdW5sb2NrZWQgdmF1bHQKICAgICAgICAgICAgbGV0IGxvY2tlZEhvbGRlciA9IHNpZ25lci5saW5rPCZMb2NrZWRUb2tlbnMuVG9rZW5Ib2xkZXI+KC9wcml2YXRlL2Zsb3dUb2tlbkhvbGRlciwgdGFyZ2V0OiBMb2NrZWRUb2tlbnMuVG9rZW5Ib2xkZXJTdG9yYWdlUGF0aCkhCiAgICAgICAgICAgIGxldCBmbG93VG9rZW4gPSBzaWduZXIubGluazwmRmxvd1Rva2VuLlZhdWx0PigvcHJpdmF0ZS9mbG93VG9rZW5WYXVsdCwgdGFyZ2V0OiAvc3RvcmFnZS9mbG93VG9rZW5WYXVsdCkhCiAgICAgICAgICAgIAogICAgICAgICAgICAvLyBDcmVhdGUgYSBuZXcgU3Rha2luZyBDb2xsZWN0aW9uIGFuZCBwdXQgaXQgaW4gc3RvcmFnZQogICAgICAgICAgICBpZiBsb2NrZWRIb2xkZXIuY2hlY2soKSB7CiAgICAgICAgICAgICAgICBzaWduZXIuc2F2ZSg8LUZsb3dTdGFraW5nQ29sbGVjdGlvbi5jcmVhdGVTdGFraW5nQ29sbGVjdGlvbih1bmxvY2tlZFZhdWx0OiBmbG93VG9rZW4sIHRva2VuSG9sZGVyOiBsb2NrZWRIb2xkZXIpLCB0bzogRmxvd1N0YWtpbmdDb2xsZWN0aW9uLlN0YWtpbmdDb2xsZWN0aW9uU3RvcmFnZVBhdGgpCiAgICAgICAgICAgIH0gZWxzZSB7CiAgICAgICAgICAgICAgICBzaWduZXIuc2F2ZSg8LUZsb3dTdGFraW5nQ29sbGVjdGlvbi5jcmVhdGVTdGFraW5nQ29sbGVjdGlvbih1bmxvY2tlZFZhdWx0OiBmbG93VG9rZW4sIHRva2VuSG9sZGVyOiBuaWwpLCB0bzogRmxvd1N0YWtpbmdDb2xsZWN0aW9uLlN0YWtpbmdDb2xsZWN0aW9uU3RvcmFnZVBhdGgpCiAgICAgICAgICAgIH0KCiAgICAgICAgICAgIC8vIENyZWF0ZSBhIHB1YmxpYyBsaW5rIHRvIHRoZSBzdGFraW5nIGNvbGxlY3Rpb24KICAgICAgICAgICAgc2lnbmVyLmxpbms8JkZsb3dTdGFraW5nQ29sbGVjdGlvbi5TdGFraW5nQ29sbGVjdGlvbntGbG93U3Rha2luZ0NvbGxlY3Rpb24uU3Rha2luZ0NvbGxlY3Rpb25QdWJsaWN9PigKICAgICAgICAgICAgICAgIEZsb3dTdGFraW5nQ29sbGVjdGlvbi5TdGFraW5nQ29sbGVjdGlvblB1YmxpY1BhdGgsCiAgICAgICAgICAgICAgICB0YXJnZXQ6IEZsb3dTdGFraW5nQ29sbGVjdGlvbi5TdGFraW5nQ29sbGVjdGlvblN0b3JhZ2VQYXRoCiAgICAgICAgICAgICkKICAgICAgICB9CgogICAgICAgIC8vIGJvcnJvdyBhIHJlZmVyZW5jZSB0byB0aGUgc3Rha2luZyBjb2xsZWN0aW9uCiAgICAgICAgbGV0IGNvbGxlY3Rpb25SZWYgPSBzaWduZXIuYm9ycm93PCZGbG93U3Rha2luZ0NvbGxlY3Rpb24uU3Rha2luZ0NvbGxlY3Rpb24+KGZyb206IEZsb3dTdGFraW5nQ29sbGVjdGlvbi5TdGFraW5nQ29sbGVjdGlvblN0b3JhZ2VQYXRoKQogICAgICAgICAgICA/PyBwYW5pYygiQ291bGQgbm90IGJvcnJvdyBzdGFraW5nIGNvbGxlY3Rpb24gcmVmZXJlbmNlIikKCiAgICAgICAgLy8gSWYgdGhlcmUgaXMgYSBub2RlIHN0YWtlciBvYmplY3QgaW4gdGhlIGFjY291bnQsIHB1dCBpdCBpbiB0aGUgc3Rha2luZyBjb2xsZWN0aW9uCiAgICAgICAgaWYgc2lnbmVyLmJvcnJvdzwmRmxvd0lEVGFibGVTdGFraW5nLk5vZGVTdGFrZXI+KGZyb206IEZsb3dJRFRhYmxlU3Rha2luZy5Ob2RlU3Rha2VyU3RvcmFnZVBhdGgpICE9IG5pbCB7CiAgICAgICAgICAgIGxldCBub2RlIDwtIHNpZ25lci5sb2FkPEBGbG93SURUYWJsZVN0YWtpbmcuTm9kZVN0YWtlcj4oZnJvbTogRmxvd0lEVGFibGVTdGFraW5nLk5vZGVTdGFrZXJTdG9yYWdlUGF0aCkhCiAgICAgICAgICAgIGNvbGxlY3Rpb25SZWYuYWRkTm9kZU9iamVjdCg8LW5vZGUsIG1hY2hpbmVBY2NvdW50SW5mbzogbmlsKQogICAgICAgIH0KCiAgICAgICAgLy8gSWYgdGhlcmUgaXMgYSBkZWxlZ2F0b3Igb2JqZWN0IGluIHRoZSBhY2NvdW50LCBwdXQgaXQgaW4gdGhlIHN0YWtpbmcgY29sbGVjdGlvbgogICAgICAgIGlmIHNpZ25lci5ib3Jyb3c8JkZsb3dJRFRhYmxlU3Rha2luZy5Ob2RlRGVsZWdhdG9yPihmcm9tOiBGbG93SURUYWJsZVN0YWtpbmcuRGVsZWdhdG9yU3RvcmFnZVBhdGgpICE9IG5pbCB7CiAgICAgICAgICAgIGxldCBkZWxlZ2F0b3IgPC0gc2lnbmVyLmxvYWQ8QEZsb3dJRFRhYmxlU3Rha2luZy5Ob2RlRGVsZWdhdG9yPihmcm9tOiBGbG93SURUYWJsZVN0YWtpbmcuRGVsZWdhdG9yU3RvcmFnZVBhdGgpIQogICAgICAgICAgICBjb2xsZWN0aW9uUmVmLmFkZERlbGVnYXRvck9iamVjdCg8LWRlbGVnYXRvcikKICAgICAgICB9CiAgICB9Cn0="
        service.securityCadenceCheck(CadenceSecurityCheck(cadenceB64, network = chainNetWorkString()))
    }.getOrNull()
}