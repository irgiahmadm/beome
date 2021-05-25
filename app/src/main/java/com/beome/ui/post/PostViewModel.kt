package com.beome.ui.post

import android.util.Log
import androidx.lifecycle.*
import com.beome.model.LikedPostList
import com.beome.model.Post
import com.beome.utilities.NetworkState
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject


class PostViewModel : ViewModel() {
    private val listLikedPost = MutableLiveData<List<LikedPostList>>()
    private val postRepo = PostRepository(viewModelScope)
    lateinit var editPostState : LiveData<NetworkState>
    lateinit var deletePostState : LiveData<NetworkState>
    private val _postRepo = MutableLiveData<PostRepository>()

    fun getListLikedPost(idUser: String): LiveData<List<LikedPostList>> {

        val addedRecentPostList = mutableListOf<LikedPostList>()
        postRepo.getListPost()
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .whereEqualTo("status", 1)
            .whereArrayContains("likedBy", idUser)
            .addSnapshotListener { querySnapshot, error ->
                error?.let {
                    Log.e("err_get_liked_post1", error.localizedMessage!!)
                }
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
                    listLikedPost.value = addedRecentPostList
                    Log.d("list_liked_post", addedRecentPostList.toString())
                }
            }
        return listLikedPost
    }

    fun likePost(idPost : String, likedBy: String) =
        postRepo.likePost(idPost, likedBy)


    fun unlikePost(idPost : String, likedBy: String) =
        postRepo.unlikePost(idPost, likedBy)


    fun setUpUpdatePost(){
        editPostState = Transformations.switchMap(_postRepo, PostRepository::editPostState)
        _postRepo.postValue(postRepo)
    }

    fun updatePost(idPost: String, title : String, description : String) =
        postRepo.updatePost(idPost, title, description)

    fun setUpDeletePost(){
        deletePostState = Transformations.switchMap(_postRepo, PostRepository::deletePostState)
        _postRepo.postValue(postRepo)
    }

    fun deletePost(idPost: String) =
        postRepo.deletePost(idPost)
}