package com.beome.ui.home.recent

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beome.model.LikedPostList
import com.beome.model.Post
import com.beome.ui.post.PostRepository
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject

class RecentPostViewModel : ViewModel() {
    private val listLikedPost = MutableLiveData<List<LikedPostList>>()
    private val listRecenPost = MutableLiveData<List<LikedPostList>>()
    private val recentPostRepo = RecentPostRepository(viewModelScope)
    private val postRepo = PostRepository(viewModelScope)

    fun getListRecentPost(idUser: String) : LiveData<List<LikedPostList>>{
        recentPostRepo.getRecentPost()
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .whereEqualTo("status", 1)
            .addSnapshotListener { querySnapshot, error ->
            error?.let{
                Log.e("err_get_recet_post", error.localizedMessage!!)
                return@addSnapshotListener
            }
            val addedRecentPostList = mutableListOf<LikedPostList>()
            querySnapshot?.let {
                for (document in it){
                    var isExist: Boolean
                    val post = document.toObject<Post>()
                    isExist  = post.likedBy.any { likedBy ->
                        likedBy == idUser
                    }
                    val likedPostObj = LikedPostList(post, isExist)
                    addedRecentPostList.add(likedPostObj)
                }
                listRecenPost.value = addedRecentPostList
            }
        }
        return listRecenPost
    }
}