package io.outblock.lilico.widgets.webview.fcl.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.transition.*
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.outblock.lilico.R
import io.outblock.lilico.databinding.DialogFclAuthzBinding
import io.outblock.lilico.manager.config.isGasFree
import io.outblock.lilico.page.browser.toFavIcon
import io.outblock.lilico.utils.extensions.isVisible
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.utils.uiScope


class FclAuthzDialog : BottomSheetDialogFragment() {

    private val cadence by lazy { arguments?.getString(EXTRA_CADENCE) }
    private val url by lazy { arguments?.getString(EXTRA_URL) }
    private val title by lazy { arguments?.getString(EXTRA_TITLE) }

    private lateinit var binding: DialogFclAuthzBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        instance = this
        binding = DialogFclAuthzBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (cadence == null) {
            dismiss()
            return
        }
        with(binding) {
            Glide.with(iconView).load(url?.toFavIcon()).placeholder(R.drawable.placeholder).into(iconView)
            nameView.text = title
            uiScope { feeNumber.text = if (isGasFree()) "0" else "0.001" }
            scriptTextView.text = cadence?.trimIndent()
            actionButton.setOnProcessing { approveCallback?.invoke(true) }
            scriptHeaderWrapper.setOnClickListener { toggleScriptVisible() }
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
            TransitionManager.go(Scene(scriptLayout), TransitionSet().apply {
                addTransition(ChangeBounds().apply { duration = 150 })
                addTransition(Fade(Fade.IN).apply { duration = 150 })
            })
            val toVisible = !scriptTextWrapper.isVisible()
            scriptTextWrapper.setVisible(toVisible)
            scriptArrow.rotation = if (toVisible) 0f else 270f
        }
    }

    companion object {
        private const val EXTRA_CADENCE = "cadence"
        private const val EXTRA_URL = "url"
        private const val EXTRA_TITLE = "title"

        private var approveCallback: ((isApprove: Boolean) -> Unit)? = null

        private var instance: FclAuthzDialog? = null

        fun observe(callback: (isApprove: Boolean) -> Unit) {
            this.approveCallback = callback
        }

        fun isShowing() = (instance?.dialog?.isShowing ?: false) && !(instance?.isRemoving ?: true)

        fun show(
            fragmentManager: FragmentManager,
            cadence: String,
            url: String?,
            title: String?,
        ) {
            if (instance != null) {
                return
            }
            FclAuthzDialog().apply {
                arguments = Bundle().apply {
                    putString(EXTRA_CADENCE, cadence)
                    putString(EXTRA_URL, url)
                    putString(EXTRA_TITLE, title)
                }
            }.show(fragmentManager, "")
        }

        fun dismiss() {
            instance?.dismiss()
        }
    }
}