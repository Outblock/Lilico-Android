package io.outblock.lilico.utils.secret

import android.util.Base64
import com.nftco.flow.sdk.bytesToHex
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * 加密解密 key 和 iv 必须16位字符
 */


fun aesEncrypt(key: String, iv: String = "0102030405060708", message: String): String {
    val sKey = SecretKeySpec(key.parseKey(), "AES")
    val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
    cipher.init(Cipher.ENCRYPT_MODE, sKey, IvParameterSpec(iv.parseKey()))
    return Base64.encodeToString(cipher.doFinal(message.toByteArray()), Base64.NO_WRAP)
}

fun aesDecrypt(key: String, iv: String = "0102030405060708", message: String): String {
    val sKey = SecretKeySpec(key.parseKey(), "AES")
    val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
    val data = Base64.decode(message, Base64.NO_WRAP)
    cipher.init(Cipher.DECRYPT_MODE, sKey, IvParameterSpec(iv.parseKey()))
    return String(cipher.doFinal(data))
}

private fun String.parseKey(): ByteArray = padEnd(16, '0').toByteArray().take(16).toByteArray()