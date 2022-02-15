package io.outblock.lilico.firebase.storage

import android.graphics.Bitmap
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import io.outblock.lilico.utils.extensions.removeUrlParams
import io.outblock.lilico.utils.getUsername
import io.outblock.lilico.utils.loge
import java.io.ByteArrayOutputStream


suspend fun uploadAvatarToFirebase(image: Bitmap, callback: (url: String?) -> Unit) {
    val baos = ByteArrayOutputStream()
    image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    val data = baos.toByteArray()

    val ref = Firebase.storage.reference.child("avatar/${getUsername()}-${System.currentTimeMillis()}.jpg")
    val uploadTask = ref.putBytes(data)
    uploadTask.continueWithTask { task -> ref.downloadUrl }
        .addOnCompleteListener { task ->
            callback.invoke(task.result.toString().removeUrlParams())
        }.addOnFailureListener {
            loge(it)
            callback.invoke(null)
        }
}

fun String.firebaseImage(): String {
    if (!this.startsWith("https://firebasestorage.googleapis.com")) {
        return this
    }

    if (this.contains("alt=media")) {
        return this
    }
    return "$this?alt=media"
}