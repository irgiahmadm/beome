package com.beome.ui.admin.account

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.beome.model.ReportedAccount
import com.beome.model.ReportedPost
import com.beome.utilities.NetworkState
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ReportedAccountRepository(private val scope : CoroutineScope) {
    val stateReportedAccount = MutableLiveData<NetworkState>()
    val stateTakedownAccount = MutableLiveData<NetworkState>()
    private val listReportedAccount = MutableLiveData<List<ReportedAccount>>()
    private val reportedAccount = MutableLiveData<ReportedAccount>()

    fun getListReportedAccount() : LiveData<List<ReportedAccount>> {
        scope.launch {
            withContext(Dispatchers.IO){
                val refReportedPost = Firebase.firestore.collection("reported_account")
                refReportedPost.whereEqualTo("user.userStatus", 1).addSnapshotListener { value, error ->
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

    fun getReportedAccount(authKey : String) : LiveData<ReportedAccount>{
        scope.launch {
            withContext(Dispatchers.IO){
                val refReportedAccount = Firebase.firestore.collection("reported_account").document(authKey)
                refReportedAccount.addSnapshotListener { value, error ->
                    stateReportedAccount.postValue(NetworkState.LOADING)
                    value?.let {
                        if(it.exists()){
                            val account = it.toObject<ReportedAccount>()
                            reportedAccount.value = account!!
                            stateReportedAccount.postValue(NetworkState.SUCCESS)
                        }else{
                            stateReportedAccount.postValue(NetworkState.NOT_FOUND)
                        }
                    }
                    error?.let {
                        Log.d("err_get_rprtd_account", it.message.toString())
                        stateReportedAccount.postValue(NetworkState.FAILED)
                    }
                }
            }
        }
        return reportedAccount
    }

    fun takedownAccount(authKey: String){
        scope.launch {
            withContext(Dispatchers.IO){
                val refReportedAccount = Firebase.firestore.collection("reported_account").document(authKey)
                val refUser = Firebase.firestore.collection("user").document(authKey)
                stateTakedownAccount.postValue(NetworkState.LOADING)
                Firebase.firestore.runTransaction {transaction ->
                    transaction.update(refReportedAccount, "user.userStatus", 2)
                    transaction.update(refUser, "userStatus", 2)
                }.addOnSuccessListener {
                    stateTakedownAccount.postValue(NetworkState.SUCCESS)
                }.addOnFailureListener {
                    stateTakedownAccount.postValue(NetworkState.FAILED)
                    Log.d("err_takedown_acc", it.message.toString())
                }.await()
            }
        }
    }
}