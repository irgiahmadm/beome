package com.beome.ui.home.recent

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val listRecenPost = MutableLiveData<List<Post>>()
    private val recentPostRepo = RecentPostRepository(Dispatchers.IO)
    private val postRepo = PostRepository(Dispatchers.IO)

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

    fun getListLikedPost(idUser :String) : LiveData<List<LikedPostList>> {
        postRepo.getListLikedPost()
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .whereEqualTo("status", 1)
            .addSnapshotListener { querySnapshot, error ->
                val addedRecentPostList = mutableListOf<LikedPostList>()
                error?.let{
                    Log.e("err_get_liked_post1", error.localizedMessage!!)
                }
                querySnapshot?.let {
                    for (document in querySnapshot){
                        val likedPostObj = LikedPostList()
                        var isExist = false

                        postRepo.getLikedPost().whereEqualTo("idUser", idUser).addSnapshotListener { value, error ->

                            value?.let {
                                for (likedPost in it){
                                    Log.d("likedPost", likedPost.get("idPost").toString())
                                    isExist = likedPost.get("idPost").toString() == document.get("idPost")
                                }
                            }
                            error?.let {
                                Log.e("err_get_liked_post2", error.localizedMessage!!)
                            }
                        }

                        val post = document.toObject<Post>()
                        likedPostObj.post = post
                        likedPostObj.isLiked = isExist
                        Log.d("list_liked_post1", likedPostObj.toString())
                        addedRecentPostList.add(likedPostObj)
                    }
                    listLikedPost.value = addedRecentPostList

                }
            }

        return listLikedPost
    }
}