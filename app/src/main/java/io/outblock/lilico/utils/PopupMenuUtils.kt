package io.outblock.lilico.utils

import android.content.Context
import android.view.View
import com.lxj.xpopup.core.AttachPopupView
import com.lxj.xpopup.interfaces.OnSelectListener
import io.outblock.lilico.R
import io.outblock.lilico.utils.extensions.res2color
import io.outblock.lilico.widgets.popup.PopupBuilder
import io.outblock.lilico.widgets.popup.PopupListView

fun popupMenu(
    author: View,
    context: Context? = null,
    items: List<PopupListView.ItemData>,
    selectListener: OnSelectListener,
    offsetX: Int = 0,
    offsetY: Int = 0,
    isDialogMode: Boolean = false
): AttachPopupView {
    val builder = PopupBuilder(context ?: author.context)
        .hasShadowBg(false)
        .isDestroyOnDismiss(true)
        .isViewMode(!isDialogMode)
        .navigationBarColor(R.color.deep_bg.res2color())
        .isLightNavigationBar(isNightMode())
        .hasNavigationBar(true)
        .isLightStatusBar(isNightMode())
        .offsetX(offsetX)
        .offsetY(offsetY)
        .atView(author)

    return PopupListView(context ?: author.context, items).apply {
        setOnSelectListener(selectListener)
        popupInfo = builder.popupInfo
    }
}