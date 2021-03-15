package com.beome.ui.home.recent

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beome.model.Post
import com.google.firebase.firestore.ktx.toObject

class RecentPostViewModel : ViewModel() {
    private val listRecenPost = MutableLiveData<List<Post>>()
    private val recentPostRepo = RecentPostRepository()

    fun getListRecentPost() : LiveData<List<Post>>{
        recentPostRepo.getRecentPost().addSnapshotListener { querySnapshot, error ->
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