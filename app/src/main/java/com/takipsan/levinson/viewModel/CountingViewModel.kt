package com.takipsan.levinson.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takipsan.levinson.Entities.Retrofit.Request.SayimEpcGonderme_Kor
import com.takipsan.levinson.Entities.Retrofit.Resource
import com.takipsan.levinson.Entities.Retrofit.Response.ApiResponseBase
import com.takipsan.levinson.Entities.Retrofit.Response.CountingEpc
import com.takipsan.levinson.Entities.Retrofit.Response.Login
import com.takipsan.levinson.Entities.Retrofit.Response.SayimListesi
import com.takipsan.levinson.NetworkManager
import com.takipsan.levinson.UserData
import com.takipsan.levinson.repository.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CountingViewModel @Inject constructor(private val apiRepository: ApiRepository) :
    ViewModel() {

    private val _countingList = MutableLiveData<Resource<ApiResponseBase<List<SayimListesi>>>>()
    var countingList: MutableLiveData<Resource<ApiResponseBase<List<SayimListesi>>>> = _countingList

    private val _countingEpc = MutableLiveData<Resource<ApiResponseBase<List<CountingEpc>>>>()
    var CountingEpc: MutableLiveData<Resource<ApiResponseBase<List<CountingEpc>>>> = _countingEpc

    private val _setBlinkEpc = MutableLiveData<Resource<ApiResponseBase<List<CountingEpc>>>>()
    var setBlinkEpc: MutableLiveData<Resource<ApiResponseBase<List<CountingEpc>>>> = _setBlinkEpc

    fun getCountingList(context: Context) {
        _countingList.postValue(Resource.loading(null))
        if (NetworkManager.isOnline(context)) {
            viewModelScope.launch(Dispatchers.IO) {
                val response = apiRepository.getCountingList(UserData.bearer!!)
                if (response.code() == 200) {
                    _countingList.postValue(Resource.success(response.body()))
                } else if (response.code() == 204) {
                    _countingList.postValue(Resource.nocontent(null))
                } else {
                    _countingList.postValue(Resource.forbidden(null))
                }
            }
        }else{
            _countingList.postValue(Resource.noInternet(null))
        }
    }

    fun setCountingEpc_bilink(context:Context,list: ArrayList<CountingEpc>, sayimID: Long) {
        val req: SayimEpcGonderme_Kor = SayimEpcGonderme_Kor(
            userId = UserData.UserID,
            countingId = sayimID,
            epc = list.mapIndexed { index, value -> index.toString() to value.epc }.toMap()
        )
        if (NetworkManager.isOnline(context)){
            _setBlinkEpc.postValue(Resource.loading(null))
            viewModelScope.launch(Dispatchers.IO) {
                val response = apiRepository.setCountingEpcBlink(UserData.bearer!!, req)
                if (response.code() == 200) {
                    _setBlinkEpc.postValue(Resource.success(response.body()))
                } else if (response.code() == 204) {
                    _setBlinkEpc.postValue(Resource.nocontent(null))
                } else {
                    _setBlinkEpc.postValue(Resource.forbidden(null))
                }

            }
        }

    }

    fun getCountingEpcs(context: Context,req: com.takipsan.levinson.Entities.Retrofit.Request.CountingEpc) {
        _countingEpc.postValue(Resource.loading(null))
        viewModelScope.launch(Dispatchers.IO) {
            val response = apiRepository.getCountingEpcList(UserData.bearer!!, req)
            if (response.code() == 200) {
                _countingEpc.postValue(Resource.success(response.body()))
            } else if (response.code() == 204) {
                _countingEpc.postValue(Resource.nocontent(null))
            } else {
                _countingEpc.postValue(Resource.forbidden(null))
            }
        }
    }

}