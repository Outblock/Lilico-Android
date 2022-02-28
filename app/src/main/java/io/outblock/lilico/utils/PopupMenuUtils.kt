package io.outblock.lilico.utils

import android.view.Gravity
import android.view.View
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.impl.AttachListPopupView
import com.lxj.xpopup.interfaces.OnSelectListener
import io.outblock.lilico.R

fun popupMenu(
    author: View,
    titles: List<String>,
    icons: List<Int>,
    selectListener: OnSelectListener,
): AttachListPopupView {
    return XPopup.Builder(author.context)
        .hasShadowBg(false)
        .isDestroyOnDismiss(true)
        .isViewMode(true)
        .offsetX(0)
        .offsetY(0)
        .atView(author)
        .asAttachList(
            titles.toTypedArray(),
            icons.toIntArray(),
            selectListener,
            R.layout.popup_menu_root,
            R.layout.popup_menu_item,
        )
        .setContentGravity(Gravity.START or Gravity.CENTER_VERTICAL)
}