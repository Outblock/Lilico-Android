package io.outblock.lilico.page.transaction.record.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import io.outblock.lilico.page.transaction.record.fragments.TransactionRecordListFragment

class TransactionRecordPageAdapter(
    fragmentManager: FragmentManager
) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getCount(): Int = 2

    override fun getItem(position: Int): Fragment {
        return TransactionRecordListFragment.newInstance(position)
    }
}