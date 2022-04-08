package io.outblock.lilico.network

import io.outblock.lilico.network.model.CryptowatchPriceResponse
import io.outblock.lilico.network.model.CryptowatchSummaryResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiCryptowatchService {

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
