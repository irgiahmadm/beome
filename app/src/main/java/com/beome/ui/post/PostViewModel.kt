package com.beome.ui.post

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


class PostViewModel : ViewModel() {
    private val listLikedPost = MutableLiveData<List<Post>>()
    private val isPostLiked = MutableLiveData<Boolean>()
    private val postRepo = PostRepository(Dispatchers.IO)

    fun getLikedPost(idUser: String, idPost : String) : LiveData<Boolean>{
        postRepo.getLikedPost()
            .whereEqualTo("idUser", idUser)
            .whereEqualTo("idPost", idPost)
            .addSnapshotListener { value, error ->
                error?.let {
                    Log.e("err_get_liked_post3", error.localizedMessage!!)

                }
                var isExsist = false
                value?.let {
                    for (document in it) {
                        if (document.exists()) {
                            isExsist = true
                        }
                    }
                    isPostLiked.value = isExsist
                }
            }
        return isPostLiked
    }

    fun getListLikedPost(idUser :String) : LiveData<List<Post>> {
        postRepo.getLikedPost().whereEqualTo("idUser", idUser).addSnapshotListener { value, error ->
            val addedRecentPostList = mutableListOf<Post>()
            value?.let {
                for (likedPost in it){
                    Log.d("likedPost", likedPost.get("idPost").toString())
                    postRepo.getListLikedPost()
                        .orderBy("createdAt", Query.Direction.ASCENDING)
                        .whereEqualTo("status", 1)
                        .whereEqualTo("idPost", likedPost.get("idPost"))
                        .addSnapshotListener { querySnapshot, error ->
                            error?.let{
                                Log.e("err_get_liked_post1", error.localizedMessage!!)
                            }
                            querySnapshot?.let {
                                for (document in querySnapshot){
                                    val post = document.toObject<Post>()
                                    addedRecentPostList.add(post)
                                }
                                listLikedPost.value = addedRecentPostList
                                Log.d("list_liked_post", addedRecentPostList.toString())
                            }
                        }
                }
            }
            error?.let {
                Log.e("err_get_liked_post2", error.localizedMessage!!)
            }
        }
        return listLikedPost
    }

    //liked post should use write batch
    //TODO LIKE POST SHOULD BE USING WRITE BATCH
    fun likePost(likedPost: LikedPost) = viewModelScope.launch{
        postRepo.likePost(likedPost)
    }
}