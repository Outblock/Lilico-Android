package io.outblock.lilico.manager.flowjvm.transaction.resolve

import io.outblock.lilico.manager.flowjvm.transaction.Interaction
import io.outblock.lilico.manager.flowjvm.transaction.Roles
import io.outblock.lilico.manager.flowjvm.transaction.SignableUser


class AccountsResolver : Resolver {

    override suspend fun resolve(ix: Interaction) {
        collectAccounts(ix)
    }

    private fun collectAccounts(ix: Interaction) {


        val signableUsers = listOf(
            SignableUser(
                addr = ix.proposer,
                role = Roles(proposer = true),
            ),
            SignableUser(
                addr = ix.payer,
                role = Roles(payer = true)
            ),
            SignableUser(
                addr = ix.account.addr,
                role = Roles(authorizer = true)
            ),
        )
        val accounts = mutableMapOf<String, SignableUser>()

        ix.authorizations.clear()

        signableUsers.forEach { user ->
            val tempID = "${user.addr}-${user.keyId}"
            user.tempId = tempID

            if (accounts.keys.contains(tempID)) {
                accounts[tempID]?.role?.merge(user.role)
            }

            accounts[tempID] = user

            if (user.role.proposer) {
                ix.proposer = tempID
            }

            if (user.role.payer) {
                ix.payer = tempID
            }

            if (user.role.authorizer) {
                ix.authorizations.add(tempID)
            }
        }


        ix.accounts = accounts
    }
}