package io.outblock.lilico.page.main.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import io.outblock.lilico.page.explore.ExploreFragment
import io.outblock.lilico.page.main.MainActivity
import io.outblock.lilico.page.nft.nftlist.NFTFragment
import io.outblock.lilico.page.profile.ProfileFragment
import io.outblock.lilico.page.wallet.fragment.WalletHomeFragment

class MainPageAdapter(
    private val activity: MainActivity
) : FragmentStatePagerAdapter(activity.supportFragmentManager) {
    override fun getCount(): Int = 4

    override fun getItem(position: Int): Fragment {
        return when (position) {
            1 -> NFTFragment()
            2 -> ExploreFragment()
            3 -> ProfileFragment()
            else -> WalletHomeFragment()
        }
    }
}