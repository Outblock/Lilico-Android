package io.outblock.lilico.page.nft.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import io.outblock.lilico.page.nft.NFTFragment
import io.outblock.lilico.page.nft.NftListFragment

class NftListPageAdapter(
    fragment: NFTFragment
) : FragmentStatePagerAdapter(fragment.childFragmentManager) {

    override fun getCount(): Int = 2

    override fun getItem(position: Int): Fragment {
        return when (position) {
            1 -> NftListFragment.newInstance(false)
            else -> NftListFragment.newInstance(true)
        }
    }
}