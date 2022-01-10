package io.outblock.lilico.network

import io.outblock.lilico.network.model.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("/register")
    suspend fun register(@Body param: RegisterRequest): RegisterResponse

    @POST("/user/address")
    suspend fun createWallet(): CreateWalletResponse

    @GET("/user/check/{username}")
    suspend fun checkUsername(@Path("username") username: String): UsernameCheckResponse

    @POST("/user/token")
    suspend fun uploadPushToken(@Body token: Map<String, String>): CommonResponse

    @GET("/user/wallet")
    suspend fun getWalletList(): WalletListResponse

    @POST("/login")
    suspend fun login(@Body params: Map<String, String>): LoginResponse

    @GET("/account/info/{address}")
    suspend fun getAddressInfo(@Path("address") address: String): AddressInfoResponse
}