package com.beome.ui.home.recent

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beome.model.LikedBy
import com.beome.model.LikedPost
import com.beome.model.LikedPostList
import com.beome.model.Post
import com.beome.ui.post.PostRepository
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecentPostViewModel : ViewModel() {
    private val listLikedPost = MutableLiveData<List<LikedPostList>>()
    private val listRecenPost = MutableLiveData<List<LikedPostList>>()
    private val recentPostRepo = RecentPostRepository(Dispatchers.IO)
    private val postRepo = PostRepository(Dispatchers.IO)

    fun getListRecentPost(idUser: String) : LiveData<List<LikedPostList>>{
        recentPostRepo.getRecentPost()
            .orderBy("createdAt", Query.Direction.ASCENDING)
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