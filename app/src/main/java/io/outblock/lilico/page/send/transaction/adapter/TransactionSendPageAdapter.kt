package io.outblock.lilico.page.send.transaction.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import io.outblock.lilico.page.send.transaction.AddressPageFragment

class TransactionSendPageAdapter(
    fragmentManager: FragmentManager
) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getCount(): Int = 2

    override fun getItem(position: Int): Fragment {
        return AddressPageFragment.newInstance(position)
    }
}