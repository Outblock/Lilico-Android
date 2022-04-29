package io.outblock.lilico.page.explore.presenter

import android.graphics.Color
import androidx.recyclerview.widget.LinearLayoutManager
import com.zackratos.ultimatebarx.ultimatebarx.addStatusBarTopPadding
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.FragmentExploreBinding
import io.outblock.lilico.page.explore.ExploreFragment
import io.outblock.lilico.page.explore.adapter.ExploreRecentAdapter
import io.outblock.lilico.page.explore.model.ExploreModel
import io.outblock.lilico.page.explore.subpage.RecentHistoryDialog
import io.outblock.lilico.utils.extensions.dp2px
import io.outblock.lilico.utils.extensions.scrollToPositionForce
import io.outblock.lilico.widgets.itemdecoration.ColorDividerItemDecoration

class ExplorePresenter(
    private val fragment: ExploreFragment,
    private val binding: FragmentExploreBinding,
) : BasePresenter<ExploreModel> {

    private val recentAdapter by lazy { ExploreRecentAdapter() }

    init {
        binding.root.addStatusBarTopPadding()
        with(binding.recentListView) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(ColorDividerItemDecoration(Color.TRANSPARENT, 9.dp2px().toInt(), LinearLayoutManager.HORIZONTAL))
            adapter = recentAdapter
        }
        with(binding) {
            recentMoreButton.setOnClickListener { RecentHistoryDialog.show(fragment.childFragmentManager) }
        }
    }

    override fun bind(model: ExploreModel) {
        model.recentList?.let {
            recentAdapter.setNewDiffData(it)
            binding.recentListView.post { binding.recentListView.scrollToPositionForce(0) }
        }
    }
}