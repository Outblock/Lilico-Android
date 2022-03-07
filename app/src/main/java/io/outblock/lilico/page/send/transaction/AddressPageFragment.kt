package io.outblock.lilico.page.send.transaction

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import io.outblock.lilico.databinding.LayoutTransactionSendAddressListBinding
import io.outblock.lilico.page.address.adapter.AddressBookAdapter
import io.outblock.lilico.utils.extensions.dp2px
import io.outblock.lilico.widgets.itemdecoration.ColorDividerItemDecoration

class AddressPageFragment : Fragment() {

    private val type by lazy { requireArguments().getInt(EXTRA_TYPE, -1) }
    private val adapter by lazy { AddressBookAdapter() }

    private lateinit var binding: LayoutTransactionSendAddressListBinding
    private lateinit var viewModel: TransactionSendViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = LayoutTransactionSendAddressListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding.root) {
            adapter = this@AddressPageFragment.adapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(
                ColorDividerItemDecoration(Color.TRANSPARENT, 4.dp2px().toInt())
            )
        }

        viewModel = ViewModelProvider(requireActivity())[TransactionSendViewModel::class.java].apply {
            when (type) {
                TYPE_RECENT -> recentListLiveData.observe(viewLifecycleOwner) { adapter.setNewDiffData(it) }
                TYPE_ADDRESS -> addressListLiveData.observe(viewLifecycleOwner) { adapter.setNewDiffData(it) }
                TYPE_ACCOUNT -> accountListLiveData.observe(viewLifecycleOwner) { adapter.setNewDiffData(it) }
            }
            load(type)
        }
    }

    companion object {
        const val TYPE_RECENT = 0
        const val TYPE_ADDRESS = 1
        const val TYPE_ACCOUNT = 2

        private const val EXTRA_TYPE = "extra_type"

        fun newInstance(type: Int): AddressPageFragment {
            return AddressPageFragment().apply {
                arguments = Bundle().apply {
                    putInt(EXTRA_TYPE, type)
                }
            }
        }
    }
}