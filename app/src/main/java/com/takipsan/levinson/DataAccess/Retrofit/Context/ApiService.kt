package com.takipsan.levinson.DataAccess.Retrofit.Context

import com.takipsan.levinson.Entities.Retrofit.Request.ConsignmentEpc
import com.takipsan.levinson.Entities.Retrofit.Request.CountingEpc
import com.takipsan.levinson.Entities.Retrofit.Request.Login
import com.takipsan.levinson.Entities.Retrofit.Request.SayimEpcGonderme_Kor
import com.takipsan.levinson.Entities.Retrofit.Request.SevkiyatEpcGonderme_Kor
import com.takipsan.levinson.Entities.Retrofit.Request.UrunArama
import com.takipsan.levinson.Entities.Retrofit.Response.ApiResponseBase
import com.takipsan.levinson.Entities.Retrofit.Response.ConsigmentEpc
import com.takipsan.levinson.Entities.Retrofit.Response.SayimListesi
import com.takipsan.levinson.Entities.Retrofit.Response.SevkiyatListesi
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("login")
    suspend fun login(
        @Header("Authorization")
        authorizationHeader: String,
        @Body
        login: Login
    ): Response<ApiResponseBase<com.takipsan.levinson.Entities.Retrofit.Response.Login>>

    @GET("counting/list")
    suspend fun countingList(
        @Header("Authorization")
        authorizationHeader: String,
    ): Response<ApiResponseBase<List<SayimListesi>>>

    @POST("counting/epc-list")
    suspend fun countingEpcList(
        @Header("Authorization")
        authorizationHeader: String,
        @Body
        body:CountingEpc
    ): Response<ApiResponseBase<List<com.takipsan.levinson.Entities.Retrofit.Response.CountingEpc>>>

    @POST("counting/blind")
    suspend fun setCountingEpcBlink(
        @Header("Authorization")
        authorizationHeader: String,
        @Body
        body:SayimEpcGonderme_Kor
    ): Response<ApiResponseBase<List<com.takipsan.levinson.Entities.Retrofit.Response.CountingEpc>>>

    @POST("product/search")
    suspend fun getProduct(
        @Header("Authorization")
        authorizationHeader: String,
        @Body
        body:UrunArama
    ): Response<ApiResponseBase<List<com.takipsan.levinson.Entities.Retrofit.Response.UrunArama>>>

    @GET("consignment/list")
    suspend fun consignmentList(
        @Header("Authorization")
        authorizationHeader: String,
    ): Response<ApiResponseBase<List<SevkiyatListesi>>>

    @POST("consignment/epc-list")
    suspend fun consignmentEpcList(
        @Header("Authorization")
        authorizationHeader: String,
        @Body
        body:ConsignmentEpc
    ): Response<ApiResponseBase<List<ConsigmentEpc>>>

    @POST("consignment/blind")
    suspend fun SetconsignmentBlind(
        @Header("Authorization")
        authorizationHeader: String,
        @Body
        body:SevkiyatEpcGonderme_Kor
    ): Response<ApiResponseBase<List<ConsigmentEpc>>>
}