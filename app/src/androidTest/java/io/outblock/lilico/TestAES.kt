package io.outblock.lilico

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.outblock.lilico.utils.secret.aesDecrypt
import io.outblock.lilico.utils.secret.aesEncrypt
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TestAES {

    @Test
    fun aesTest() {
        val key = "0102030405060708"
        val iv = "0102030405060708"
        val message = "hello world"
        val encrypt = aesEncrypt(key, iv, message)
        val decrypt = aesDecrypt(key, iv, encrypt)
        println("encrypt:$encrypt")
        assert(message == decrypt)
    }
}