package com.beome.ui.feedback

import android.util.Log
import androidx.lifecycle.*
import com.beome.model.*
import com.beome.utilities.NetworkState
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject

class FeedbackViewModel : ViewModel() {
    private val detailPost = MutableLiveData<Post>()
    private val listFeedbackPost = MutableLiveData<List<FeedbackPostUser>>()
    private val userPoint = MutableLiveData<Long>()
    private val isGiveFeedbackPost = MutableLiveData<Boolean>()
    private val listFeedbackComponent = MutableLiveData<List<ComponentFeedbackPost>>()
    private val feedbackRepo = FeedbackRepository(viewModelScope)
    lateinit var addUserFeedbackState: LiveData<NetworkState>
    private lateinit var addFeedbackValueState: LiveData<NetworkState>
    private val _feedbackRepo = MutableLiveData<FeedbackRepository>()


    fun getPostDetail(idPost: String): LiveData<Post> {
        feedbackRepo.getDetailPost()
            .whereEqualTo("idPost", idPost)
            .addSnapshotListener { querySnapshot, error ->
                error?.let {
                    Log.e("err_get_recet_post", error.localizedMessage!!)
                    return@addSnapshotListener
                }
                querySnapshot?.let {
                    if(it.documents.isNotEmpty()){
                        if (it.documents[0].exists()) {
                            val post = it.documents[0].toObject<Post>()!!
                            detailPost.value = post
                        }
                    }
                }
            }
        return detailPost
    }

    fun getBadge(authKey : String) : LiveData<Long>{
        feedbackRepo.getUsersBadge()
            .get().addOnSuccessListener {
                var tempUserPoint = 0L
                for (document in it){
                    if(document.get("authKey") == authKey){
                        tempUserPoint = document.get("userPoint") as Long
                        break
                    }
                }
                userPoint.value = tempUserPoint
            }
        return userPoint
    }

    fun getListFeedbackPost(idPost: String): LiveData<List<FeedbackPostUser>> {
        feedbackRepo.getFeedbackUsers(idPost)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .whereEqualTo("status", 1)
            .addSnapshotListener { listFeedbackUser, errorUser ->
                errorUser?.let {
                    Log.e("err_get_list_fdbck", errorUser.localizedMessage!!)
                    return@addSnapshotListener
                }
                val tempListFeedbackUser = mutableListOf<FeedbackPostUser>()
                listFeedbackUser?.let {
                    for (document in listFeedbackUser) {
                        val feedbackPostUser = document.toObject<FeedbackPostUser>()
                        Log.d("list_feedback", feedbackPostUser.toString())
                        tempListFeedbackUser.add(feedbackPostUser)
                    }
                }
                listFeedbackPost.value = tempListFeedbackUser
            }
        return listFeedbackPost
    }

    fun isUserGiveFeedback(idPost: String, idUser: String) : LiveData<Boolean>{
        feedbackRepo.getFeedbackUsers(idPost)
            .whereEqualTo("authKey", idUser)
            .addSnapshotListener { value, error ->
                error?.let {
                    Log.e("err_get_fdbck_usr", error.localizedMessage!!)
                }
                var tempIsUserGiveFeedback = false
                value?.let {
                    for (document in value) {
                        if(document.exists()){
                            tempIsUserGiveFeedback = true
                        }
                    }
                }
                isGiveFeedbackPost.value = tempIsUserGiveFeedback
            }
        return isGiveFeedbackPost
    }


    fun getFeedbackComponent(idPost: String): LiveData<List<ComponentFeedbackPost>> {
        feedbackRepo.getFeedbackComponent()
            .whereEqualTo("idPost", idPost)
            .addSnapshotListener { querySnapshot, error ->
                error?.let {
                    Log.e("err_get_fdbck_cmpnnt", error.localizedMessage!!)
                    return@addSnapshotListener
                }
                val templistFeedbackComp = arrayListOf<ComponentFeedbackPost>()
                querySnapshot?.let {
                    for (document in it) {
                        val component = document.toObject<ComponentFeedbackPost>()
                        templistFeedbackComp.add(component)
                    }
                }
                listFeedbackComponent.value = templistFeedbackComp
            }
        return listFeedbackComponent
    }

    fun setUpUsertoFeedback() {
        addUserFeedbackState =
            Transformations.switchMap(_feedbackRepo, FeedbackRepository::addDataUserState)
        _feedbackRepo.postValue(feedbackRepo)
    }

    fun addUserFeedback(idPost: String, user: FeedbackPostUser) =
        feedbackRepo.addUserFeedback(idPost, user)

    fun setUpFeedbackValue() {
        addFeedbackValueState =
            Transformations.switchMap(_feedbackRepo, FeedbackRepository::addDataFeedbackValueState)
        _feedbackRepo.postValue(feedbackRepo)
    }

}