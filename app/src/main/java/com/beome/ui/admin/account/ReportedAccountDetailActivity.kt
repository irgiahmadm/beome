package com.beome.ui.admin.account

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.beome.R
import com.beome.constant.ConstantPost
import com.beome.constant.ConstantReport
import com.beome.databinding.ActivityReportedAccountDetailBinding
import com.beome.model.ReportDetail
import com.beome.utilities.AdapterUtil
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_list_detail_report.view.*
import java.text.SimpleDateFormat

class ReportedAccountDetailActivity : AppCompatActivity() {
    private val viewModel : ReportedAccountViewModel by lazy {
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(ReportedAccountViewModel::class.java)
    }
    private lateinit var adapter : AdapterUtil<ReportDetail>
    private lateinit var binding : ActivityReportedAccountDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportedAccountDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var authKey = ""
        if(intent.hasExtra(ConstantReport.CONSTANT_REPORT_KEY)){
            authKey = intent.getStringExtra(ConstantReport.CONSTANT_REPORT_KEY) as String
        }
        getReportDetail(authKey)
        getListReportDetail(authKey)
    }

    private fun getReportDetail(authKey: String) {
        viewModel.setUpRepo()
        viewModel.setUpReportedAccount()
        viewModel.getReportedAccount(authKey).observe(this,{
            if(it.user.photoProfile.isEmpty() || it.user.photoProfile == "null"){
                Glide.with(this).load(R.drawable.ic_profile).into(binding.imageViewUserProfile)
            }else{
                Glide.with(this).load(it.user.photoProfile).circleCrop().into(binding.imageViewUserProfile)
            }
            binding.textViewFullname.text = it.user.fullName
            binding.textViewFollowersCount.text = it.user.follower.toString()
            binding.textViewPostsCount.text = it.user.post.toString()
        })
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun getListReportDetail(authKey: String){
        viewModel.setUpRepoDetailList()
        viewModel.setUpReportedDetailList()
        viewModel.getListReportDetail(authKey).observe(this,{
            binding.textViewReport.text = "Report (${it.size})"
            adapter.data = it
        })
        adapter = AdapterUtil(R.layout.item_list_detail_report, arrayListOf(), { pos, view, item ->
            if (item.imageUser.isEmpty() || item.imageUser == "null") {
                Glide.with(this).load(R.drawable.ic_profile).into(view.imageViewUserListReportDetail)
            } else {
                Glide.with(this).load(item.imageUser).circleCrop().into(view.imageViewUserListReportDetail)
            }
            view.textViewUsernameListReportDetail.text = item.username
            view.textViewReasonListReportDetail.text = item.reportReason
            view.textViewCommentListReportDetail.text = item.reportDesc
            val dateCreated = SimpleDateFormat(ConstantPost.CONSTANT_POST_TIMESTAMP_FORMAT).format(
                item.createdAt
            )
            view.textViewDateListReportDetail.text = dateCreated
        }, { pos, item ->

        })
        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter
    }

}