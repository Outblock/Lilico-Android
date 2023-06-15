package io.outblock.lilico.manager.childaccount

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import com.nftco.flow.sdk.FlowScriptResponse

class DataClasses {
    data class Root(
        @SerializedName("value")
        val value: List<ValueItem>
    )

    data class ValueItem(
        @SerializedName("key")
        val key: ValueContainer,
        @SerializedName("value")
        val value: ComplexValue
    )

    data class ValueContainer(
        @SerializedName("value")
        val value: String
    )

    data class ComplexValue(
        @SerializedName("value")
        val value: ComplexValue1?
    )

    data class ComplexValue1(
        @SerializedName("value")
        val value: FieldContainer?
    )

    data class FieldContainer(
        @SerializedName("fields")
        val fields: List<FieldItem>?
    )

    data class FieldItem(
        @SerializedName("name")
        val name: String,
        @SerializedName("value")
        val value: ValueOrNested
    )

    data class ValueOrNested(
        @SerializedName("value")
        val value: JsonElement
    )
}

fun FlowScriptResponse.parseAccountMetas(): List<ChildAccount> {
    val root = Gson().fromJson(String(bytes), DataClasses.Root::class.java)

    return root.value.map { valueItem ->
        val address = valueItem.key.value
        var name: String? = null
        var icon: String? = null

        valueItem.value.value?.value?.fields?.forEach { fieldItem ->
            when (fieldItem.name) {
                "name" -> {
                    // 这里需要检查value是否是一个JsonPrimitive（对应一个String）
                    if (fieldItem.value.value.isJsonPrimitive) {
                        name = fieldItem.value.value.asString
                    }
                }

                "thumbnail" -> {
                    // 这里需要检查value是否是一个JsonObject（对应一个嵌套的对象）
                    if (fieldItem.value.value.isJsonObject) {
                        val thumbnailFields = Gson().fromJson(fieldItem.value.value, DataClasses.FieldContainer::class.java)
                        thumbnailFields.fields?.firstOrNull { it.name == "url" }?.value?.value?.let {
                            if (it.isJsonPrimitive) {
                                icon = it.asString
                            }
                        }
                    }
                }
            }
        }

        // 在此处，变量address，name和icon应该已经被正确地赋值
        println("address: $address, name: $name, icon: $icon")
        ChildAccount(
            address = address,
            name = name.orEmpty(),
            icon = icon.orEmpty(),
        )
    }
}
