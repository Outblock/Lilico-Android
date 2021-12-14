package io.outblock.lilico.base.presenter

interface BasePresenter<T> {
    fun bind(model: T)
}
