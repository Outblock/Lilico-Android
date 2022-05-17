package io.outblock.lilico.page.explore.subpage

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.outblock.lilico.database.AppDataBase
import io.outblock.lilico.database.Bookmark
import io.outblock.lilico.databinding.DialogRecentHistoryBinding
import io.outblock.lilico.page.explore.ExploreViewModel
import io.outblock.lilico.page.explore.adapter.ExploreBookmarkAdapter
import io.outblock.lilico.utils.viewModelIOScope
import io.outblock.lilico.widgets.itemdecoration.GridSpaceItemDecoration

class BookmarkListDialog : BottomSheetDialogFragment() {

    private lateinit var binding: DialogRecentHistoryBinding

    private lateinit var viewModel: BookmarkListViewModel

    private val adapter by lazy { ExploreBookmarkAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogRecentHistoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.root.requestFocus()
        with(binding.recyclerView) {
            adapter = this@BookmarkListDialog.adapter
            layoutManager = GridLayoutManager(context, 2)
            addItemDecoration(GridSpaceItemDecoration(vertical = 9.0, horizontal = 9.0))
        }

        viewModel = ViewModelProvider(this)[BookmarkListViewModel::class.java].apply {
            bookmarkLiveData.observe(this@BookmarkListDialog) { adapter.setNewDiffData(it) }
            bindFragment(this@BookmarkListDialog)
            load()
        }

        ViewModelProvider(requireActivity())[ExploreViewModel::class.java].apply {
            onDAppClickLiveData.observe(this@BookmarkListDialog) { if (isResumed) dismiss() }
        }

        binding.closeButton.setOnClickListener { dismiss() }
    }

    companion object {

        fun show(fragmentManager: FragmentManager) {
            BookmarkListDialog().showNow(fragmentManager, "")
        }
    }
}

class BookmarkListViewModel : ViewModel() {
    val bookmarkLiveData = MutableLiveData<List<Bookmark>>()

    @SuppressLint("StaticFieldLeak")
    private lateinit var fragment: Fragment

    fun bindFragment(fragment: Fragment) {
        this.fragment = fragment
        registerObserve()
    }

    fun load() {
        viewModelIOScope(this) {
            bookmarkLiveData.postValue(AppDataBase.database().bookmarkDao().findAll(limit = 100))
        }
    }

    private fun registerObserve() {
        AppDataBase.database().bookmarkDao().findAllLive(limit = 100).observe(fragment.viewLifecycleOwner) { bookmarkLiveData.postValue(it) }
    }
}