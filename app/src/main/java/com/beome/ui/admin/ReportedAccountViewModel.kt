package com.beome.ui.admin

import androidx.lifecycle.*
import com.beome.utilities.NetworkState

class ReportedAccountViewModel : ViewModel() {
    val reportedtedAccountRepo = ReportedAccountRepository(viewModelScope)
    val _reportedPostRepo = MutableLiveData<ReportedAccountRepository>()
    lateinit var stateReportedPost : LiveData<NetworkState>

    fun setUpRepo(){
        _reportedPostRepo.postValue(reportedtedAccountRepo)
    }

    fun setUpReportedAccount(){
        stateReportedPost = Transformations.switchMap(_reportedPostRepo, ReportedAccountRepository::stateReportedAccount)
    }

    fun getListReportedAccount() = reportedtedAccountRepo.getListReportedAccount()
}