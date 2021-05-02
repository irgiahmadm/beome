package com.beome.ui.authentication.login

import android.app.Activity
import androidx.lifecycle.*
import com.beome.model.User
import com.beome.ui.authentication.signup.SignupRepository
import com.beome.utilities.NetworkState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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