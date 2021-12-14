package io.outblock.lilico.network

import io.outblock.lilico.network.model.OutblockUser
import retrofit2.http.POST

interface ApiService {

    @POST("users")
    suspend fun createUser(): OutblockUser

    @POST("/user/wallet")
    suspend fun createWallet(): OutblockUser
}