package io.outblock.lilico.network

import io.outblock.lilico.network.model.InboxResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface OtherHostService {

    @GET("/api/data/domain/{domain}")
    suspend fun queryInbox(@Path("domain") domain: String): InboxResponse
}