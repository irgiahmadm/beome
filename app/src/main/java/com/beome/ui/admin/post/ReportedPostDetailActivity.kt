package com.beome.ui.admin.post

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
import com.beome.databinding.ActivityReportPostDetailBinding
import com.beome.model.ReportDetail
import com.beome.utilities.AdapterUtil
import com.beome.utilities.NetworkState
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
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Report Detail"
        var idPost = ""
        if (intent.hasExtra(ConstantReport.CONSTANT_REPORT_KEY)){
            idPost = intent.getStringExtra(ConstantReport.CONSTANT_REPORT_KEY) as String
        }
        viewModel.setUpRepo()
        getReportedPostDetail(idPost)
        getListReportDetail(idPost)
        takedownPost(idPost)
    }

    @SuppressLint("SimpleDateFormat")
    private fun getReportedPostDetail(idPost : String){
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

    private fun takedownPost(idPost: String){
        viewModel.setUpTakedownPost()
        binding.buttonDelete.setOnClickListener {
            deletePostConfirmation(idPost)
        }
        viewModel.stateTakedownPost.observe(this, {
            when(it){
                NetworkState.LOADING -> {

                }
                NetworkState.SUCCESS -> {
                    Toast.makeText(this, "Success to delete post", Toast.LENGTH_SHORT).show()
                    finish()
                }
                NetworkState.FAILED -> {
                    Toast.makeText(this, "Failed to delete post", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
    private fun deletePostConfirmation(idPost: String){
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            setCancelable(true)
            setTitle(getString(R.string.delete_confirmation))
            setMessage(getString(R.string.delete_post_message))
            setPositiveButton(
                getString(R.string.delete)
            ) { _, _ ->
                viewModel.takedownPost(idPost)
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