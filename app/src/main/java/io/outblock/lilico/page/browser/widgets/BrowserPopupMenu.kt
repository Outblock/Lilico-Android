package io.outblock.lilico.page.browser.widgets

import android.view.View
import io.outblock.lilico.R
import io.outblock.lilico.database.AppDataBase
import io.outblock.lilico.database.Bookmark
import io.outblock.lilico.page.browser.tools.BrowserTab
import io.outblock.lilico.utils.extensions.dp2px
import io.outblock.lilico.utils.extensions.openInSystemBrowser
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.extensions.res2color
import io.outblock.lilico.utils.ioScope
import io.outblock.lilico.utils.popupMenu
import io.outblock.lilico.utils.shareText
import io.outblock.lilico.utils.uiScope
import io.outblock.lilico.widgets.popup.PopupListView

class BrowserPopupMenu(
    private val view: View,
    private val browserTab: BrowserTab,
) {
    fun show() {
        uiScope {
            val iconColor = R.color.salmon_primary.res2color()
            popupMenu(
                view,
                items = listOf(
                    PopupListView.ItemData(R.string.bookmark.res2String(), iconRes = R.drawable.ic_bookmark, iconTint = iconColor),
                    PopupListView.ItemData(R.string.share.res2String(), iconRes = R.drawable.ic_web, iconTint = iconColor),
                    PopupListView.ItemData(R.string.open_in_browser.res2String(), iconRes = R.drawable.ic_web, iconTint = iconColor),
                ),
                selectListener = { _, text -> onMenuItemClick(text) },
                offsetX = -50.dp2px().toInt()
            ).show()
        }
    }

    private fun onMenuItemClick(text: String): Boolean {
        when (text) {
            R.string.bookmark.res2String() -> addToBookmark()
            R.string.share.res2String() -> browserTab.url()?.let { view.context.shareText(it) }
            R.string.open_in_browser.res2String() -> browserTab.url()?.openInSystemBrowser(view.context)
        }
        return true
    }

    private fun addToBookmark() {
        val url = browserTab.url().orEmpty()
        val title = browserTab.title().orEmpty()
        ioScope {
            AppDataBase.database().bookmarkDao().save(
                Bookmark(
                    url = url,
                    title = title,
                    createTime = System.currentTimeMillis(),
                )
            )
        }
    }

}