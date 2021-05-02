package com.beome.ui.admin.post

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.beome.R
import com.beome.constant.ConstantPost
import com.beome.constant.ConstantReport
import com.beome.databinding.ActivityReportPostDetailBinding
import com.beome.model.ReportDetail
import com.beome.utilities.AdapterUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.item_list_detail_report.view.*
import java.text.SimpleDateFormat
import java.util.*

class ReportedPostDetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityReportPostDetailBinding
    private lateinit var adapter : AdapterUtil<ReportDetail>
    private val viewModel: ReportedPostViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(ReportedPostViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportPostDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Report Detail"
        var idPost = ""
        if (intent.hasExtra(ConstantReport.CONSTANT_REPORT_KEY)){
            idPost = intent.getStringExtra(ConstantReport.CONSTANT_REPORT_KEY) as String
        }
        getReportedPostDetail(idPost)
        getListReportDetail(idPost)
    }

    @SuppressLint("SimpleDateFormat")
    private fun getReportedPostDetail(idPost : String){
        viewModel.setUpRepo()
        viewModel.getReportedPost(idPost).observe(this,{
            Glide.with(this)
                .load(it.post.imagePost)
                .placeholder(R.drawable.ic_placeholder_image)
                .thumbnail(
                    Glide.with(this).load(it.post.imagePost).apply(
                        RequestOptions.bitmapTransform(BlurTransformation(25, 3))
                    )
                )
                .into(binding.imageViewPost)
            binding.textViewTitle.text = it.post.title.capitalize(Locale.getDefault())
            binding.textViewDescription.text = it.post.description
            binding.textViewUsername.text = it.post.username
            if (it.post.imgUser.isNullOrEmpty() || it.post.imgUser == "null") {
                Glide.with(this).load(R.drawable.ic_profile).into(binding.imageViewUser)
            } else {
                Glide.with(this).load(it.post.imgUser).circleCrop().into(binding.imageViewUser)
            }
            val dateCreated = SimpleDateFormat(ConstantPost.CONSTANT_POST_TIMESTAMP_FORMAT).parse(
                it.post.createdAt
            )
            val dateFormatted = SimpleDateFormat("dd-MM-yyyy").format(dateCreated!!)
            binding.textViewDateCreated.text = dateFormatted
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