package io.outblock.lilico.page.explore.subpage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.outblock.lilico.databinding.DialogRecentHistoryBinding
import io.outblock.lilico.page.explore.ExploreViewModel
import io.outblock.lilico.page.explore.adapter.ExploreRecentAdapter
import io.outblock.lilico.widgets.itemdecoration.GridSpaceItemDecoration

class RecentHistoryDialog : BottomSheetDialogFragment() {

    private lateinit var binding: DialogRecentHistoryBinding

    private lateinit var viewModel: RecentHistoryViewModel

    private val adapter by lazy { ExploreRecentAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogRecentHistoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.root.requestFocus()
        with(binding.recyclerView) {
            adapter = this@RecentHistoryDialog.adapter
            layoutManager = GridLayoutManager(context, 2)
            addItemDecoration(GridSpaceItemDecoration(vertical = 9.0, horizontal = 9.0))
        }

        viewModel = ViewModelProvider(this)[RecentHistoryViewModel::class.java].apply {
            recentLiveData.observe(this@RecentHistoryDialog) { adapter.setNewDiffData(it) }
            bindFragment(this@RecentHistoryDialog)
            load()
        }

        ViewModelProvider(requireActivity())[ExploreViewModel::class.java].apply {
            onDAppClickLiveData.observe(this@RecentHistoryDialog) { dismiss() }
        }

        binding.closeButton.setOnClickListener { dismiss() }
    }

    companion object {

        fun show(fragmentManager: FragmentManager) {
            RecentHistoryDialog().show(fragmentManager, "")
        }
    }
}