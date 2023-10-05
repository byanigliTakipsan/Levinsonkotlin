package com.takipsan.levinson.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takipsan.levinson.Entities.Retrofit.Resource
import com.takipsan.levinson.Entities.Retrofit.Response.ApiResponseBase
import com.takipsan.levinson.Entities.Retrofit.Response.SayimListesi
import com.takipsan.levinson.Entities.Retrofit.Response.UrunArama
import com.takipsan.levinson.NetworkManager
import com.takipsan.levinson.UserData
import com.takipsan.levinson.repository.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UrunAramaViewModel @Inject constructor(private val apiRepository: ApiRepository) :
    ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        // Hata durumunda yapılacak işlemler burada
        // throwable, meydana gelen hata nesnesidir
        _urun.postValue(Resource.forbidden(null))

    }

    private val _urun = MutableLiveData<Resource<ApiResponseBase<List<UrunArama>>>>()
    var urun: MutableLiveData<Resource<ApiResponseBase<List<UrunArama>>>> = _urun


    private val _rssi = MutableLiveData<Double>(0.0)
    val rssi: LiveData<Double> get() = _rssi

    fun putRssi(i:Double){
        _rssi.postValue(i)
    }

    fun getUrunList(context: Context,urun:com.takipsan.levinson.Entities.Retrofit.Request.UrunArama) {
        _urun.postValue(Resource.loading(null))
        if (NetworkManager.isOnline(context)) {

            viewModelScope.launch(Dispatchers.IO+exceptionHandler) {
                val response = apiRepository.getUrunArama(UserData.bearer!!,urun)
                if (response.code() == 200) {
                    _urun.postValue(Resource.success(response.body()))
                } else if (response.code() == 204) {
                    _urun.postValue(Resource.nocontent(null))
                } else if (response.code() == 401) {
                    _urun.postValue(Resource.forbidden(null))

                } else {
                    _urun.postValue(
                        Resource.error(
                            if (response.body() != null) response.body()
                                .toString() else response.errorBody().toString(), null
                        )
                    )
                }
            }
        } else {
            _urun.postValue(Resource.noInternet(null))
        }
    }
}