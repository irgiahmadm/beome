package com.beome.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beome.model.Post
import com.beome.model.User
import com.beome.utilities.NetworkState
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Dispatchers

class ProfileViewModel : ViewModel() {
    private val profileUser = MutableLiveData<User>()
    private val profileRepo = ProfileRepository(Dispatchers.IO)
    private val listPostUser = MutableLiveData<List<Post>>()
    private val _userState  = MutableLiveData<NetworkState>()
    val userState : LiveData<NetworkState>
        get() = _userState
    private val _listPostState  = MutableLiveData<NetworkState>()
    val listPostState : LiveData<NetworkState>
        get() = _listPostState

    fun getProfileUser(authKey : String) : LiveData<User>{
        _userState.postValue(NetworkState.LOADING)
        profileRepo.getUserProfile()
            .whereEqualTo("authKey", authKey)
            .whereEqualTo("userStatus", 1)
            .addSnapshotListener { value, error ->
                error?.let {
                    Log.e("err_get_recet_post", error.localizedMessage!!)
                    _userState.postValue(NetworkState.FAILED)
                    return@addSnapshotListener
                }
                var tempUser: User
                value?.let {
                    if(value.documents[0].exists()){
                        val user = value.documents[0].toObject<User>()
                        tempUser = user!!
                        profileUser.value = tempUser
                        _userState.postValue(NetworkState.SUCCESS)
                    }else{
                        _userState.postValue(NetworkState.NOT_FOUND)
                    }
                }
            }
        return profileUser
    }

    fun getListPostUser(authKey: String) : LiveData<List<Post>>{
        profileRepo.getPostByUser()
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .whereEqualTo("status", 1)
            .whereEqualTo("authKey", authKey)
            .addSnapshotListener { querySnapshot, error ->
                error?.let{
                    Log.e("err_get_recet_post", error.localizedMessage!!)
                    return@addSnapshotListener
                }
                val tempListPostUser = mutableListOf<Post>()
                querySnapshot?.let {
                    for (document in it){
                        val post = document.toObject<Post>()
                        tempListPostUser.add(post)
                    }
                    listPostUser.value = tempListPostUser
                }
            }
        return listPostUser
    }
}