package io.outblock.lilico.page.dialog.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.outblock.lilico.databinding.DialogBackupTipsBinding

class BackupTipsDialog : BottomSheetDialogFragment() {

    private lateinit var binding: DialogBackupTipsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogBackupTipsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.root.requestFocus()

        binding.closeButton.setOnClickListener { dismiss() }
        binding.skipButton.setOnClickListener { dismiss() }
        binding.startButton.setOnClickListener { }
    }

    companion object {

        fun show(fragmentManager: FragmentManager) {
            BackupTipsDialog().showNow(fragmentManager, "")
        }
    }
}