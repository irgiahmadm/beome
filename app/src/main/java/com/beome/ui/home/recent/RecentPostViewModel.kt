package com.beome.ui.home.recent

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beome.model.LikedPost
import com.beome.model.Post
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecentPostViewModel : ViewModel() {
    private val listRecenPost = MutableLiveData<List<Post>>()
    private val recentPostRepo = RecentPostRepository(Dispatchers.IO)

    fun getListRecentPost() : LiveData<List<Post>>{
        recentPostRepo.getRecentPost()
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .whereEqualTo("status", 1)
            .addSnapshotListener { querySnapshot, error ->
            error?.let{
                Log.e("err_get_recet_post", error.localizedMessage!!)
                return@addSnapshotListener
            }
            val addedRecentPostList = mutableListOf<Post>()
            querySnapshot?.let {
                for (document in it){
                    val post = document.toObject<Post>()
                    addedRecentPostList.add(post)
                }
                listRecenPost.value = addedRecentPostList
            }
        }
        return listRecenPost
    }
}