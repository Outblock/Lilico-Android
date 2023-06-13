package io.outblock.lilico.page.dialog.accounts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.outblock.lilico.databinding.DialogAccountSwitchBinding
import io.outblock.lilico.page.dialog.accounts.adapter.AccountListAdapter
import io.outblock.lilico.utils.ioScope

class AccountSwitchDialog : BottomSheetDialogFragment() {

    private lateinit var binding: DialogAccountSwitchBinding

    private val adapter by lazy { AccountListAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogAccountSwitchBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.root.requestFocus()

        binding.newButton.setOnClickListener { dismiss() }
        binding.createButton.setOnClickListener {
            dismiss()
        }

        with(binding.recyclerView) {
            adapter = this@AccountSwitchDialog.adapter
        }

        loadAccounts()
    }

    private fun loadAccounts() {
        ioScope {

        }
    }

    companion object {

        fun show(fragmentManager: FragmentManager) {
            AccountSwitchDialog().showNow(fragmentManager, "")
        }
    }
}