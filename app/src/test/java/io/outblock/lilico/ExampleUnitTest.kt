package io.outblock.lilico

import org.junit.Assert.assertEquals
import org.junit.Test
import org.onflow.sdk.Flow
import org.onflow.sdk.FlowScript
import org.onflow.sdk.simpleFlowScript

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testScript() {
        println("===========> method: testScript()")
        val accessApi = Flow.newAccessApi("access.mainnet.nodes.onflow.org", 9000)
        println("===========> start ping")
        accessApi.ping()
        println("===========> end ping")
        val response = accessApi.simpleFlowScript {
            script {
                """
                    pub fun main(): String {
                        return "Hello World"
                    }
                """
            }
        }
        println("===========> response:${response}")
    }

    companion object{
        const val HOST_TESTNET = "access.devnet.nodes.onflow.org"
    }
}