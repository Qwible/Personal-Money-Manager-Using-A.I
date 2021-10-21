package com.example.qwibBank.TrueLayer

import com.example.qwibBank.Entities.AccessToken
import retrofit2.Response
import retrofit2.http.*
import retrofit2.http.POST
import retrofit2.http.FormUrlEncoded


interface AuthApi {
    @FormUrlEncoded
    @POST("/connect/token")
    suspend fun getAccessToken(
        @Field("grant_type") grantType: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("code") code: String
    ) : Response<AccessToken>

    @FormUrlEncoded
    @POST("/connect/token")
    suspend fun refreshAccessToken(
        @Field("grant_type") grantType: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("refresh_token") refresh_token: String
    ) : Response<AccessToken>
}



