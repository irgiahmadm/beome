package com.beome.ui.admin.post

import androidx.lifecycle.*
import com.beome.ui.admin.ReportDetailRepository
import com.beome.utilities.NetworkState
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ReportedPostViewModel : ViewModel(){
    private val reportedtedPostRepo = ReportedPostRepository(viewModelScope)
    private val _reportedPostRepo = MutableLiveData<ReportedPostRepository>()
    private val reportedDetailRepo = ReportDetailRepository(viewModelScope)
    private val _reportedDetailRepo = MutableLiveData<ReportDetailRepository>()
    lateinit var stateReportedPost : LiveData<NetworkState>
    private lateinit var stateReportDetailList : LiveData<NetworkState>
    lateinit var stateTakedownPost : LiveData<NetworkState>

    fun setUpRepo(){
        _reportedPostRepo.postValue(reportedtedPostRepo)
    }

    fun setUpRepoDetailList(){
        _reportedDetailRepo.postValue(reportedDetailRepo)
    }

    fun setUpReportedPost(){
        stateReportedPost = Transformations.switchMap(_reportedPostRepo, ReportedPostRepository::stateReportedPost)
    }

    fun setUpTakedownPost(){
        stateTakedownPost = Transformations.switchMap(_reportedPostRepo, ReportedPostRepository::stateTakedownPost)
    }

    fun setUpReportedDetailList(){
        stateReportDetailList = Transformations.switchMap(_reportedDetailRepo, ReportDetailRepository::stateReportedDetail)
    }

    fun takedownPost(idPost: String) = reportedtedPostRepo.takedownPost(idPost)

    fun getReportedPost(idPost: String) = reportedtedPostRepo.getReportedPost(idPost)

    fun getListReportedPost() = reportedtedPostRepo.getListReportedPost()

    fun getListReportDetail(idPost: String) = reportedDetailRepo.getListReportedDetail(
        Firebase.firestore.collection("reported_post_detail"),
        idPost
    )
}