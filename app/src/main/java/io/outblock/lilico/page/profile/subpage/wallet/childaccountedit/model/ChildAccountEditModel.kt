package io.outblock.lilico.page.profile.subpage.wallet.childaccountedit.model

import io.outblock.lilico.manager.childaccount.ChildAccount
import java.io.File

class ChildAccountEditModel(
    val account: ChildAccount? = null,
    val avatarFile: File? = null,
    var showProgressDialog: Boolean? = null,
)