package io.outblock.lilico.page.transaction.record.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.outblock.lilico.R
import io.outblock.lilico.base.fragment.BaseFragment
import io.outblock.lilico.page.transaction.record.TransactionRecordViewModel
import io.outblock.lilico.page.transaction.record.adapter.TransactionRecordListAdapter
import io.outblock.lilico.utils.extensions.dp2px
import io.outblock.lilico.widgets.itemdecoration.ColorDividerItemDecoration

class TransactionRecordListFragment : BaseFragment() {

    private val type by lazy { arguments?.getInt(EXTRA_TYPE)!! }
    private val recyclerView by lazy { view?.findViewById<RecyclerView>(R.id.recycler_view)!! }

    private val adapter by lazy { TransactionRecordListAdapter() }
    private val viewModel by lazy { ViewModelProvider(requireActivity())[TransactionRecordViewModel::class.java] }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_transaction_record_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(recyclerView) {
            adapter = this@TransactionRecordListFragment.adapter
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(ColorDividerItemDecoration(Color.TRANSPARENT, 4.dp2px().toInt()))
        }
        with(viewModel) {
            if (type == TYPE_TRANSACTION) {
                transactionListLiveData.observe(viewLifecycleOwner) { adapter.setNewDiffData(it) }
            } else {
                transferListLiveData.observe(viewLifecycleOwner) { adapter.setNewDiffData(it) }
            }
        }
    }

    companion object {
        private const val EXTRA_TYPE = "type"

        const val TYPE_TRANSFER = 0
        const val TYPE_TRANSACTION = 1
        fun newInstance(type: Int): TransactionRecordListFragment {
            return TransactionRecordListFragment().apply {
                arguments = Bundle().apply { putInt(EXTRA_TYPE, type) }
            }
        }
    }
}