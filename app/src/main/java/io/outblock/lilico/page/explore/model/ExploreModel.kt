package io.outblock.lilico.page.explore.model

import io.outblock.lilico.database.Bookmark
import io.outblock.lilico.database.WebviewRecord

class ExploreModel(
    val recentList: List<WebviewRecord>? = null,
    val bookmarkList: List<Bookmark>? = null,
)