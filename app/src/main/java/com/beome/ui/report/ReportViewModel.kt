package com.beome.ui.report

import androidx.lifecycle.*
import com.beome.model.*
import com.beome.utilities.NetworkState

class ReportViewModel : ViewModel() {
    private val reportRepo = ReportRepository(viewModelScope)
    private val _reportRepo = MutableLiveData<ReportRepository>()
    lateinit var reportFeedbackState: LiveData<NetworkState>
    lateinit var reportAccountState: LiveData<NetworkState>
    lateinit var reportPostState: LiveData<NetworkState>

    fun setUpRepo() {
        _reportRepo.postValue(reportRepo)
    }

    fun setUpReportFeedback() {
        reportFeedbackState =
            Transformations.switchMap(_reportRepo, ReportRepository::stateReportFeedback)
    }

    fun reportFeedback(reportedFeedback: ReportedFeedback, report: ReportDetail) =
        reportRepo.reportFeedback(reportedFeedback, report)

    fun setUpReportAccount() {
        reportAccountState =
            Transformations.switchMap(_reportRepo, ReportRepository::stateReportAccount)
    }

    fun reportAccount(reportedAccount: ReportedAccount, report: ReportDetail) =
        reportRepo.reportAccount(reportedAccount, report)

    fun setUpReportPost() {
        reportPostState = Transformations.switchMap(_reportRepo, ReportRepository::stateReportPost)
    }

    fun reportPost(reportedPost: ReportedPost, report: ReportDetail) =
        reportRepo.reportPost(reportedPost, report)
}