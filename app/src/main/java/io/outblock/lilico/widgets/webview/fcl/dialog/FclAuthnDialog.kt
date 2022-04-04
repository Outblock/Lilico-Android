package io.outblock.lilico.widgets.webview.fcl.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.outblock.lilico.R
import io.outblock.lilico.databinding.DialogFclAuthnBinding
import io.outblock.lilico.widgets.webview.fcl.model.FclAuthnResponse
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FclAuthnDialog : BottomSheetDialogFragment() {

    private var data: FclAuthnResponse? = null
    private var result: Continuation<Boolean>? = null

    private lateinit var binding: DialogFclAuthnBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogFclAuthnBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (data == null || result == null) {
            return
        }
        with(binding) {
            Glide.with(iconView).load("").placeholder(R.drawable.placeholder).into(iconView)
            nameView.text = "Website"
            descView.text = getString(R.string.authn_desc, "Website")
            closeButton.setOnClickListener {
                dismiss()
                result?.resume(false)
            }
            cancelButton.setOnClickListener {
                dismiss()
                result?.resume(false)
            }
            approveButton.setOnClickListener {
                dismiss()
                result?.resume(true)
            }
        }
    }

    suspend fun show(fragmentManager: FragmentManager, data: FclAuthnResponse) = suspendCoroutine<Boolean> { result ->
        this.data = data
        this.result = result
        show(fragmentManager, "")
    }

    override fun onResume() {
        if (data == null || result == null) {
            dismiss()
        }
        super.onResume()
    }
}