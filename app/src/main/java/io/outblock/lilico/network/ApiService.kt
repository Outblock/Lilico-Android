package io.outblock.lilico.network

import io.outblock.lilico.network.model.CreateWalletResponse
import io.outblock.lilico.network.model.RegisterRequest
import io.outblock.lilico.network.model.RegisterResponse
import io.outblock.lilico.network.model.UsernameCheckResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("/register")
    suspend fun register(@Body param: RegisterRequest): RegisterResponse

    @POST("/user/wallet")
    suspend fun createWallet(): CreateWalletResponse

    @GET("/user/check/{username}")
    suspend fun checkUsername(@Path("username") username: String): UsernameCheckResponse
}