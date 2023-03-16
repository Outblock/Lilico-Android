package io.outblock.lilico.page.inbox

import io.outblock.lilico.network.model.InboxNft
import io.outblock.lilico.network.model.InboxResponse
import io.outblock.lilico.network.model.InboxToken
import io.outblock.lilico.utils.getInboxReadList
import io.outblock.lilico.utils.updateInboxReadListPref

suspend fun updateInboxReadList(inboxResponse: InboxResponse) {
    updateInboxReadListPref(inboxResponse.toReadList().joinToString(",") { it })
}

suspend fun countUnreadInbox(inboxResponse: InboxResponse): Int {
    val cacheReadList = getInboxReadList()
    val remoteList = inboxResponse.toReadList().toMutableList()
    remoteList.removeAll { cacheReadList.contains(it) }
    return remoteList.size
}

private fun InboxResponse.toReadList(): List<String> {
    val data = mutableListOf<String>()
    data.addAll(tokenList().map { it.tag() })
    data.addAll(nftList().map { it.tag() })
    return data
}

private fun InboxToken.tag() = "${coinSymbol}-${amount}"
private fun InboxNft.tag() = "${collectionAddress}-${tokenId}"