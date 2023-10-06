package com.takipsan.levinson.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takipsan.levinson.DataAccess.Room.dao.ConsigmentDao
import com.takipsan.levinson.DataAccess.Room.dao.ConsigmentEpcDao
import com.takipsan.levinson.Entities.Retrofit.Request.ConsignmentEpc
import com.takipsan.levinson.Entities.Retrofit.Request.SevkiyatEpcGonderme_Kor
import com.takipsan.levinson.Entities.Retrofit.Resource
import com.takipsan.levinson.Entities.Retrofit.Response.ApiResponseBase
import com.takipsan.levinson.Entities.Retrofit.Response.ConsigmentEpc
import com.takipsan.levinson.Entities.Retrofit.Response.SevkiyatListesi
import com.takipsan.levinson.Entities.Retrofit.Response.UrunArama
import com.takipsan.levinson.Entities.Room.Consignment
import com.takipsan.levinson.Entities.Room.cosignmentEpcs
import com.takipsan.levinson.NetworkManager
import com.takipsan.levinson.UserData
import com.takipsan.levinson.repository.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class ConsigmentViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val consignmentDoa: ConsigmentDao,
    public val consigmentEpcDao: ConsigmentEpcDao
) : ViewModel() {
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        // Hata durumunda yapılacak işlemler burada
        // throwable, meydana gelen hata nesnesidir
        _consigmentList.postValue(Resource.forbidden(null))
        Log.e("Burakk", throwable.message.toString())

    }

    private val _consigmentList =
        MutableLiveData<Resource<ApiResponseBase<List<SevkiyatListesi>>>>()
    var consigmentList: MutableLiveData<Resource<ApiResponseBase<List<SevkiyatListesi>>>> =
        _consigmentList

    private val _epcList = MutableLiveData<Resource<ApiResponseBase<List<ConsigmentEpc>>>>()
    var epcList: MutableLiveData<Resource<ApiResponseBase<List<ConsigmentEpc>>>> = _epcList

    private val _SaveepcList = MutableLiveData<Resource<ApiResponseBase<List<ConsigmentEpc>>>>()
    var SaveepcList: MutableLiveData<Resource<ApiResponseBase<List<ConsigmentEpc>>>> = _SaveepcList

    fun getSevkiyatList(context: Context) {
        consigmentList.postValue(Resource.loading(null))
        if (NetworkManager.isOnline(context)) {

            viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
                val response = apiRepository.getConsigmentList(UserData.bearer!!)
                if (response.code() == 200) {
                    val r = response.body() as ApiResponseBase<List<SevkiyatListesi>>
                    if (r.code == "00") {
                        r.data.forEach { it ->
                            consignmentDoa.insert(
                                Consignment(
                                    id = it.id.toLong(),
                                    status = it.status,
                                    name = it.name,
                                    mode = it.mode,
                                    type = it.type,
                                    quantity = it.quantity
                                )
                            )
                        }
                    }
                    consigmentList.postValue(Resource.success(response.body()))
                } else if (response.code() == 204) {
                    consigmentList.postValue(Resource.nocontent(null))
                } else if (response.code() == 401) {
                    consigmentList.postValue(Resource.forbidden(null))

                } else {
                    consigmentList.postValue(
                        Resource.error(
                            if (response.body() != null) response.body()
                                .toString() else response.errorBody().toString(), null
                        )
                    )
                }
            }
        } else {
            consigmentList.postValue(Resource.noInternet(null))
        }
    }

    fun getSevkiyatEpcList(context: Context, req: ConsignmentEpc, countingID: Long) {
        _epcList.postValue(Resource.loading(null))
        if (NetworkManager.isOnline(context)) {

            viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
                val response = apiRepository.getConsigmentEpcList(UserData.bearer!!, req)
                if (response.code() == 200) {
                    _epcList.postValue(Resource.success(response.body()))
                } else if (response.code() == 204) {
                    _epcList.postValue(Resource.nocontent(null))
                } else if (response.code() == 401) {
                    _epcList.postValue(Resource.forbidden(null))

                } else {
                    _epcList.postValue(
                        Resource.error(
                            if (response.body() != null) response.body()
                                .toString() else response.errorBody().toString(), null
                        )
                    )
                }
            }
        } else {
            _epcList.postValue(Resource.noInternet(null))
        }
    }

    fun setSevkiyatKor(context: Context,sevkiyat:SevkiyatEpcGonderme_Kor){
        _SaveepcList.postValue(Resource.loading(null))
        if (NetworkManager.isOnline(context)) {

            viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
                val response = apiRepository.setConsigmentEpcList(UserData.bearer!!,sevkiyat)
                if (response.code() == 200) {
                    _SaveepcList.postValue(Resource.success(response.body()))
                } else if (response.code() == 204) {
                    _SaveepcList.postValue(Resource.nocontent(null))
                } else if (response.code() == 401) {
                    _SaveepcList.postValue(Resource.forbidden(null))

                } else {
                    _SaveepcList.postValue(
                        Resource.error(
                            if (response.body() != null) response.body()
                                .toString() else response.errorBody().toString(), null
                        )
                    )
                }
            }
        } else {
            _SaveepcList.postValue(Resource.noInternet(null))
        }
    }
}

