package com.beome.ui.authentication.login

import android.app.Activity
import androidx.lifecycle.*
import com.beome.utilities.NetworkState

class LoginViewModel(activity : Activity) : ViewModel() {
    private val loginRepo = LoginRepository(activity, viewModelScope)
    lateinit var loginState : LiveData<NetworkState>
    private val _loginRepository = MutableLiveData<LoginRepository>()

    fun setUpLoginUser(){
        loginState = Transformations.switchMap(_loginRepository, LoginRepository::networkState)
        _loginRepository.postValue(loginRepo)
    }

    fun loginUser(email: String, password : String) =
        loginRepo.loginUser(email, password)

}