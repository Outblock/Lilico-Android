package io.outblock.lilico.network

import io.outblock.lilico.network.model.*
import retrofit2.http.*

interface ApiService {

    @POST("/register")
    suspend fun register(@Body param: RegisterRequest): RegisterResponse

    @POST("/user/wallet")
    suspend fun createWallet(): CreateWalletResponse

    @GET("/user/check/{username}")
    suspend fun checkUsername(@Path("username") username: String): UsernameCheckResponse

    @POST("/user/token")
    suspend fun uploadPushToken(@Field("push_token") token: String): CommonResponse
}