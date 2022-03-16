package io.outblock.lilico.manager.flowjvm

import com.nftco.flow.sdk.FlowAddress
import com.nftco.flow.sdk.FlowScriptResponse
import com.nftco.flow.sdk.cadence.marshall
import com.nftco.flow.sdk.simpleFlowScript
import io.outblock.lilico.utils.logd
import io.outblock.lilico.utils.loge

class FlowJvmHelper {

    fun getFlownsAddress(domain: String, root: String = "fn"): String? {
        logd(TAG, "getFlownsAddress()")
        val result = FlowApi.get().simpleFlowScript {
            script {
                """
                  import Flowns from 0xFlowns
                  import Domains from 0xDomains
                  pub fun main(name: String, root: String) : Address? {
                    let prefix = "0x"
                    let rootHahsh = Flowns.hash(node: "", lable: root)
                    let namehash = prefix.concat(Flowns.hash(node: rootHahsh, lable: name))
                    var address = Domains.getRecords(namehash)
                    return address
                  }
                """.trimIndent()
            }
            arg { marshall { string(domain) } }
            arg { marshall { string(root) } }
        }
        logd(TAG, "getFlownsAddress response:${String(result.bytes)}")
        return result.parseSearchAddress()
    }

    fun getFlownsDomainsByAddress(address: String): FlowScriptResponse {
        val result = FlowApi.get().simpleFlowScript {
            script {
                """
                  import Domains from 0xDomains
                  // address: Flow address
                  pub fun main(address: Address): [Domains.DomainDetail] {
                    let account = getAccount(address)
                    let collectionCap = account.getCapability<&{Domains.CollectionPublic}>(Domains.CollectionPublicPath)
                    let collection = collectionCap.borrow()!
                    let domains:[Domains.DomainDetail] = []
                    let ids = collection.getIDs()
                    for id in ids {
                      let domain = collection.borrowDomain(id: id)
                      let detail = domain.getDetail()
                      domains.append(detail)
                    }
                    return domains
                  }
                """.trimIndent()
            }
            arg { address(address) }
        }
        logd(TAG, "getFlownsDomainsByAddress response:${String(result.bytes)}")
        return result
    }

    fun getFindAddress(domain: String): String? {
        logd(TAG, "getFindAddress()")
        val result = FlowApi.get().simpleFlowScript {
            script {
                """
                  import FIND from 0xFind
                  //Check the status of a fin user
                  pub fun main(name: String) : Address? {
                      let status=FIND.status(name)
                      return status.owner
                  }
                """.trimIndent()
            }
            arg { marshall { string(domain) } }
        }
        logd(TAG, "getFindAddress response:${String(result.bytes)}")
        return result.parseSearchAddress()
    }

    fun getFindDomainByAddress(address: String): FlowScriptResponse {
        val result = FlowApi.get().simpleFlowScript {
            script {
                """
                  import FIND from 0xFind
                  pub fun main(address: Address) : String?{
                    return FIND.reverseLookup(address)
                  }
                """.trimIndent()
            }
            arg { address(address) }
        }
        logd(TAG, "getFindDomainByAddress response:$result")
        return result
    }

    fun addressVerify(address: String): Boolean {
        if (!address.startsWith("0x")) {
            return false
        }
        return try {
            FlowApi.get().getAccountAtLatestBlock(FlowAddress(address)) != null
        } catch (e: Exception) {
            loge(e)
            false
        }
    }

    companion object {
        private val TAG = FlowJvmHelper::class.java.simpleName
    }
}