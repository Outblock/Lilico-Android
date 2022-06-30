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
import com.nftco.flow.sdk.hexToBytes
import io.outblock.lilico.R
import io.outblock.lilico.databinding.DialogFclSignMessageBinding
import io.outblock.lilico.page.browser.toFavIcon
import io.outblock.lilico.utils.extensions.isVisible
import io.outblock.lilico.utils.extensions.setVisible
import io.outblock.lilico.widgets.webview.fcl.model.FclDialogModel


class FclSignMessageDialog : BottomSheetDialogFragment() {

    private val data by lazy { arguments?.getParcelable<FclDialogModel>(EXTRA_DATA) }

    private lateinit var binding: DialogFclSignMessageBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogFclSignMessageBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (data == null) {
            dismiss()
            return
        }
        val data = data ?: return
        with(binding) {
            Glide.with(iconView).load(data.logo ?: data.url?.toFavIcon()).placeholder(R.drawable.placeholder).into(iconView)
            nameView.text = data.title
            data.signMessage?.hexToBytes()?.let { scriptTextView.text = it.toString(Charsets.UTF_8) }
            actionButton.setOnProcessing {
                approveCallback?.invoke(true)
                dismiss()
            }
            scriptHeaderWrapper.setOnClickListener { toggleScriptVisible() }
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        approveCallback?.invoke(false)
    }

    override fun onDestroy() {
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
        private const val EXTRA_DATA = "data"

        private var approveCallback: ((isApprove: Boolean) -> Unit)? = null

        fun observe(callback: (isApprove: Boolean) -> Unit) {
            this.approveCallback = callback
        }

        fun show(
            fragmentManager: FragmentManager,
            data: FclDialogModel,
        ) {
            FclSignMessageDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(EXTRA_DATA, data)
                }
            }.show(fragmentManager, "")
        }
    }
}