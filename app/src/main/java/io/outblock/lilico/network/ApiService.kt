package io.outblock.lilico.network

import io.outblock.lilico.network.model.*
import retrofit2.http.*

interface ApiService {

    @POST("/v1/register")
    suspend fun register(@Body param: RegisterRequest): RegisterResponse

    @POST("/v1/user/address")
    suspend fun createWallet(): CreateWalletResponse

    @GET("/v1/user/check")
    suspend fun checkUsername(@Query("username") username: String): UsernameCheckResponse

    @POST("/v1/user/token")
    suspend fun uploadPushToken(@Body token: Map<String, String>): CommonResponse

    @GET("/v1/user/wallet")
    suspend fun getWalletList(): WalletListResponse

    @GET("/v1/user/search")
    suspend fun searchUser(@Query("keyword") keyword: String): SearchUserResponse

    @POST("/v2/login")
    suspend fun login(@Body params: Map<String, String>): LoginResponse

    @GET("/v1/account/info")
    suspend fun getAddressInfo(@Query("address") address: String): AddressInfoResponse

    @GET("/v2/nft/list")
    suspend fun nftList(
        @Query("address") address: String,
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 25,
    ): NFTListResponse

    @GET("/v1/nft/meta")
    suspend fun nftMeta(
        @Query("address") address: String,
        @Query("contractName") contractName: String,
        @Query("contractAddress") contractAddress: String,
        @Query("tokenId") tokenId: String,
    ): CommonResponse

    @GET("/v1/user/info")
    suspend fun userInfo(): UserInfoResponse

    @POST("/v1/profile")
    suspend fun updateProfile(@Body params: Map<String, String>): CommonResponse

    @GET("/v1/addressbook/contact")
    suspend fun getAddressBook(): AddressBookResponse

    @PUT("/v1/addressbook/external")
    @JvmSuppressWildcards
    suspend fun addAddressBookExternal(@Body params: Map<String, Any?>): CommonResponse

    @PUT("/v1/addressbook/contact")
    @JvmSuppressWildcards
    suspend fun addAddressBook(@Body params: Map<String, Any?>): CommonResponse

    @DELETE("/v1/addressbook/contact")
    suspend fun deleteAddressBook(@Query("id") contactId: String): CommonResponse

    @POST("/v1/addressbook/contact")
    @JvmSuppressWildcards
    suspend fun editAddressBook(@Body params: Map<String, Any>): CommonResponse

    @GET("/v1/coin/rate")
    suspend fun coinRate(@Query("coinId") coinId: Int): CoinRateResponse

    @GET("/v1/coin/map")
    suspend fun coinMap(): CoinMapResponse

    // @doc https://docs.cryptowat.ch/rest-api/
    // @example https://api.cryptowat.ch/markets/binance/btcusdt/price
    @GET("/markets/{market}/{pair}/price")
    suspend fun price(@Path("market") market: String, @Path("pair") coinPair: String): CryptowatchPriceResponse

    // @doc https://docs.cryptowat.ch/rest-api/markets/ohlc
    // @example https://api.cryptowat.ch/markets/binance/btcusdt/ohlc
    // @before @after Unix timestamp
    // @periods Comma separated integers. Only return these time periods. Example: 60,180,108000
    @GET("/markets/{market}/{pair}/ohlc")
    suspend fun ohlc(
        @Path("market") market: String,
        @Path("pair") coinPair: String,
        @Query("before") before: Long? = null,
        @Query("after") after: Long? = null,
        @Query("periods") periods: IntArray? = null,
    ): Map<String, Any>

    // @example https://api.cryptowat.ch/markets/binance/flowusdt/summary
    @GET("/markets/{market}/{pair}/summary")
    suspend fun summary(@Path("market") market: String, @Path("pair") coinPair: String): CryptowatchSummaryResponse
}