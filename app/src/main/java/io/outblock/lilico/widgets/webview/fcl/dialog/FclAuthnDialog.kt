package io.outblock.lilico.widgets.webview.fcl.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.outblock.lilico.databinding.DialogFclAuthnBinding
import io.outblock.lilico.page.browser.loadFavicon
import io.outblock.lilico.page.browser.toFavIcon
import io.outblock.lilico.utils.extensions.urlHost
import io.outblock.lilico.widgets.webview.fcl.model.FclDialogModel
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FclAuthnDialog : BottomSheetDialogFragment() {

    private var data: FclDialogModel? = null
    private var result: Continuation<Boolean>? = null

    private lateinit var binding: DialogFclAuthnBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogFclAuthnBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        result ?: return
        val data = data ?: return
        with(binding) {
            iconView.loadFavicon(data.logo ?: data.url?.toFavIcon())
            nameView.text = data.title
            urlView.text = data.url?.urlHost()
            cancelButton.setOnClickListener {
                result?.resume(false)
                dismiss()
            }
            approveButton.setOnClickListener {
                result?.resume(true)
                dismiss()
            }
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        result?.resume(false)
    }

    suspend fun show(
        fragmentManager: FragmentManager,
        data: FclDialogModel,
    ) = suspendCoroutine<Boolean> { result ->
        this.result = result
        this.data = data
        show(fragmentManager, "")
    }

    override fun onResume() {
        if (result == null) {
            dismiss()
        }
        super.onResume()
    }

}