package com.beome.ui.authentication.signup

import androidx.lifecycle.*
import com.beome.model.User
import com.beome.utilities.NetworkState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignupViewModel : ViewModel(){
    private val registerRepo = SignupRepository()
    lateinit var registerState : LiveData<NetworkState>
    private val _registerRepository = MutableLiveData<SignupRepository>()

    fun setUpRegisterUser(){
        registerState = Transformations.switchMap(_registerRepository, SignupRepository::networkState)
        _registerRepository.postValue(SignupRepository())
    }

    fun registerUser(user: User) = viewModelScope.launch {
        withContext(Dispatchers.IO){
            registerRepo.registerUser(user)
        }
    }
}