package com.beome.ui.admin

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.beome.model.ReportDetail
import com.beome.model.ReportedPost
import com.beome.utilities.NetworkState
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReportDetailRepository(private val scope : CoroutineScope) {
    private val listReportedDetail = MutableLiveData<List<ReportDetail>>()
    val stateReportedDetail = MutableLiveData<NetworkState>()

    fun getListReportedDetail(collectionRef : CollectionReference, idReport : String) : LiveData<List<ReportDetail>>{
        scope.launch {
            withContext(Dispatchers.IO){
                collectionRef.whereEqualTo("idReport", idReport).addSnapshotListener { value, error ->
                    stateReportedDetail.postValue(NetworkState.LOADING)
                    val tempReportedDetail = mutableListOf<ReportDetail>()
                    value?.let {
                        Log.d("data_rprt", it.documents.toString() +"-"+idReport)
                        if(it.documents.isEmpty()){
                            stateReportedDetail.postValue(NetworkState.NOT_FOUND)
                        }else{
                            for (document in it){
                                val reportedPost = document.toObject<ReportDetail>()
                                tempReportedDetail.add(reportedPost)
                            }
                            listReportedDetail.value = tempReportedDetail
                            stateReportedDetail.postValue(NetworkState.SUCCESS)
                        }
                    }
                }
            }
        }
       return listReportedDetail
    }
}