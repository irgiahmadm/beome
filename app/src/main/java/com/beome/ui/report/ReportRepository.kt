package com.beome.ui.report

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.beome.model.*
import com.beome.utilities.NetworkState
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
                    Firebase.firestore.runTransaction { transaction ->
                        val reportedFeedbackRef = Firebase.firestore.collection("reported_feedback").document(reportedFeedback.feedback.idFeedback)
                        val reportDetail = Firebase.firestore.collection("reported_feedback_detail").document()
                        transaction.set(reportedFeedbackRef, reportedFeedback)
                        transaction.set(reportDetail, report)

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
                    stateReportFeedback.postValue(NetworkState.LOADING)
                    Firebase.firestore.runTransaction { transaction ->
                        val reportedAccountRef = Firebase.firestore.collection("reported_account").document(reportedAccount.user.authKey)
                        val reportDetail = Firebase.firestore.collection("reported_account_detail").document()
                        transaction.set(reportedAccountRef, reportedAccount)
                        transaction.set(reportDetail, report)

                    }.await()
                    stateReportFeedback.postValue(NetworkState.SUCCESS)
                }catch (e : Exception){
                    Log.d("err_add_report_feedback", e.message.toString())
                    stateReportFeedback.postValue(NetworkState.FAILED)
                }
            }
        }
    }

    fun reportPost(reportedPost : ReportedPost, report : ReportDetail){
        coroutineScope.launch {
            withContext(Dispatchers.IO){
                try {
                    stateReportFeedback.postValue(NetworkState.LOADING)
                    Firebase.firestore.runTransaction { transaction ->
                        val reportedPostRef = Firebase.firestore.collection("reported_post").document(reportedPost.post.idPost)
                        val reportDetail = Firebase.firestore.collection("reported_post_detail").document()

                        transaction.set(reportedPostRef, reportedPost)
                        transaction.set(reportDetail, report)

                    }.await()
                    stateReportFeedback.postValue(NetworkState.SUCCESS)
                }catch (e : Exception){
                    Log.d("err_add_report_feedback", e.message.toString())
                    stateReportFeedback.postValue(NetworkState.FAILED)
                }
            }
        }
    }
}