package com.beome.ui.authentication.signup

import android.util.Log
import androidx.lifecycle.*
import com.beome.model.User
import com.beome.utilities.NetworkState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignupViewModel : ViewModel(){
    private val registerRepo = SignupRepository(viewModelScope)
    lateinit var registerState : LiveData<NetworkState>
    lateinit var isUsernameExist : LiveData<Boolean>
    lateinit var isEmailExist : LiveData<Boolean>
    private val _registerRepository = MutableLiveData<SignupRepository>()

    fun setUpRepoRegister(){
        _registerRepository.postValue(registerRepo)
    }
    fun setUpRegisterUser(){
        registerState = Transformations.switchMap(_registerRepository, SignupRepository::networkState)
    }

    fun registerUser(user: User) = viewModelScope.launch {
        withContext(Dispatchers.IO){
            registerRepo.registerUser(user)
        }
    }

    fun setupIsEmailExist(){
        isEmailExist = Transformations.switchMap(_registerRepository, SignupRepository::isEmailExist)
    }

    fun isEmailExist(email : String) =
        registerRepo.isEmailExist(email)


    fun setupIsUsernameExist(){
        isUsernameExist = Transformations.switchMap(_registerRepository, SignupRepository::isUserNameExist)
    }

    fun isUsernameExist(username : String) = registerRepo.isUsernameExist(username)

}