package com.beome.ui.admin

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.beome.model.ReportedAccount
import com.beome.utilities.NetworkState
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReportedAccountRepository(private val scope : CoroutineScope) {
    val stateReportedAccount = MutableLiveData<NetworkState>()
    private val listReportedAccount = MutableLiveData<List<ReportedAccount>>()

    fun getListReportedAccount() : LiveData<List<ReportedAccount>> {
        scope.launch {
            withContext(Dispatchers.IO){
                val refReportedPost = Firebase.firestore.collection("reported_account")
                refReportedPost.addSnapshotListener { value, error ->
                    stateReportedAccount.postValue(NetworkState.LOADING)
                    val tempReportedAccount = mutableListOf<ReportedAccount>()
                    value?.let {
                        if(it.documents.isEmpty()){
                            stateReportedAccount.postValue(NetworkState.NOT_FOUND)
                        }else{
                            for (document in it){
                                val reportedPost = document.toObject<ReportedAccount>()
                                tempReportedAccount.add(reportedPost)
                            }
                            listReportedAccount.value = tempReportedAccount
                            stateReportedAccount.postValue(NetworkState.SUCCESS)
                        }
                    }
                    error?.let {
                        Log.d("err_get_rprtd_post", it.message.toString())
                        stateReportedAccount.postValue(NetworkState.FAILED)
                    }
                }
            }
        }
        return listReportedAccount
    }
}