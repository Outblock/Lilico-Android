package io.outblock.lilico.page.explore.presenter

import android.graphics.Color
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionManager
import com.zackratos.ultimatebarx.ultimatebarx.addStatusBarTopPadding
import io.outblock.lilico.base.presenter.BasePresenter
import io.outblock.lilico.databinding.FragmentExploreBinding
import io.outblock.lilico.page.browser.openBrowser
import io.outblock.lilico.page.explore.ExploreFragment
import io.outblock.lilico.page.explore.adapter.ExploreBookmarkAdapter
import io.outblock.lilico.page.explore.adapter.ExploreDAppAdapter
import io.outblock.lilico.page.explore.adapter.ExploreRecentAdapter
import io.outblock.lilico.page.explore.model.ExploreModel
import io.outblock.lilico.page.explore.subpage.BookmarkListDialog
import io.outblock.lilico.page.explore.subpage.RecentHistoryDialog
import io.outblock.lilico.page.profile.subpage.claimdomain.ClaimDomainActivity
import io.outblock.lilico.page.profile.subpage.claimdomain.MeowDomainClaimedStateChangeListener
import io.outblock.lilico.page.profile.subpage.claimdomain.observeMeowDomainClaimedStateChange
import io.outblock.lilico.utils.extensions.*
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.isMeowDomainClaimed
import io.outblock.lilico.utils.uiScope
import io.outblock.lilico.widgets.itemdecoration.ColorDividerItemDecoration
import io.outblock.lilico.widgets.itemdecoration.GridSpaceItemDecoration
import kotlinx.coroutines.delay

class ExplorePresenter(
    private val fragment: ExploreFragment,
    private val binding: FragmentExploreBinding,
) : BasePresenter<ExploreModel>, MeowDomainClaimedStateChangeListener {

    private val recentAdapter by lazy { ExploreRecentAdapter(true) }
    private val bookmarkAdapter by lazy { ExploreBookmarkAdapter() }
    private val dappAdapter by lazy { ExploreDAppAdapter() }

    private val activity by lazy { fragment.requireActivity() }

    init {
        binding.root.addStatusBarTopPadding()
        with(binding.recentListView) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(ColorDividerItemDecoration(Color.TRANSPARENT, 9.dp2px().toInt(), LinearLayoutManager.HORIZONTAL))
            adapter = recentAdapter
        }

        with(binding.bookmarkListView) {
            layoutManager = GridLayoutManager(context, 5)
            addItemDecoration(
                GridSpaceItemDecoration(
                    start = 18.0,
                    end = 18.0,
                    horizontal = 14.0,
                    vertical = 16.0,
                )
            )
            adapter = bookmarkAdapter
        }

        with(binding.dappListView) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(ColorDividerItemDecoration(Color.TRANSPARENT, 12.dp2px().toInt(), LinearLayoutManager.VERTICAL))
            adapter = dappAdapter
        }

        with(binding) {
            recentMoreButton.setOnClickListener { RecentHistoryDialog.show(activity.supportFragmentManager) }
            bookmarkMoreButton.setOnClickListener { BookmarkListDialog.show(activity.supportFragmentManager) }
            searchBox.root.setOnClickListener {
                uiScope {
                    searchBoxWrapper.setVisible(false, invisible = true)
                    delay(800)
                    searchBoxWrapper.setVisible(true)
                }
                openBrowser(activity, searchBoxPosition = searchBox.root.location())
            }
            claimButton.setOnClickListener { ClaimDomainActivity.launch(activity) }
        }
        observeMeowDomainClaimedStateChange(this)
        updateClaimDomainState()
    }

    override fun bind(model: ExploreModel) {
        model.recentList?.let {
            recentAdapter.setNewDiffData(it)
            binding.recentWrapper.setVisible(it.isNotEmpty())
            binding.recentListView.post { binding.recentListView.scrollToPositionForce(0) }
        }

        model.bookmarkList?.let {
            bookmarkAdapter.setNewDiffData(it)
            binding.bookmarkWrapper.setVisible(it.isNotEmpty())
        }

        model.dAppList?.let {
            dappAdapter.setNewDiffData(it)
            binding.dappWrapper.setVisible(it.isNotEmpty())
        }
    }

    override fun onDomainClaimedStateChange(isClaimed: Boolean) {
        updateClaimDomainState()
    }

    private fun updateClaimDomainState() {
        ioScope {
            val isClaimedDomain = isMeowDomainClaimed()
            val isVisibleChange = binding.claimDomainWrapper.isVisible() == isClaimedDomain
            if (isVisibleChange) {
                uiScope {
                    TransitionManager.beginDelayedTransition(binding.contentWrapper)
                    binding.claimDomainWrapper.setVisible(!isClaimedDomain)
                }
            }
        }
    }
}