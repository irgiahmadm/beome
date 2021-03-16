package com.beome.ui.feedback

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beome.model.ComponentFeedbackPost
import com.beome.model.Post
import com.google.firebase.firestore.ktx.toObject

class FeedbackViewModel : ViewModel() {
    private val detailPost = MutableLiveData<Post>()
    private val listFeedbackComponent = MutableLiveData<List<ComponentFeedbackPost>>()
    private val feedbackRepo = FeedbackRepository()

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
}