package io.outblock.lilico.base.fragment

import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {

    private var firstVisible = true

    override fun onResume() {
        super.onResume()
        if (firstVisible) {
            onFirstVisible()
        } else {
            onRevisible()
        }
        firstVisible = false
    }

    open fun onFirstVisible() {}

    open fun onRevisible() {}

    fun isFirstVisible() = firstVisible
}