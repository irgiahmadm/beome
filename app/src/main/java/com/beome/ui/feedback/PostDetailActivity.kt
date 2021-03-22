package com.beome.ui.feedback

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.beome.R
import com.beome.constant.ConstantPost
import com.beome.databinding.ActivityPostDetailBinding
import com.beome.model.FeedbackPostUser
import com.beome.model.FeedbackPostUserValue
import com.beome.utilities.AdapterUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.activity_feedback.view.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.item_feedback_component.view.*
import kotlinx.android.synthetic.main.item_list_feedback.view.*
import java.text.SimpleDateFormat

class PostDetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityPostDetailBinding
    private lateinit var idPost : String
    private lateinit var adapterFeedbackUser : AdapterUtil<FeedbackPostUser>
    private lateinit var adapterFeedbackValue : AdapterUtil<FeedbackPostUserValue>
    private val viewModel: FeedbackViewModel by lazy{
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(FeedbackViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Post Detail"
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if(intent.getStringExtra(ConstantPost.CONSTANT_ID_POST) != null){
            idPost = intent.getStringExtra(ConstantPost.CONSTANT_ID_POST) as String
            //give feedback
            binding.buttonGiveFeedback.setOnClickListener {
                startActivity(
                    Intent(
                        this,
                        FeedbackActivity::class.java
                    ).putExtra(ConstantPost.CONSTANT_ID_POST, idPost)
                )
            }
            getDetailPost()
            getListFeedback()
        }
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun getListFeedback(){
        viewModel.getListFeedbackPost(idPost).observe(this,{
            binding.textViewFeedback.text = "Feedback (${it.user.size})"
            Log.d("feedback_value_dtl", it.feedbackValue.toString())
            adapterFeedbackUser =
                AdapterUtil(R.layout.item_list_feedback, it.user, { _, view, item ->
                    view.textViewUsernameFeedback.text = item.username
                    val dateCreated = SimpleDateFormat(ConstantPost.CONSTANT_POST_TIMESTAMP_FORMAT).parse(item.createdAt)
                    val dateFormatted = SimpleDateFormat("dd-MM-yyyy").format(dateCreated!!)
                    view.textViewDateFeedback.text = dateFormatted
                    if(item.photoProfile.isNullOrEmpty() || item.photoProfile == "null"){
                        Glide.with(this).load(R.drawable.ic_profile).into(view.imageViewUserFeedback)
                    }else{
                        Glide.with(this).load(item.photoProfile).circleCrop().into(view.imageViewUserFeedback)
                    }
                    view.textViewCommentFeedback.text = item.comment
                    adapterFeedbackValue = AdapterUtil(R.layout.item_feedback_component,it.feedbackValue,{posValue, viewValue, itemValue ->
                        viewValue.textViewComponentReview.text = itemValue.componentName
                        viewValue.textViewValueFeedback.text = itemValue.componentValue.toString()
                    },{ _, item ->

                    })
                    view.rvComponentFeedback.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                    view.rvComponentFeedback.adapter = adapterFeedbackValue

                }, { _, _ ->

                })

            binding.recyclerViewReview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.recyclerViewReview.adapter = adapterFeedbackUser

        })

    }

    @SuppressLint("SimpleDateFormat")
    private fun getDetailPost(){
        //get data detail
        viewModel.getPostDetail(idPost).observe(this,{
            Glide.with(this)
                .load(it.imagePost)
                .thumbnail(Glide.with(this).load(it.imagePost).apply(
                    RequestOptions.bitmapTransform(BlurTransformation(25,3))))
                .into(binding.imageViewPost)
            binding.textViewTitle.text = it.title
            binding.textViewDescription.text = it.description
            binding.textViewUsername.text = it.username
            if(it.imgUser.isNullOrEmpty() || it.imgUser == "null"){
                Glide.with(this).load(R.drawable.ic_profile).into(binding.imageViewUser)
            }else{
                Glide.with(this).load(it.imgUser).circleCrop().into(binding.imageViewUser)
            }
            val dateCreated = SimpleDateFormat(ConstantPost.CONSTANT_POST_TIMESTAMP_FORMAT).parse(it.createdAt)
            val dateFormatted = SimpleDateFormat("dd-MM-yyyy").format(dateCreated!!)
            binding.textViewDateCreated.text = dateFormatted
            binding.textViewLikeCount.text = it.likeCount.toString()
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}