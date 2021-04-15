package com.beome.ui.home.following

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beome.model.LikedPostList
import com.beome.model.Post
import com.beome.ui.post.PostRepository
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Dispatchers

class FollowingPostViewModel : ViewModel(){

    private val listFollowingPost = MutableLiveData<List<LikedPostList>>()
    private val postRepo = PostRepository(viewModelScope)
    private val followRepo = FollowingPostRepository()

    fun getFollowingPost(followingId : String) : LiveData<List<LikedPostList>>{
        followRepo.getFollowing()
            .whereEqualTo("followingId", followingId)
            .addSnapshotListener { value, error ->
                val addedRecentPostList = mutableListOf<LikedPostList>()
                value?.let {
                    for (document in value) {
                        postRepo.getListPost()
                            .whereEqualTo("status", 1)
                            .whereEqualTo("authKey", document.get("followedId").toString())
                            .addSnapshotListener { listPost, errorListPost ->
                                listPost?.let {
                                    for (postVal in it){
                                        var isExist: Boolean
                                        val post = postVal.toObject<Post>()
                                        Log.d("list_foll_post", post.toString())
                                        isExist  = post.likedBy.any { likedBy ->
                                            likedBy == followingId
                                        }
                                        val likedPostObj = LikedPostList(post, isExist)
                                        addedRecentPostList.add(likedPostObj)
                                    }
                                    listFollowingPost.value = addedRecentPostList
                                }
                                errorListPost?.let {
                                    Log.d("err_get_foll_post", it.message.toString())
                                }

                            }
                    }
                }
            error?.let {
                Log.d("err_get_following", it.message.toString())
            }
        }
        return listFollowingPost
    }
}