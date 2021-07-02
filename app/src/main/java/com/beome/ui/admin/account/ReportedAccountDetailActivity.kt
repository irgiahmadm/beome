package com.beome.ui.admin.account

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.beome.R
import com.beome.constant.ConstantPost
import com.beome.constant.ConstantReport
import com.beome.databinding.ActivityReportedAccountDetailBinding
import com.beome.model.ReportDetail
import com.beome.utilities.AdapterUtil
import com.beome.utilities.NetworkState
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_reported_account_detail.*
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
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Report Detail"
        var authKey = ""
        if(intent.hasExtra(ConstantReport.CONSTANT_REPORT_KEY)){
            authKey = intent.getStringExtra(ConstantReport.CONSTANT_REPORT_KEY) as String
        }
        viewModel.setUpRepo()
        getReportDetail(authKey)
        getListReportDetail(authKey)
        takedownAccount(authKey)
    }

    private fun getReportDetail(authKey: String) {
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

    private fun takedownAccount(authKey: String){
        viewModel.setUpTakedowonAccount()
        binding.buttonDelete.setOnClickListener {
            deleteConfirmation(authKey)
        }
        viewModel.stateTakedownAccount.observe(this,{
            when(it){
                NetworkState.LOADING -> {

                }
                NetworkState.SUCCESS -> {
                    Toast.makeText(this, "Success to delete account", Toast.LENGTH_SHORT).show()
                    finish()
                }
                NetworkState.FAILED -> {
                    Toast.makeText(this, "Failed to delete account", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun deleteConfirmation(authKey: String){
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            setCancelable(true)
            setTitle(getString(R.string.delete_confirmation))
            setMessage(getString(R.string.delete_feedback_message))
            setPositiveButton(
                getString(R.string.delete)
            ) { _, _ ->
                viewModel.takedownAccount(authKey)
            }
            setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
        }
        alertDialog.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            finish()
        }
        return true
    }
}