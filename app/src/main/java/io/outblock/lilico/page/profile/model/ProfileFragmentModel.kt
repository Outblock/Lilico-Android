package io.outblock.lilico.page.profile.model

import io.outblock.lilico.network.model.UserInfoData

class ProfileFragmentModel(
    val userInfo: UserInfoData? = null,
    val onResume: Boolean? = null,
    val inboxCount: Int? = null,
)