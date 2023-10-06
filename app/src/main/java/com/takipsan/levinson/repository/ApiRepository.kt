package com.takipsan.levinson.repository

import com.takipsan.levinson.DataAccess.Retrofit.Context.ApiService
import com.takipsan.levinson.Entities.Retrofit.Request.ConsignmentEpc
import com.takipsan.levinson.Entities.Retrofit.Request.CountingEpc
import com.takipsan.levinson.Entities.Retrofit.Request.Login
import com.takipsan.levinson.Entities.Retrofit.Request.SayimEpcGonderme_Kor
import com.takipsan.levinson.Entities.Retrofit.Request.SevkiyatEpcGonderme_Kor
import com.takipsan.levinson.Entities.Retrofit.Request.UrunArama
import com.takipsan.levinson.Entities.Retrofit.Response.ApiResponseBase
import com.takipsan.levinson.Entities.Retrofit.Response.SayimListesi
import com.takipsan.levinson.Entities.Retrofit.Response.SevkiyatListesi
import com.takipsan.levinson.UserData
import retrofit2.Response
import javax.inject.Inject

class ApiRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun login(loginObj: Login): Response<ApiResponseBase<com.takipsan.levinson.Entities.Retrofit.Response.Login>> {

        return apiService.login("", loginObj);
    }

    suspend fun getCountingList(auth: String): Response<ApiResponseBase<List<SayimListesi>>> {
        return apiService.countingList(auth)
    }

    suspend fun getConsigmentList(auth: String): Response<ApiResponseBase<List<SevkiyatListesi>>> {
        return apiService.consignmentList(auth)
    }

    suspend fun getCountingEpcList(
        auth: String,
        countingEpc: CountingEpc
    ): Response<ApiResponseBase<List<com.takipsan.levinson.Entities.Retrofit.Response.CountingEpc>>> {
        return apiService.countingEpcList(auth, countingEpc)
    }

    suspend fun setCountingEpcBlink(
        auth: String,
        countingEpc: SayimEpcGonderme_Kor
    ): Response<ApiResponseBase<List<com.takipsan.levinson.Entities.Retrofit.Response.CountingEpc>>> {
        return apiService.setCountingEpcBlink(auth, countingEpc)
    }

    suspend fun getUrunArama(
        auth: String,
        keyword: UrunArama
    ): Response<ApiResponseBase<List<com.takipsan.levinson.Entities.Retrofit.Response.UrunArama>>> {
        return apiService.getProduct(auth, keyword)
    }

    suspend fun getConsigmentEpcList(
        auth: String,
        consigment: ConsignmentEpc
    ): Response<ApiResponseBase<List<com.takipsan.levinson.Entities.Retrofit.Response.ConsigmentEpc>>> {
        return apiService.consignmentEpcList(auth, consigment)
    }

    suspend fun setConsigmentEpcList(
        auth: String,
        epc: SevkiyatEpcGonderme_Kor
    ): Response<ApiResponseBase<List<com.takipsan.levinson.Entities.Retrofit.Response.ConsigmentEpc>>> {
        return apiService.SetconsignmentBlind(auth, epc)
    }
}