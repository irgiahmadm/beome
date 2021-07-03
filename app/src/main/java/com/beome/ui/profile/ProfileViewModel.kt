package com.beome.ui.profile

import android.app.Activity
import android.util.Log
import androidx.lifecycle.*
import com.beome.model.Follow
import com.beome.model.LikedPostList
import com.beome.model.Post
import com.beome.model.User
import com.beome.utilities.NetworkState
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.messaging.FirebaseMessaging

class ProfileViewModel : ViewModel() {
    private val profileUser = MutableLiveData<User>()
    private val follow = MutableLiveData<Follow>()
    private val profileRepo = ProfileRepository(viewModelScope)
    private val _profileRepo = MutableLiveData<ProfileRepository>()
    private val listPostUser = MutableLiveData<List<LikedPostList>>()
    private val _userState  = MutableLiveData<NetworkState>()
    val userState : LiveData<NetworkState>
        get() = _userState
    private val _followState  = MutableLiveData<NetworkState>()
    private val _listPostState  = MutableLiveData<NetworkState>()
    val listPostState : LiveData<NetworkState>
        get() = _listPostState
    lateinit var editProfileState : LiveData<NetworkState>
    lateinit var getOldPasswordState : LiveData<NetworkState>
    lateinit var changePasswordState : LiveData<NetworkState>

    fun followUser(follow: Follow) = profileRepo.followUser(follow)

    fun unFollowUser(followingId: String, followedId: String) =
        profileRepo.unFollowUser(followingId, followedId)

    fun setUpEditProfile(){
        editProfileState = Transformations.switchMap(_profileRepo, ProfileRepository::editProfileState)
        _profileRepo.postValue(profileRepo)
    }

    fun editProfile(authKey: String, user: User, actvity : Activity) =
        profileRepo.updateProfile(authKey, user, actvity)

    fun setUpChangePassword(){
        getOldPasswordState = Transformations.switchMap(_profileRepo, ProfileRepository::oldPasswordState)
        changePasswordState = Transformations.switchMap(_profileRepo, ProfileRepository::changePasswordState)
        _profileRepo.postValue(profileRepo)
    }

    fun getOldPassword(password : String, authKey : String) =
        profileRepo.getOldPassword(password, authKey)

    fun changePassword(password: String, authKey: String) = profileRepo.changePassword(password, authKey)


    fun getFollowStatus(authKey: String, followedId: String) : LiveData<NetworkState>{
        Log.d("follow_key", "$authKey - $followedId" )
        _followState.postValue(NetworkState.LOADING)
        profileRepo.getFollowStatus()
            .whereEqualTo("followingId", authKey)
            .whereEqualTo("followedId", followedId)
            .addSnapshotListener { value, error ->
                value?.let {
                    if(value.documents.isEmpty()){
                        _followState.postValue(NetworkState.NOT_FOUND)
                    }else{
                        if(value.documents[0].exists()){
                            _followState.postValue(NetworkState.SUCCESS)
                        }
                    }
                }
                error?.let {
                    Log.d("error_get_follow_state", error.localizedMessage!!)
                    _followState.postValue(NetworkState.FAILED)
                    return@addSnapshotListener
                }
        }
        return _followState
    }

    fun getProfileUser(authKey : String) : LiveData<User>{
        _userState.postValue(NetworkState.LOADING)
        profileRepo.getUserProfile()
            .whereEqualTo("authKey", authKey)
            .whereEqualTo("userStatus", 1)
            .addSnapshotListener { value, error ->
                error?.let {
                    Log.e("err_get_recet_post", error.localizedMessage!!)
                    _userState.postValue(NetworkState.FAILED)
                    return@addSnapshotListener
                }
                var tempUser: User
                value?.let {
                    if(value.documents.isNotEmpty()){
                        val user = value.documents[0].toObject<User>()
                        tempUser = user!!
                        profileUser.value = tempUser
                        _userState.postValue(NetworkState.SUCCESS)
                    }else{
                        _userState.postValue(NetworkState.NOT_FOUND)
                    }
                }
            }
        return profileUser
    }

    fun getListPostUser(authKey: String) : LiveData<List<LikedPostList>>{
        profileRepo.getPostByUser()
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .whereEqualTo("status", 1)
            .whereEqualTo("authKey", authKey)
            .addSnapshotListener { querySnapshot, error ->
                error?.let{
                    Log.e("err_get_recet_post", error.localizedMessage!!)
                    return@addSnapshotListener
                }
                val tempListPostUser = mutableListOf<LikedPostList>()
                querySnapshot?.let {
                    for (document in it){
                        var isExist: Boolean
                        val post = document.toObject<Post>()
                        isExist  = post.likedBy.any { likedBy ->
                            likedBy == authKey
                        }
                        val likedPostObj = LikedPostList(post, isExist)
                        tempListPostUser.add(likedPostObj)
                    }
                    listPostUser.value = tempListPostUser
                }
            }
        return listPostUser
    }

    fun getListPostUserPreview(authKeyPreview: String, authKeyLogedIn : String) : LiveData<List<LikedPostList>>{
        profileRepo.getPostByUser()
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .whereEqualTo("status", 1)
            .whereEqualTo("authKey", authKeyPreview)
            .addSnapshotListener { querySnapshot, error ->
                error?.let{
                    Log.e("err_get_recet_post", error.localizedMessage!!)
                    return@addSnapshotListener
                }
                val tempListPostUser = mutableListOf<LikedPostList>()
                querySnapshot?.let {
                    for (document in it){
                        var isExist: Boolean
                        val post = document.toObject<Post>()
                        isExist  = post.likedBy.any { likedBy ->
                            likedBy == authKeyLogedIn
                        }
                        val likedPostObj = LikedPostList(post, isExist)
                        tempListPostUser.add(likedPostObj)
                    }
                    listPostUser.value = tempListPostUser
                }
            }
        return listPostUser
    }

    fun checkToken(authKey: String) {
        val tokenTask = FirebaseMessaging.getInstance().token
        tokenTask.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "FCM token failed.", task.exception)
                return@OnCompleteListener
            }
            updateToken(authKey, task.result.toString())
        })
    }

    private fun updateToken(authKey: String, oldToken : String){
        profileRepo.updateToken(authKey, oldToken)
    }

    fun deleteToken(authKey: String){
        profileRepo.deleteToken(authKey)
    }
}