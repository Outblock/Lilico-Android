package io.outblock.lilico.page.nft.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import io.outblock.lilico.page.nft.NftListFragment

class NftListPageAdapter(
    activity: FragmentActivity
) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> NftListFragment.newInstance(false)
            else -> NftListFragment.newInstance(true)
        }
    }
}