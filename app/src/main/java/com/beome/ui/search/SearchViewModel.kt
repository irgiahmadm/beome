package com.beome.ui.search

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beome.model.Post
import com.beome.model.User
import com.beome.utilities.NetworkState
import com.google.firebase.firestore.ktx.toObject

class SearchViewModel : ViewModel() {
    private val listPost = MutableLiveData<List<Post>>()
    private val listUser = MutableLiveData<List<User>>()
    private val searchRepository = SearchRepository()
    private val _listPostState  = MutableLiveData<NetworkState>()
    val listPostState : LiveData<NetworkState>
        get() = _listPostState
    private val _listUserState  = MutableLiveData<NetworkState>()
    val listUserState : LiveData<NetworkState>
        get() = _listUserState

    fun getListPost(searchQuery : String) : LiveData<List<Post>>{
        Log.d("Search_query", searchQuery)
        _listPostState.postValue(NetworkState.LOADING)
        if (TextUtils.isEmpty(searchQuery)){
            Log.d("not_found_search_post", "not_found")
            _listPostState.postValue(NetworkState.NOT_FOUND)
        }else{
            searchRepository.getListPost()
                .whereGreaterThan("title", searchQuery)
                .whereLessThan("title", searchQuery+ "\uf8ff")
                .whereEqualTo("status", 1)
                .addSnapshotListener { querySnapshot, error ->
                    error?.let{
                        Log.e("err_get_recet_post", error.localizedMessage!!)
                        _listPostState.postValue(NetworkState.FAILED)
                        return@addSnapshotListener
                    }
                    val addedRecentPostList = mutableListOf<Post>()
                    querySnapshot?.let {
                        for (document in it){
                            val post = document.toObject<Post>()
                            addedRecentPostList.add(post)
                        }
                        Log.d("data_search_post", addedRecentPostList.toString())
                        listPost.value = addedRecentPostList
                    }
                }
            _listPostState.postValue(NetworkState.SUCCESS)

        }
        return listPost
    }

    fun getListUser(searchQuery : String) : LiveData<List<User>>{
        Log.d("Search_query", searchQuery)
        _listUserState.postValue(NetworkState.LOADING)
        if (TextUtils.isEmpty(searchQuery)){
            Log.d("not_found_search_user", "not_found")
            _listUserState.postValue(NetworkState.NOT_FOUND)
        }else{
            searchRepository.getListUser()
                .whereGreaterThan("username", searchQuery)
                .whereLessThan("username", searchQuery+ "\uf8ff")
                .whereEqualTo("userStatus", 1)
                .addSnapshotListener { querySnapshot, error ->
                    error?.let{
                        Log.e("err_get_recet_post", error.localizedMessage!!)
                        _listUserState.postValue(NetworkState.FAILED)
                        return@addSnapshotListener
                    }
                    val tempListUser = mutableListOf<User>()
                    querySnapshot?.let {
                        for (document in it){
                            val post = document.toObject<User>()
                            tempListUser.add(post)
                        }
                        Log.d("data_search_user", tempListUser.toString())
                        listUser.value = tempListUser
                    }
                }
            _listUserState.postValue(NetworkState.SUCCESS)

        }
        return listUser
    }


}