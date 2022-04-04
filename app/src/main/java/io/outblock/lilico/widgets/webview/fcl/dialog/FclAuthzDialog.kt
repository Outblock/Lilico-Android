package io.outblock.lilico.widgets.webview.fcl.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.outblock.lilico.R
import io.outblock.lilico.databinding.DialogFclAuthzBinding
import io.outblock.lilico.widgets.webview.fcl.model.FclAuthzResponse
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FclAuthzDialog : BottomSheetDialogFragment() {

    private var data: FclAuthzResponse? = null
    private var result: Continuation<Boolean>? = null

    private lateinit var binding: DialogFclAuthzBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogFclAuthzBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (data == null || result == null) {
            return
        }
        with(binding) {
            Glide.with(iconView).load("").placeholder(R.drawable.placeholder).into(iconView)
            nameView.text = "Website"
            descView.text = getString(R.string.authz_desc, "Website")
            scriptTextView.text = data?.body?.cadence
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

    suspend fun show(fragmentManager: FragmentManager, data: FclAuthzResponse) = suspendCoroutine<Boolean> { result ->
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