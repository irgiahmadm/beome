package com.beome.ui.search

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beome.model.LikedPostList
import com.beome.model.Post
import com.beome.model.User
import com.beome.utilities.NetworkState
import com.google.firebase.firestore.ktx.toObject
import java.util.*

class SearchViewModel : ViewModel() {
    private val listPost = MutableLiveData<List<LikedPostList>>()
    private val listUser = MutableLiveData<List<User>>()
    private val searchRepository = SearchRepository()
    private val _listPostState  = MutableLiveData<NetworkState>()
    val listPostState : LiveData<NetworkState>
        get() = _listPostState
    private val _listUserState  = MutableLiveData<NetworkState>()
    val listUserState : LiveData<NetworkState>
        get() = _listUserState

    fun getListPost(searchQuery : String, idUser : String) : LiveData<List<LikedPostList>>{
        Log.d("Search_query", searchQuery)
        _listPostState.postValue(NetworkState.LOADING)
        var addedRecentPostList = mutableListOf<LikedPostList>()
        if (TextUtils.isEmpty(searchQuery)){
            Log.d("not_found_search_post", "not_found")
            _listPostState.postValue(NetworkState.NOT_FOUND)
            addedRecentPostList = arrayListOf()
            listPost.value = addedRecentPostList
        }else{

            searchRepository.getListPost()
                .whereArrayContains("searchKeyword", searchQuery.toLowerCase(Locale.getDefault()))
                .whereEqualTo("status", 1)
                .get()
                .addOnSuccessListener {
                    if(it != null){
                        for (document in it){
                            var isExist: Boolean
                            val post = document.toObject<Post>()
                            isExist  = post.likedBy.any { likedBy ->
                                likedBy == idUser
                            }
                            val likedPostObj = LikedPostList(post, isExist)
                            addedRecentPostList.add(likedPostObj)
                        }
                        listPost.value = addedRecentPostList
                        _listPostState.postValue(NetworkState.SUCCESS)
                    }
                }
                .addOnFailureListener {
                    Log.e("err_get_recet_post", it.localizedMessage!!)
                    _listPostState.postValue(NetworkState.FAILED)
                }
        }
        return listPost
    }

    fun getListUser(searchQuery : String) : LiveData<List<User>>{
        Log.d("Search_query", searchQuery)
        _listUserState.postValue(NetworkState.LOADING)
        var tempListUser = mutableListOf<User>()
        if (TextUtils.isEmpty(searchQuery)){
            Log.d("not_found_search_user", "not_found")
            _listUserState.postValue(NetworkState.NOT_FOUND)
            tempListUser = arrayListOf()
            listUser.value = tempListUser
        }else{
            searchRepository.getListUser()
                .whereGreaterThanOrEqualTo("username", searchQuery.toLowerCase(Locale.getDefault()))
                .whereLessThanOrEqualTo("username", searchQuery.toLowerCase(Locale.getDefault()) + "\uf8ff")
                .whereEqualTo("userStatus", 1)
                .get()
                .addOnSuccessListener {
                    if(it != null){
                        for (document in it){
                            val post = document.toObject<User>()
                            tempListUser.add(post)
                        }
                        listUser.value = tempListUser
                        _listUserState.postValue(NetworkState.SUCCESS)
                    }
                }
                .addOnFailureListener {
                    _listUserState.postValue(NetworkState.FAILED)
                    Log.e("err_get_recet_post", it.localizedMessage!!)
                }
        }
        return listUser
    }


}