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
    private val listFeedbackPost = MutableLiveData<String>()
    private val listFeedbackComponent = MutableLiveData<List<ComponentFeedbackPost>>()
    private val feedbackRepo = FeedbackRepository(Dispatchers.IO)
    lateinit var addUserFeedbackState : LiveData<NetworkState>
    lateinit var addFeedbackValueState : LiveData<NetworkState>
    private val _feedbackRepo = MutableLiveData<FeedbackRepository>()

    fun getPostDetail(idPost : String) : LiveData<Post> {
        feedbackRepo.getDetailPost()
            .whereEqualTo("idPost", idPost)
            .addSnapshotListener { querySnapshot, error ->
                error?.let{
                    Log.e("err_get_recet_post", error.localizedMessage!!)
                    return@addSnapshotListener
                }
                querySnapshot?.let {
                    if(it.documents[0].exists()){
                        val post = it.documents[0].toObject<Post>()!!
                        detailPost.value = post
                    }
                }
            }
        return detailPost
    }

    fun getListFeedbackPost(idPost: String, idUser: String, idFeedbackPost: String) : LiveData<String>{
        feedbackRepo.getListFeedbackPost(idPost).addSnapshotListener { listFeedback, error ->
            error?.let {
                Log.e("err_get_list_fdbck", error.localizedMessage!!)
                return@addSnapshotListener
            }
            var tempListFeedback = ""
            listFeedback?.let {documentSnapshot ->

                tempListFeedback = documentSnapshot.data.toString()
            }
            listFeedbackPost.value = tempListFeedback
        }
        return listFeedbackPost
    }

    fun getFeedbackComponent(idPost : String) : LiveData<List<ComponentFeedbackPost>>{
        feedbackRepo.getFeedbackComponent()
            .whereEqualTo("idPost", idPost)
            .addSnapshotListener { querySnapshot, error ->
                error?.let {
                    Log.e("err_get_fdbck_cmpnnt", error.localizedMessage!!)
                    return@addSnapshotListener
                }
                val templistFeedbackComp = arrayListOf<ComponentFeedbackPost>()
                querySnapshot?.let {
                    for(document in it){
                        val component = document.toObject<ComponentFeedbackPost>()
                        templistFeedbackComp.add(component)
                    }
                }
                listFeedbackComponent.value = templistFeedbackComp
            }
        return listFeedbackComponent
    }

    fun setUpUsertoFeedback(){
        addUserFeedbackState = Transformations.switchMap(_feedbackRepo, FeedbackRepository::addDataUserState)
        _feedbackRepo.postValue(feedbackRepo)
    }

    fun addUsertoFeedback(idPost: String, idUser : String, user : FeedbackPostUser, idFeedbackPost: String) = viewModelScope.launch {
        withContext(Dispatchers.IO){
            feedbackRepo.addUsertoFeedback(idPost, idUser, user, idFeedbackPost)
        }
    }

    fun setUpFeedbackValue(){
        addFeedbackValueState = Transformations.switchMap(_feedbackRepo, FeedbackRepository::addDataFeedbackValueState)
        _feedbackRepo.postValue(feedbackRepo)
    }

    fun addFeedbackValue(idPost:String, idUser: String, feedbackValue : FeedbackPostUserValue, listSize : Int, counter : Int, idFeedbackPost : String) = viewModelScope.launch{
        withContext(Dispatchers.IO){
            feedbackRepo.addFeedbackValue(idPost, idUser, feedbackValue, listSize, counter, idFeedbackPost)
        }
    }

}