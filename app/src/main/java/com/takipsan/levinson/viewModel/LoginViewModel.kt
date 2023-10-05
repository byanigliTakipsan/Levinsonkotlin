package com.takipsan.levinson.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takipsan.levinson.Entities.Retrofit.Resource
import com.takipsan.levinson.Entities.Retrofit.Response.ApiResponseBase
import com.takipsan.levinson.Entities.Retrofit.Response.Login
import com.takipsan.levinson.NetworkManager
import com.takipsan.levinson.repository.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val apiRepository: ApiRepository) : ViewModel() {
    private val _loginInfo = MutableLiveData<Resource<ApiResponseBase<Login>>>()
    var loginInfo: MutableLiveData<Resource<ApiResponseBase<Login>>> = _loginInfo


    fun getLoginInfo(
        context: Context,
        login: com.takipsan.levinson.Entities.Retrofit.Request.Login
    ) {
        if (NetworkManager.isOnline(context)) {
            _loginInfo.postValue(Resource.loading(null))
            viewModelScope.launch(Dispatchers.IO) {
                val response = apiRepository.login(login)
                if (response.code() == 200) {
                    _loginInfo.postValue(Resource.success(response.body()))
                } else if (response.code() == 204) {
                    _loginInfo.postValue(Resource.nocontent(null))
                } else {
                    _loginInfo.postValue(Resource.forbidden(null))
                }
            }
        }else {
            _loginInfo.postValue(Resource.noInternet(null))
        }
    }
}