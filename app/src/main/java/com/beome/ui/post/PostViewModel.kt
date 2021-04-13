package com.beome.ui.post

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beome.model.Post
import com.beome.ui.home.recent.RecentPostRepository
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PostViewModel : ViewModel() {
    private val listLikedPost = MutableLiveData<List<Post>>()
    private val isPostLiked = MutableLiveData<Boolean>()
    private val postRepo = PostRepository(Dispatchers.IO)
    private val recentPostRepo = RecentPostRepository(Dispatchers.IO)

    fun getListLikedPost(idUser: String): LiveData<List<Post>> {

        val addedRecentPostList = mutableListOf<Post>()

        postRepo.getListPost()
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .whereEqualTo("status", 1)
            .whereArrayContains("likedBy", idUser)
            .addSnapshotListener { querySnapshot, error ->
                error?.let {
                    Log.e("err_get_liked_post1", error.localizedMessage!!)
                }
                querySnapshot?.let {
                    for (document in querySnapshot) {
                        val post = document.toObject<Post>()
                        addedRecentPostList.add(post)
                    }
                    listLikedPost.value = addedRecentPostList
                    Log.d("list_liked_post", addedRecentPostList.toString())
                }
            }
        return listLikedPost
    }

    fun likePost(idPost : String, likedBy: String) = viewModelScope.launch{
        postRepo.likePost(idPost, likedBy)
    }

    fun unlikePost(idPost : String, likedBy: String) = viewModelScope.launch {
        postRepo.unlikePost(idPost, likedBy)
    }
}