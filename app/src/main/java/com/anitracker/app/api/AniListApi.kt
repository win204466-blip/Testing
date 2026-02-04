package com.anitracker.app.api

import com.anitracker.app.model.MediaResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AniListApi {
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    @POST(".")
    suspend fun query(@Body body: GraphQLRequest): MediaResponse
}

data class GraphQLRequest(
    val query: String,
    val variables: Map<String, Any?> = emptyMap()
)
