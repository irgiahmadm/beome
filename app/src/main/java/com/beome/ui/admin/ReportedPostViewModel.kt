package com.beome.ui.admin

import androidx.lifecycle.*
import com.beome.utilities.NetworkState

class ReportedPostViewModel : ViewModel(){
    val reportedtedPostRepo = ReportedPostRepository(viewModelScope)
    val _reportedPostRepo = MutableLiveData<ReportedPostRepository>()
    lateinit var stateReportedPost : LiveData<NetworkState>

    fun setUpRepo(){
        _reportedPostRepo.postValue(reportedtedPostRepo)
    }

    fun setUpReportedPost(){
        stateReportedPost = Transformations.switchMap(_reportedPostRepo, ReportedPostRepository::stateReportedPost)
    }

    fun getListReportedPost() = reportedtedPostRepo.getListReportedPost()
}