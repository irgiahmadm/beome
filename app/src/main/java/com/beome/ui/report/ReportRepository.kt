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
                    checkReportFeedbackAlreadyExist(reportedFeedback, report, reportDetail)
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
                    checkReportAccountAlreadyExist(reportedAccount, report, reportDetail).await()
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
                    stateReportPost.postValue(NetworkState.LOADING)
                    val reportDetail = Firebase.firestore.collection("reported_post_detail")
                    checkReportPostAlreadyExist(reportedPost,report, reportDetail).await()
                }catch (e : Exception){
                    Log.d("err_add_report_feedback", e.message.toString())
                    stateReportPost.postValue(NetworkState.FAILED)
                }
            }
        }
    }

    private fun checkReportPostAlreadyExist(reportedPost : ReportedPost, report : ReportDetail, reportDetail : CollectionReference) : Task<QuerySnapshot>{
        return reportDetail
            .whereEqualTo("username", report.username)
            .whereEqualTo("idReport", report.idReport)
            .get()
            .addOnSuccessListener {
                if(it.documents.isEmpty()){
                    reportDetail.add(report)
                    Firebase.firestore.runTransaction { transaction ->
                        val reportedPostRef = Firebase.firestore.collection("reported_post").document(reportedPost.post.idPost)
                        if(transaction.get(reportedPostRef).exists()){
                            val post = transaction.get(reportedPostRef)
                            val updateCounter = post["counter"] as Long + 1
                            transaction.update(reportedPostRef, "counter", updateCounter)
                        }else{
                            transaction.set(reportedPostRef, reportedPost)
                        }
                    }
                    stateReportPost.postValue(NetworkState.SUCCESS)
                }else{
                    for (document in it){
                        Log.d("reportReason", document["reportReason"].toString() +"-"+ report.reportReason)
                        if(document["reportReason"] != report.reportReason){
                            reportDetail.add(report)
                            Firebase.firestore.runTransaction { transaction ->
                                val reportedPostRef = Firebase.firestore.collection("reported_post").document(reportedPost.post.idPost)
                                if(transaction.get(reportedPostRef).exists()){
                                    val post = transaction.get(reportedPostRef)
                                    val updateCounter = post["counter"] as Long + 1
                                    transaction.update(reportedPostRef, "counter", updateCounter)
                                }else{
                                    transaction.set(reportedPostRef, reportedPost)
                                }
                            }
                            stateReportPost.postValue(NetworkState.SUCCESS)
                            break
                        }else{
                            Log.d("reportReason", "sama")
                            stateReportPost.postValue(NetworkState.FAILED)
                            break
                        }
                    }
                }
            }.addOnFailureListener {
                stateReportPost.postValue(NetworkState.FAILED)
            }
    }

    private fun checkReportAccountAlreadyExist(reportedAccount: ReportedAccount, report : ReportDetail, reportDetail : CollectionReference) : Task<QuerySnapshot>{
        return reportDetail
            .whereEqualTo("username", report.username)
            .whereEqualTo("idReport", report.idReport)
            .get()
            .addOnSuccessListener {
                if(it.documents.isEmpty()){
                    reportDetail.add(report)
                    Firebase.firestore.runTransaction { transaction ->
                        val reportedAccountRef = Firebase.firestore.collection("reported_account").document(reportedAccount.user.authKey)
                        if(transaction.get(reportedAccountRef).exists()){
                            val user = transaction.get(reportedAccountRef)
                            val updateCounter = user["counter"] as Long + 1
                            transaction.update(reportedAccountRef, "counter", updateCounter)
                        }else{
                            transaction.set(reportedAccountRef, reportedAccount)
                        }
                    }
                    stateReportAccount.postValue(NetworkState.SUCCESS)
                }else{
                    for (document in it){
                        Log.d("reportReason", document["reportReason"].toString() +"-"+ report.reportReason)
                        if(document["reportReason"] != report.reportReason){
                            reportDetail.add(report)
                            Firebase.firestore.runTransaction { transaction ->
                                val reportedAccountRef = Firebase.firestore.collection("reported_account").document(reportedAccount.user.authKey)
                                if(transaction.get(reportedAccountRef).exists()){
                                    val user = transaction.get(reportedAccountRef)
                                    val updateCounter = user["counter"] as Long + 1
                                    transaction.update(reportedAccountRef, "counter", updateCounter)
                                }else{
                                    transaction.set(reportedAccountRef, reportedAccount)
                                }
                            }
                            stateReportAccount.postValue(NetworkState.SUCCESS)
                            break
                        }else{
                            stateReportAccount.postValue(NetworkState.FAILED)
                            break
                        }
                    }
                }
            }.addOnFailureListener {
                stateReportAccount.postValue(NetworkState.FAILED)
            }
    }

    private fun checkReportFeedbackAlreadyExist(reportedFeedback: ReportedFeedback, report : ReportDetail, reportDetail : CollectionReference) : Task<QuerySnapshot>{
        return reportDetail
            .whereEqualTo("username", report.username)
            .whereEqualTo("idReport", report.idReport)
            .get()
            .addOnSuccessListener {
                if(it.documents.isEmpty()){
                    reportDetail.add(report)
                    Firebase.firestore.runTransaction { transaction ->
                        val reportedFeedbackRef = Firebase.firestore.collection("reported_feedback").document(reportedFeedback.feedback.idFeedback)
                        if(transaction.get(reportedFeedbackRef).exists()){
                            val feedback = transaction.get(reportedFeedbackRef)
                            val updateCounter = feedback["counter"] as Long + 1
                            transaction.update(reportedFeedbackRef,"counter", updateCounter)
                        }else{
                            transaction.set(reportedFeedbackRef, reportedFeedback)
                        }
                    }
                    stateReportFeedback.postValue(NetworkState.SUCCESS)
                }else{
                    for (document in it){
                        Log.d("reportReason", document["reportReason"].toString() +"-"+ report.reportReason)
                        if(document["reportReason"] != report.reportReason){
                            reportDetail.add(report)
                            Firebase.firestore.runTransaction { transaction ->
                                val reportedFeedbackRef = Firebase.firestore.collection("reported_feedback").document(reportedFeedback.feedback.idFeedback)
                                if(transaction.get(reportedFeedbackRef).exists()){
                                    val feedback = transaction.get(reportedFeedbackRef)
                                    val updateCounter = feedback["counter"] as Long + 1
                                    transaction.update(reportedFeedbackRef,"counter", updateCounter)
                                }else{
                                    transaction.set(reportedFeedbackRef, reportedFeedback)
                                }
                            }
                            stateReportFeedback.postValue(NetworkState.SUCCESS)
                            break
                        }else{
                            stateReportFeedback.postValue(NetworkState.FAILED)
                            break
                        }
                    }
                }
            }.addOnFailureListener {
                stateReportFeedback.postValue(NetworkState.FAILED)
            }
    }
}