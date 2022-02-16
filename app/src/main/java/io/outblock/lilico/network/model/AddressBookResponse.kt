package io.outblock.lilico.network.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

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

@Parcelize
data class AddressBookContact(
    @SerializedName("address")
    val address: String? = null,
    @SerializedName("avatar")
    val avatar: String? = null,
    @SerializedName("contact_name")
    val contactName: String? = null,
    @SerializedName("username")
    val username: String? = null,
    @SerializedName("domain")
    val domain: AddressBookDomain? = null,
    @SerializedName("id")
    val id: String? = null,
) : Parcelable {
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

@Parcelize
data class AddressBookDomain(
    @SerializedName("domain_type")
    val domainType: Int,
    @SerializedName("value")
    val value: String? = null,
) : Parcelable {
    companion object {
        const val DOMAIN_FIND_XYZ = 1
        const val DOMAIN_FLOWNS = 2
    }
}
