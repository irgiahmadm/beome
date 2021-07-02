package com.beome.ui.admin.feedback

import androidx.lifecycle.*
import com.beome.ui.admin.ReportDetailRepository
import com.beome.utilities.NetworkState
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ReportedFeedbackViewModel : ViewModel() {
    private val reportedtedFeedbackRepo = ReportedFeedbackRepository(viewModelScope)
    private val _reportedtedFeedbackRepo = MutableLiveData<ReportedFeedbackRepository>()
    private val reportedDetailRepo = ReportDetailRepository(viewModelScope)
    private val _reportedDetailRepo = MutableLiveData<ReportDetailRepository>()
    lateinit var stateReportedFeedback : LiveData<NetworkState>
    private lateinit var stateReportDetailList : LiveData<NetworkState>
    lateinit var stateTakedownFeedback : LiveData<NetworkState>

    fun setUpRepo(){
        _reportedtedFeedbackRepo.postValue(reportedtedFeedbackRepo)
    }

    fun setUpRepoDetailList(){
        _reportedDetailRepo.postValue(reportedDetailRepo)
    }

    fun setUpReportedFeedback(){
        stateReportedFeedback = Transformations.switchMap(_reportedtedFeedbackRepo, ReportedFeedbackRepository::stateReportedFeedback)
    }

    fun setUpTakedownFeedback(){
        stateTakedownFeedback = Transformations.switchMap(_reportedtedFeedbackRepo, ReportedFeedbackRepository::networkStateTakedownPost)
    }

    fun setUpReportedDetailList(){
        stateReportDetailList = Transformations.switchMap(_reportedDetailRepo, ReportDetailRepository::stateReportedDetail)
    }


    fun getListReportedFeedback() = reportedtedFeedbackRepo.getListReportedFeedback()

    fun getReportedFeedback(idFeedback: String) = reportedtedFeedbackRepo.getReportedFeedback(idFeedback)

    fun getListReportDetail(idFeedback: String) = reportedDetailRepo.getListReportedDetail(
        Firebase.firestore.collection("reported_feedback_detail"),
        idFeedback
    )
    fun takedownFeedback(idFeedback: String, idPost : String) = reportedtedFeedbackRepo.takedownFeedback(idFeedback, idPost)
}