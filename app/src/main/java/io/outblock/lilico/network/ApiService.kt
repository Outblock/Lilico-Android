package io.outblock.lilico.network

import io.outblock.lilico.network.model.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("/register")
    suspend fun register(@Body param: RegisterRequest): RegisterResponse

    @POST("/user/address")
    suspend fun createWallet(): CreateWalletResponse

    @GET("/user/check")
    suspend fun checkUsername(@Query("username") username: String): UsernameCheckResponse

    @POST("/user/token")
    suspend fun uploadPushToken(@Body token: Map<String, String>): CommonResponse

    @GET("/user/wallet")
    suspend fun getWalletList(): WalletListResponse

    @POST("/login")
    suspend fun login(@Body params: Map<String, String>): LoginResponse

    @GET("/account/info")
    suspend fun getAddressInfo(@Query("address") address: String): AddressInfoResponse

    @GET("/nft/list")
    suspend fun nftList(
        @Query("address") address: String,
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 100,
    ): NFTListResponse

    @GET("/nft/meta")
    suspend fun nftMeta(
        @Query("address") address: String,
        @Query("contractName") contractName: String,
        @Query("contractAddress") contractAddress: String,
        @Query("tokenId") tokenId: String,
    ): CommonResponse

    @GET("/user/info")
    suspend fun userInfo(): UserInfoResponse
}