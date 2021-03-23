package com.beome.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beome.model.User
import com.beome.utilities.NetworkState
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Dispatchers

class ProfileViewModel : ViewModel() {
    private val profileUser = MutableLiveData<User>()
    private val profileRepo = ProfileRepository(Dispatchers.IO)
    private val userState  = MutableLiveData<NetworkState>()
    val _userState : LiveData<NetworkState>
        get() = userState

    fun getProfileUser(authKey : String) : LiveData<User>{
        userState.postValue(NetworkState.LOADING)
        profileRepo.getUserProfile()
            .whereEqualTo("authKey", authKey)
            .whereEqualTo("userStatus", 1)
            .addSnapshotListener { value, error ->
                error?.let {
                    Log.e("err_get_recet_post", error.localizedMessage!!)
                    userState.postValue(NetworkState.FAILED)
                    return@addSnapshotListener
                }
                var tempUser: User
                value?.let {
                    if(value.documents[0].exists()){
                        val user = value.documents[0].toObject<User>()
                        tempUser = user!!
                        profileUser.value = tempUser
                        userState.postValue(NetworkState.SUCCESS)
                    }else{
                        userState.postValue(NetworkState.NOT_FOUND)
                    }
                }
            }
        return profileUser
    }
}