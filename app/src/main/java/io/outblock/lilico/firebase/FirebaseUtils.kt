package io.outblock.lilico.firebase

import io.outblock.lilico.firebase.auth.firebaseJwt
import io.outblock.lilico.firebase.messaging.uploadPushToken


fun firebaseInformationCheck() {
    firebaseJwt()
    uploadPushToken()
}