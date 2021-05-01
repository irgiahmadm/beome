package com.beome.ui.admin

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.beome.model.ReportedPost
import com.beome.utilities.NetworkState
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReportedPostRepository(private val scope : CoroutineScope) {
    val stateReportedPost = MutableLiveData<NetworkState>()
    private val listReportedPost = MutableLiveData<List<ReportedPost>>()

    fun getListReportedPost() : LiveData<List<ReportedPost>>{
        scope.launch {
            withContext(Dispatchers.IO){
                val refReportedPost = Firebase.firestore.collection("reported_post")
                refReportedPost.addSnapshotListener { value, error ->
                    stateReportedPost.postValue(NetworkState.LOADING)
                    val tempReportedPost = mutableListOf<ReportedPost>()
                    value?.let {
                        if(it.documents.isEmpty()){
                            stateReportedPost.postValue(NetworkState.NOT_FOUND)
                        }else{
                            for (document in it){
                                val reportedPost = document.toObject<ReportedPost>()
                                tempReportedPost.add(reportedPost)
                            }
                            listReportedPost.value = tempReportedPost
                            stateReportedPost.postValue(NetworkState.SUCCESS)
                        }
                    }
                    error?.let {
                        Log.d("err_get_rprtd_post", it.message.toString())
                        stateReportedPost.postValue(NetworkState.FAILED)
                    }
                }
            }
        }
        return listReportedPost
    }
}