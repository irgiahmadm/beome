package com.beome.ui.report

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.beome.model.*
import com.beome.utilities.NetworkState
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

class ReportRepository(private val coroutineScope : CoroutineScope) {
    val stateReportFeedback = MutableLiveData<NetworkState>()
    val stateReportAccount = MutableLiveData<NetworkState>()
    val stateReportPost = MutableLiveData<NetworkState>()

    fun reportFeedback(reportedFeedback : ReportedFeedback, report : ReportDetail){
        coroutineScope.launch {
            withContext(Dispatchers.IO){
                try {
                    stateReportFeedback.postValue(NetworkState.LOADING)
                    val reportDetail = Firebase.firestore.collection("reported_feedback_detail")
                    checkReportAlreadyExist(report, reportDetail, stateReportFeedback)
                    Firebase.firestore.runTransaction { transaction ->
                        val reportedFeedbackRef = Firebase.firestore.collection("reported_feedback").document(reportedFeedback.feedback.idFeedback)
                        if(transaction.get(reportedFeedbackRef).exists()){
                            val feedback = transaction.get(reportedFeedbackRef)
                            val updateCounter = feedback["counter"] as Long + 1
                            transaction.update(reportedFeedbackRef,"counter", updateCounter)
                        }else{
                            transaction.set(reportedFeedbackRef, reportedFeedback)
                        }
                    }.await()
                    stateReportFeedback.postValue(NetworkState.SUCCESS)
                }catch (e : Exception){
                    Log.d("err_add_report_feedback", e.message.toString())
                    stateReportFeedback.postValue(NetworkState.FAILED)
                }
            }
        }
    }

    fun reportAccount(reportedAccount : ReportedAccount, report : ReportDetail){
        coroutineScope.launch {
            withContext(Dispatchers.IO){
                try {
                    stateReportAccount.postValue(NetworkState.LOADING)
                    val reportDetail = Firebase.firestore.collection("reported_account_detail")
                    checkReportAlreadyExist(report, reportDetail, stateReportAccount).await()
                    Firebase.firestore.runTransaction { transaction ->
                        val reportedAccountRef = Firebase.firestore.collection("reported_account").document(reportedAccount.user.authKey)
                        if(transaction.get(reportedAccountRef).exists()){
                            val user = transaction.get(reportedAccountRef)
                            val updateCounter = user["counter"] as Long + 1
                            transaction.update(reportedAccountRef, "counter", updateCounter)
                        }else{
                            transaction.set(reportedAccountRef, reportedAccount)
                        }
                    }.await()
                    stateReportAccount.postValue(NetworkState.SUCCESS)
                }catch (e : Exception){
                    Log.d("err_add_report_feedback", e.message.toString())
                    stateReportAccount.postValue(NetworkState.FAILED)
                }
            }
        }
    }

    fun reportPost(reportedPost : ReportedPost, report : ReportDetail){
        coroutineScope.launch {
            withContext(Dispatchers.IO){
                try {
                    stateReportFeedback.postValue(NetworkState.LOADING)
                    val reportDetail = Firebase.firestore.collection("reported_post_detail")
                    checkReportAlreadyExist(report, reportDetail, stateReportFeedback).await()
                    Firebase.firestore.runTransaction { transaction ->
                        val reportedPostRef = Firebase.firestore.collection("reported_post").document(reportedPost.post.idPost)
                        if(transaction.get(reportedPostRef).exists()){
                            val post = transaction.get(reportedPostRef)
                            val updateCounter = post["counter"] as Long + 1
                            transaction.update(reportedPostRef, "counter", updateCounter)
                        }else{
                            transaction.set(reportedPostRef, reportedPost)
                        }
                    }.await()
                    stateReportFeedback.postValue(NetworkState.SUCCESS)
                }catch (e : Exception){
                    Log.d("err_add_report_feedback", e.message.toString())
                    stateReportFeedback.postValue(NetworkState.FAILED)
                }
            }
        }
    }

    private fun checkReportAlreadyExist(report : ReportDetail, reportDetail : CollectionReference, networkState: MutableLiveData<NetworkState>) : Task<QuerySnapshot>{
        return reportDetail.whereEqualTo("username", report.username).get()
            .addOnSuccessListener {
                if(it.documents.isEmpty()){
                    reportDetail.add(report)
                }else{
                    for (document in it){
                        if(document["reportReason"] != report.reportReason){
                            reportDetail.add(report)
                            break
                        }
                    }
                }
            }.addOnFailureListener {
                networkState.postValue(NetworkState.FAILED)
            }
    }
}