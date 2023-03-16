package io.outblock.lilico.manager.env

import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv

object EnvKey {

    private lateinit var dotEnv: Dotenv

    fun init() {
        dotEnv = dotenv {
            directory = "./assets/env"
            filename = "config"
            ignoreIfMalformed = true
            ignoreIfMissing = true
        }
    }

    fun get(key: String): String = dotEnv[key]
}