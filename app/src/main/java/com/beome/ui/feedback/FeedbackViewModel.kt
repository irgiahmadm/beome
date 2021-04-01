package com.beome.ui.feedback

import android.util.Log
import androidx.lifecycle.*
import com.beome.model.*
import com.beome.ui.add.AddPostRepository
import com.beome.utilities.NetworkState
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeedbackViewModel : ViewModel() {
    private val detailPost = MutableLiveData<Post>()
    private val listFeedbackPost = MutableLiveData<FeedbackPost>()
    private val listFeedbackComponent = MutableLiveData<List<ComponentFeedbackPost>>()
    private val feedbackRepo = FeedbackRepository(Dispatchers.IO)
    lateinit var addUserFeedbackState: LiveData<NetworkState>
    lateinit var addFeedbackValueState: LiveData<NetworkState>
    private val _feedbackRepo = MutableLiveData<FeedbackRepository>()
    private val tempListFeedbackUser = arrayListOf<FeedbackPostUser>()
    private val tempListFeedbackValue = arrayListOf<FeedbackPostUserValue>()

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

    fun getListFeedbackPost(idPost: String): LiveData<FeedbackPost> {
        feedbackRepo.getListFeedbackUser(idPost)
            .addSnapshotListener { listFeedbackUser, errorUser ->
                errorUser?.let {
                    Log.e("err_get_list_fdbck", errorUser.localizedMessage!!)
                    return@addSnapshotListener
                }
                for (document in listFeedbackUser!!) {
                    val feedbackPost = FeedbackPost()
                    Log.d("document_id", document.id)
                    val feedbackPostUser = document.toObject<FeedbackPostUser>()
                    tempListFeedbackUser.add(feedbackPostUser)
                    feedbackPost.user = tempListFeedbackUser
                    feedbackRepo.getListFeedbackValue(idPost, document.id)
                        .addSnapshotListener { listFeedbackValue, errorValue ->
                            errorValue?.let {
                                Log.e("err_get_list_fdbck", errorValue.localizedMessage!!)
                            }
                            for (feedbackValue in listFeedbackValue!!) {
                                val feedbackPostValue =
                                    feedbackValue.toObject<FeedbackPostUserValue>()
                                tempListFeedbackValue.add(feedbackPostValue)
                            }
                            feedbackPost.feedbackValue = tempListFeedbackValue
                            listFeedbackPost.value = feedbackPost
                            Log.d("list_feedback", tempListFeedbackValue.toString())
                        }
                }
            }
        return listFeedbackPost
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

    fun addUsertoFeedback(idPost: String, idUser: String, user: FeedbackPostUser) =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                feedbackRepo.addUsertoFeedback(idPost, idUser, user)
            }
        }

    fun setUpFeedbackValue() {
        addFeedbackValueState =
            Transformations.switchMap(_feedbackRepo, FeedbackRepository::addDataFeedbackValueState)
        _feedbackRepo.postValue(feedbackRepo)
    }

    fun addFeedbackValue(
        idPost: String,
        idUser: String,
        feedbackValue: FeedbackPostUserValue,
        listSize: Int,
        counter: Int,
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            feedbackRepo.addFeedbackValue(
                idPost,
                idUser,
                feedbackValue,
                listSize,
                counter
            )
        }
    }

}