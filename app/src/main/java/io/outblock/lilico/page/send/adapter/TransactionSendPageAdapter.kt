package io.outblock.lilico.page.send.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import io.outblock.lilico.page.send.AddressPageFragment

class TransactionSendPageAdapter(
    fragmentManager: FragmentManager
) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getCount(): Int = 3

    override fun getItem(position: Int): Fragment {
        return AddressPageFragment.newInstance(position)
    }
}