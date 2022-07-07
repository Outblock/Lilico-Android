package io.outblock.lilico.network.flowscan

private const val ADDRESS = "#address"
private const val TOKEN_CONTRACT_ID = "#contract-id"

private val ACCOUNT_TRANSFERS = """
   query AccountTransfers {
       account(id: "$ADDRESS") {
       transactions (
           first: 30 
           ordering: Descending
       ) {
           edges {
               node {
                   error
                   hash
                   status
                   eventCount
                   time
                   index
                   payer {
                       address
                   }
                   proposer {
                       address 
                   }
                   authorizers {
                       address
                   }
                   contractInteractions {
                       identifier
                   }
               }
           }
       }
       transactionCount
       }
   }
""".trimIndent()

private val TOKEN_TRANSFERS = """
   query AccountTransfers {
        account(id: "$ADDRESS") {
            tokenTransfers (
                first: 30 
                ordering: Descending
                contractId: "$TOKEN_CONTRACT_ID"
            ) {
                pageInfo {
                    hasNextPage
                    endCursor
                }
                edges {
                    node {
                        transaction {
                           error
                           hash
                           status
                           eventCount
                           time
                           index
                           payer {
                               address
                           }
                           proposer {
                               address 
                           }
                           authorizers {
                               address
                           }
                           contractInteractions {
                               identifier
                           }
                        }
                        type
                        amount {
                            token {
                                id
                            }
                            value
                        }
                        counterparty {
                            address
                        }
                        counterpartiesCount
                    }
                }
            }
        }
    }
""".trimIndent()


fun flowScanAccountTransferScript(address: String): String {
    return ACCOUNT_TRANSFERS.replace(ADDRESS, address)
}

fun flowScanTokenTransferScript(address: String, contractId: String): String {
    return TOKEN_TRANSFERS.replace(ADDRESS, address).replace(TOKEN_CONTRACT_ID, contractId)
}
