package io.outblock.lilico.utils

import android.view.View
import com.lxj.xpopup.core.AttachPopupView
import com.lxj.xpopup.interfaces.OnSelectListener
import io.outblock.lilico.widgets.popup.PopupBuilder
import io.outblock.lilico.widgets.popup.PopupListView

fun popupMenu(
    author: View,
    items: List<PopupListView.ItemData>,
    selectListener: OnSelectListener,
): AttachPopupView {
    val builder = PopupBuilder(author.context)
        .hasShadowBg(false)
        .isDestroyOnDismiss(true)
        .isViewMode(true)
        .offsetX(0)
        .offsetY(0)
        .atView(author)

    return PopupListView(author.context, items).apply {
        setOnSelectListener(selectListener)
        popupInfo = builder.popupInfo
    }
}