package io.outblock.lilico.page.explore.subpage

import android.view.View
import io.outblock.lilico.R
import io.outblock.lilico.database.AppDataBase
import io.outblock.lilico.database.Bookmark
import io.outblock.lilico.utils.*
import io.outblock.lilico.utils.extensions.res2String
import io.outblock.lilico.utils.extensions.res2color
import io.outblock.lilico.widgets.popup.PopupListView

class BookmarkPopupMenu(
    private val view: View,
    val bookmark: Bookmark,
) {

    private val activity = findActivity(view)

    fun show() {
        uiScope {
            popupMenu(
                view,
                context = activity,
                items = listOf(
                    if (bookmark.isStarted) PopupListView.ItemData(
                        R.string.cancel_favourite.res2String(),
                        iconRes = R.drawable.ic_selection_star,
                        iconTint = R.color.salmon_primary.res2color(),
                    ) else PopupListView.ItemData(
                        R.string.favourite.res2String(),
                        iconRes = R.drawable.ic_collection_star,
                        iconTint = R.color.salmon_primary.res2color()
                    ),
                    PopupListView.ItemData(
                        R.string.delete.res2String(),
                        iconRes = R.drawable.ic_trash,
                        iconTint = R.color.salmon_primary.res2color()
                    ),
                ),
                offsetY = -view.height / 2,
                offsetX = ScreenUtils.getScreenWidth() / 4,
                selectListener = { _, text -> onMenuItemClick(text) },
                isDialogMode = true,
            ).show()
        }
    }

    private fun onMenuItemClick(text: String): Boolean {
        ioScope {
            when (text) {
                R.string.cancel_favourite.res2String() -> {
                    bookmark.isStarted = false
                    AppDataBase.database().bookmarkDao().update(bookmark)
                }
                R.string.favourite.res2String() -> {
                    bookmark.isStarted = true
                    AppDataBase.database().bookmarkDao().update(bookmark)
                }
                R.string.delete.res2String() -> AppDataBase.database().bookmarkDao().delete(bookmark)
            }
        }
        return true
    }
}