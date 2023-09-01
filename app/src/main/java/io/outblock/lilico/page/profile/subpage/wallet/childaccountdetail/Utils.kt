package io.outblock.lilico.page.profile.subpage.wallet.childaccountdetail

import io.outblock.lilico.manager.childaccount.ChildAccount
import io.outblock.lilico.manager.coin.FlowCoin
import io.outblock.lilico.manager.flowjvm.CADENCE_QUERY_CHILD_ACCOUNT_NFT
import io.outblock.lilico.manager.flowjvm.CADENCE_QUERY_CHILD_ACCOUNT_TOKENS
import io.outblock.lilico.manager.flowjvm.executeCadence
import io.outblock.lilico.manager.wallet.WalletManager
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Float.parseFloat


fun queryChildAccountNftCollections(account: ChildAccount): List<CollectionData> {
    val walletAddress = WalletManager.wallet()?.walletAddress() ?: return emptyList()
    val response = CADENCE_QUERY_CHILD_ACCOUNT_NFT.executeCadence {
        arg { address(walletAddress) }
        arg { address(account.address) }
// for test
//        arg { address("0x7179def56a8b9c5e") }
//        arg { address("0xa3897cee18b350ea") }
    }
    response ?: return emptyList()
    return parseJson(response.stringValue)
}

fun queryChildAccountTokens(account: ChildAccount): List<TokenData> {
    val walletAddress = WalletManager.wallet()?.walletAddress() ?: return emptyList()
    val response = CADENCE_QUERY_CHILD_ACCOUNT_TOKENS.executeCadence {
        arg { address(walletAddress) }
        arg { address(account.address) }
    }
    response ?: return emptyList()
    return parseTokenList(response.stringValue)
}

data class CoinData(
    val coinName: String,
    val coinIcon: String,
    val balance: Float
)

data class TokenData(
    val id: String,
    val balance: Float
)

data class CollectionData(
    val id: String,
    val path: String,
    val display: DisplayData,
    val idList: List<String>
)

data class DisplayData(
    val name: String,
    val squareImage: String,
    val mediaType: String
)

fun parseJson(json: String): List<CollectionData> {
    val list = mutableListOf<CollectionData>()

    val root = JSONObject(json)
    val valueArray = root.optJSONArray("value") ?: JSONArray()
    for (i in 0 until valueArray.length()) {
        val collection = valueArray.getJSONObject(i)
        val fields = collection.optJSONObject("value")?.optJSONArray("fields") ?: JSONArray()

        var id = ""
        var path = ""
        var display = DisplayData("", "", "")
        val idList = mutableListOf<String>()

        for (j in 0 until fields.length()) {
            val field = fields.getJSONObject(j)
            val fieldName = field.optString("name")

            when (fieldName) {
                "id" -> id = field.optJSONObject("value")?.optString("value") ?: ""
                "path" -> path = field.optJSONObject("value")?.optString("value") ?: ""
                "display" -> {
                    val displayFields =
                        field.optJSONObject("value")?.optJSONObject("value")?.optJSONObject("value")
                            ?.optJSONArray("fields") ?: JSONArray()

                    var name = ""
                    var squareImage = ""
                    var mediaType = ""

                    for (k in 0 until displayFields.length()) {
                        val displayField = displayFields.getJSONObject(k)
                        val displayName = displayField.optString("name")

                        when (displayName) {
                            "name" -> name =
                                displayField.optJSONObject("value")?.optString("value") ?: ""

                            "squareImage" -> squareImage =
                                displayField.optJSONObject("value")?.optString("value") ?: ""

                            "mediaType" -> mediaType =
                                displayField.optJSONObject("value")?.optString("value") ?: ""
                        }
                    }

                    display = DisplayData(name, squareImage, mediaType)
                }

                "idList" -> {
                    val idListArray =
                        field.optJSONObject("value")?.optJSONArray("value") ?: JSONArray()
                    for (l in 0 until idListArray.length()) {
                        val idItem = idListArray.getJSONObject(l)
                        idList.add(idItem.optString("value", ""))
                    }
                }
            }
        }

        val collectionData = CollectionData(id, path, display, idList)
        list.add(collectionData)
    }

    return list
}

fun parseTokenList(json: String): List<TokenData> {
    val list = mutableListOf<TokenData>()

    val root = JSONObject(json)
    val valueArray = root.optJSONArray("value") ?: JSONArray()
    for (i in 0 until valueArray.length()) {
        val collection = valueArray.getJSONObject(i)
        val fields = collection.optJSONObject("value")?.optJSONArray("fields") ?: JSONArray()

        var id = ""
        var balance = 0f

        for (j in 0 until fields.length()) {
            val field = fields.getJSONObject(j)

            when (field.optString("name")) {
                "id" -> id = field.optJSONObject("value")?.optString("value") ?: ""
                "balance" -> balance = field.optJSONObject("value")?.optString("value")?.toFloatOrNull() ?: 0f
            }
        }

        val tokenData = TokenData(id, balance)
        list.add(tokenData)
    }

    return list
}
