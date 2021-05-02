package com.beome.ui.admin.account

import androidx.lifecycle.*
import com.beome.utilities.NetworkState

class ReportedAccountViewModel : ViewModel() {
    private val reportedtedAccountRepo = ReportedAccountRepository(viewModelScope)
    private val _reportedAccountRepo = MutableLiveData<ReportedAccountRepository>()
    lateinit var stateReportedAccount : LiveData<NetworkState>

    fun setUpRepo(){
        _reportedAccountRepo.postValue(reportedtedAccountRepo)
    }

    fun setUpReportedAccount(){
        stateReportedAccount = Transformations.switchMap(_reportedAccountRepo, ReportedAccountRepository::stateReportedAccount)
    }

    fun getListReportedAccount() = reportedtedAccountRepo.getListReportedAccount()
}