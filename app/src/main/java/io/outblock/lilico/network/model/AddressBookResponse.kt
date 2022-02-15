package io.outblock.lilico.network.model

import com.google.gson.annotations.SerializedName

data class AddressBookResponse(
    @SerializedName("data")
    val data: AddressBookContactBookList,

    @SerializedName("message")
    val message: String,

    @SerializedName("status")
    val status: Int,
)

data class AddressBookContactBookList(
    @SerializedName("contacts")
    val contacts: List<AddressBookContact>?,
)

data class AddressBookContact(
    @SerializedName("address")
    val address: String?,
    @SerializedName("avatar")
    val avatar: String?,
    @SerializedName("contact_name")
    val contactName: String?,
    @SerializedName("username")
    val username: String?,
    @SerializedName("domain")
    val domain: AddressBookDomain?,
    @SerializedName("id")
    val id: String?,
) {
    fun name(): String {
        return if (username.isNullOrEmpty()) contactName.orEmpty() else username
    }

    fun prefixName(): String {
        val name = name()
        if (name.isEmpty()) {
            return ""
        }
        return name.first().toString().uppercase()
    }
}

data class AddressBookDomain(
    @SerializedName("domain_type")
    val domainType: Int,
    @SerializedName("value")
    val value: String?,
) {
    companion object {
        const val DOMAIN_FIND_XYZ = 1
        const val DOMAIN_FLOWNS = 2
    }
}
