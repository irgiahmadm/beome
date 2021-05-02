package com.beome.ui.admin.feedback

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.beome.R
import com.beome.constant.ConstantPost
import com.beome.constant.ConstantReport
import com.beome.databinding.ActivityReportedFeedbackDetailBinding
import com.beome.model.FeedbackPostUserValue
import com.beome.model.ReportDetail
import com.beome.utilities.AdapterUtil
import com.beome.utilities.NetworkState
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_feedback_component.view.*
import kotlinx.android.synthetic.main.item_list_detail_report.view.*
import java.text.SimpleDateFormat

class ReportedFeedbackdetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityReportedFeedbackDetailBinding
    private lateinit var adapterFeedbackValue : AdapterUtil<FeedbackPostUserValue>
    private lateinit var adapter : AdapterUtil<ReportDetail>
    private val viewModel : ReportedFeedbackViewModel by lazy {
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(ReportedFeedbackViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportedFeedbackDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Report Detail"
        var idFeedback = ""
        if(intent.hasExtra(ConstantReport.CONSTANT_REPORT_KEY)){
            idFeedback = intent.getStringExtra(ConstantReport.CONSTANT_REPORT_KEY) as String
        }
        var idPost = ""
        if(intent.hasExtra(ConstantPost.CONSTANT_ID_POST)){
            idPost = intent.getStringExtra(ConstantPost.CONSTANT_ID_POST) as String
        }
        viewModel.setUpRepo()
        getReportedFeedback(idFeedback)
        getListReportDetail(idFeedback)
        takeDownFeedback(idFeedback, idPost)
    }

    @SuppressLint("SimpleDateFormat")
    private fun getReportedFeedback(idFeedback : String){
        viewModel.setUpReportedFeedback()
        viewModel.getReportedFeedback(idFeedback).observe(this,{
            if (it.feedback.photoProfile.isNullOrEmpty() || it.feedback.photoProfile == "null") {
                Glide.with(this).load(R.drawable.ic_profile)
                    .into(binding.imageViewUserFeedback)
            } else {
                Glide.with(this).load(it.feedback.photoProfile).circleCrop()
                    .into(binding.imageViewUserFeedback)
            }
            binding.textViewUsernameFeedback.text = it.feedback.username
            binding.textViewCommentFeedback.text = it.feedback.comment
            val dateCreated =
                SimpleDateFormat(ConstantPost.CONSTANT_POST_TIMESTAMP_FORMAT).format(it.feedback.createdAt)
            binding.textViewDateFeedback.text = dateCreated
            adapterFeedbackValue = AdapterUtil(
                R.layout.item_feedback_component,
                it.feedback.feedbackValue,
                { _, viewValue, itemValue ->
                    viewValue.textViewComponentReview.text = itemValue.componentName
                    viewValue.textViewValueFeedback.text =
                        itemValue.componentValue.toString()
                },
                { _, _ ->

                })
            binding.rvComponentFeedback.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.rvComponentFeedback.adapter = adapterFeedbackValue
        })
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun getListReportDetail(idPost: String){
        viewModel.setUpRepoDetailList()
        viewModel.setUpReportedDetailList()
        viewModel.getListReportDetail(idPost).observe(this,{
            binding.textViewReport.text = "Report (${it.size})"
            adapter.data = it
        })
        adapter = AdapterUtil(R.layout.item_list_detail_report, arrayListOf(), { _, view, item ->
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
        }, { _, _ ->

        })
        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter
    }

    private fun takeDownFeedback(idFeedback: String, idPost: String){
        viewModel.setUpTakedownFeedback()
        binding.buttonDelete.setOnClickListener {
            viewModel.takedownFeedback(idFeedback, idPost)
        }
        viewModel.stateTakedownFeedback.observe(this, {
            when(it){
                NetworkState.LOADING -> {

                }
                NetworkState.SUCCESS -> {
                    Toast.makeText(this, "Success to delete feedback", Toast.LENGTH_SHORT).show()
                    finish()
                }
                NetworkState.FAILED -> {
                    Toast.makeText(this, "Failed to delete feedback", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            finish()
        }
        return true
    }
}