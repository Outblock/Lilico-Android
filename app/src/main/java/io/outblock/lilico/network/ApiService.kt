package io.outblock.lilico.network

import io.outblock.lilico.network.model.CreateWalletResponse
import io.outblock.lilico.network.model.RegisterRequest
import io.outblock.lilico.network.model.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("/register")
    suspend fun register(@Body param: RegisterRequest): RegisterResponse

    @POST("/user/wallet")
    suspend fun createWallet(): CreateWalletResponse
}