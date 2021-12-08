package io.outblock.lilico.page.main

import android.os.Bundle
import io.outblock.lilico.R
import io.outblock.lilico.base.activity.BaseActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        CoroutineScope(Dispatchers.IO).launch {
            testScript()
        }
    }

    fun testScript() {
//        println("===========> method: testScript()")
//        val accessApi = Flow.newAccessApi("access.devnet.nodes.onflow.org", 9000)
//        val response = accessApi.simpleFlowScript {
//            script = FlowScript(
//                script = """
//                transaction {
//                  execute {
//                    log("A transaction happened")
//                  }
//                }·
//            """.trimIndent()
//            )
//        }
//        println("===========> response:${response}")
    }
}