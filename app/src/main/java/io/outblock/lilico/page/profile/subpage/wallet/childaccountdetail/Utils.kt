package io.outblock.lilico.page.profile.subpage.wallet.childaccountdetail

import io.outblock.lilico.manager.childaccount.ChildAccount
import io.outblock.lilico.manager.flowjvm.CADENCE_QUERY_CHILD_ACCOUNT_NFT
import io.outblock.lilico.manager.flowjvm.CADENCE_QUERY_CHILD_ACCOUNT_TOKENS
import io.outblock.lilico.manager.flowjvm.executeCadence
import io.outblock.lilico.manager.wallet.WalletManager
import org.json.JSONArray
import org.json.JSONObject


fun queryChildAccountNftCollections(account: ChildAccount): List<NFTCollectionData> {
    val walletAddress = WalletManager.wallet()?.walletAddress() ?: return emptyList()
    val response = CADENCE_QUERY_CHILD_ACCOUNT_NFT.executeCadence {
        arg { address(walletAddress) }
        arg { address(account.address) }
// for test
//        arg { address("0x84221fe0294044d7") }
//        arg { address("0x16c41a2b76dee69b") }
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
    val name: String,
    val icon: String,
    val symbol: String,
    val balance: Float
)

data class TokenData(
    val id: String,
    val balance: Float
)

data class CollectionData(
    val id: String,
    val name: String,
    val logo: String,
    val path: String,
    val contractName: String,
    val idList: List<String>
)

data class NFTCollectionData(
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

fun parseJson(json: String): List<NFTCollectionData> {
    val list = mutableListOf<NFTCollectionData>()

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

            when (field.optString("name")) {
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

                        when (displayField.optString("name")) {
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

        val nftCollectionData = NFTCollectionData(id, path, display, idList)
        list.add(nftCollectionData)
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
