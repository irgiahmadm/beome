package com.beome.ui.admin.account

import androidx.lifecycle.*
import com.beome.ui.admin.ReportDetailRepository
import com.beome.utilities.NetworkState
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ReportedAccountViewModel : ViewModel() {
    private val reportedtedAccountRepo = ReportedAccountRepository(viewModelScope)
    private val _reportedAccountRepo = MutableLiveData<ReportedAccountRepository>()
    private val reportedDetailRepo = ReportDetailRepository(viewModelScope)
    private val _reportedDetailRepo = MutableLiveData<ReportDetailRepository>()
    lateinit var stateReportedAccount : LiveData<NetworkState>
    lateinit var stateReportDetailList : LiveData<NetworkState>
    lateinit var stateTakedownAccount : LiveData<NetworkState>

    fun setUpRepo(){
        _reportedAccountRepo.postValue(reportedtedAccountRepo)
    }

    fun setUpRepoDetailList(){
        _reportedDetailRepo.postValue(reportedDetailRepo)
    }

    fun setUpReportedDetailList(){
        stateReportDetailList = Transformations.switchMap(_reportedDetailRepo, ReportDetailRepository::stateReportedDetail)
    }

    fun setUpReportedAccount(){
        stateReportedAccount = Transformations.switchMap(_reportedAccountRepo, ReportedAccountRepository::stateReportedAccount)
    }

    fun setUpTakedowonAccount(){
        stateReportedAccount = Transformations.switchMap(_reportedAccountRepo, ReportedAccountRepository::stateTakedownAccount)
    }

    fun takedownAccount(authKey: String) = reportedtedAccountRepo.takedownAccount(authKey)

    fun getListReportedAccount() = reportedtedAccountRepo.getListReportedAccount()

    fun getReportedAccount(authKey : String) = reportedtedAccountRepo.getReportedAccount(authKey)

    fun getListReportDetail(authKey: String) = reportedDetailRepo.getListReportedDetail(
        Firebase.firestore.collection("reported_account_detail"),
        authKey
    )
}