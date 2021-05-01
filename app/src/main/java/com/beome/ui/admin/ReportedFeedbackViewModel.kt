package com.beome.ui.admin

import androidx.lifecycle.*
import com.beome.utilities.NetworkState

class ReportedFeedbackViewModel : ViewModel() {
    private val reportedtedFeedbackRepo = ReportedFeedbackRepository(viewModelScope)
    private val _reportedtedFeedbackRepo = MutableLiveData<ReportedFeedbackRepository>()
    lateinit var stateReportedFeedback : LiveData<NetworkState>

    fun setUpRepo(){
        _reportedtedFeedbackRepo.postValue(reportedtedFeedbackRepo)
    }

    fun setUpReportedFeedback(){
        stateReportedFeedback = Transformations.switchMap(_reportedtedFeedbackRepo, ReportedFeedbackRepository::stateReportedFeedback)
    }

    fun getListReportedFeedback() = reportedtedFeedbackRepo.getListReportedFeedback()
}