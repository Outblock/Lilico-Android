package io.outblock.lilico.page.main

import android.os.Bundle
import com.nftco.flow.sdk.Flow
import com.nftco.flow.sdk.simpleFlowScript
import io.outblock.lilico.R
import io.outblock.lilico.base.activity.BaseActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        CoroutineScope(Dispatchers.IO).launch {
//            testScript()
//        }
    }

    fun testScript() {
        println("===========> method: testScript()")
        val accessApi = Flow.newAccessApi("access.devnet.nodes.onflow.org", 9000)
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
}