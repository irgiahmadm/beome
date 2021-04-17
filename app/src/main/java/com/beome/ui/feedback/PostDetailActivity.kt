package com.beome.ui.feedback

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.beome.R
import com.beome.constant.ConstantAuth
import com.beome.constant.ConstantPost
import com.beome.databinding.ActivityPostDetailBinding
import com.beome.model.FeedbackPostUser
import com.beome.model.FeedbackPostUserValue
import com.beome.model.FeedbackSummary
import com.beome.ui.post.EditPostActivity
import com.beome.ui.post.PostViewModel
import com.beome.ui.profile.ProfileUserPreviewActivity
import com.beome.ui.report.ReportActivity
import com.beome.utilities.AdapterUtil
import com.beome.utilities.NetworkState
import com.beome.utilities.SharedPrefUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.item_feedback_component.view.*
import kotlinx.android.synthetic.main.item_feedback_summary.view.*
import kotlinx.android.synthetic.main.item_list_feedback.view.*
import java.text.SimpleDateFormat

class PostDetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityPostDetailBinding
    private lateinit var idPost : String
    private lateinit var idPostOwner : String
    private lateinit var sharedPrefUtil: SharedPrefUtil
    private lateinit var authKey : String
    private lateinit var adapterFeedbackUser : AdapterUtil<FeedbackPostUser>
    private lateinit var adapterFeedbackValue : AdapterUtil<FeedbackPostUserValue>
    private lateinit var adapterFeedbackSummary : AdapterUtil<FeedbackSummary>
    private val viewModel: FeedbackViewModel by lazy{
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(FeedbackViewModel::class.java)
    }
    private val viewModelPost: PostViewModel by lazy{
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(PostViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Post Detail"
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        sharedPrefUtil = SharedPrefUtil()
        sharedPrefUtil.start(this, ConstantAuth.CONSTANT_PREFERENCE)
        authKey = sharedPrefUtil.get(ConstantAuth.CONSTANT_AUTH_KEY) as String

        if(intent.getStringExtra(ConstantPost.CONSTANT_ID_POST) != null){
            //get key post owner
            if(intent.hasExtra(ConstantPost.CONSTANT_POST_OWNER_KEY)){
                idPostOwner = intent.getStringExtra(ConstantPost.CONSTANT_POST_OWNER_KEY) as String
                //post is created by user who loged in
                if(authKey == idPostOwner){
                    binding.buttonGiveFeedback.visibility = View.GONE
                    binding.buttonEditPost.visibility = View.VISIBLE
                }else{
                    binding.buttonGiveFeedback.visibility = View.VISIBLE
                    binding.buttonEditPost.visibility = View.GONE
                }
            }
            idPost = intent.getStringExtra(ConstantPost.CONSTANT_ID_POST) as String

            //edit post
            binding.buttonEditPost.setOnClickListener {
                startActivity(
                    Intent(
                        this,
                        EditPostActivity::class.java
                    ).putExtra(ConstantPost.CONSTANT_ID_POST, idPost)
                )
            }
            //intent to activity give feedback
            binding.buttonGiveFeedback.setOnClickListener {
                startActivity(
                    Intent(
                        this,
                        FeedbackActivity::class.java
                    ).putExtra(ConstantPost.CONSTANT_ID_POST, idPost)
                )
            }
            if(intent.getStringExtra(ConstantPost.CONSTANT_INTENT_FROM) != null){
                if(intent.getStringExtra(ConstantPost.CONSTANT_INTENT_FROM) == ConstantPost.CONSTANT_INTENT_PROFILE_FRAGMENT){
                    binding.giveFeedbackSection.visibility = View.GONE
                }else{
                    binding.giveFeedbackSection.visibility = View.VISIBLE
                }
            }
            getDetailPost()
            getListFeedback()
            getStateDeletePost()
        }
    }

    private fun getStateDeletePost(){
        viewModelPost.setUpDeletePost()
        viewModelPost.deletePostState.observe(this,{
            when(it){
                NetworkState.LOADING -> {

                }
                NetworkState.SUCCESS -> {
                    Toast.makeText(
                        this,
                        "Post deleted",
                        Toast.LENGTH_SHORT
                    )
                    finish()
                }
                NetworkState.FAILED -> {
                    Toast.makeText(this, "Failed to update post", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(
                        this,
                        "Failed to update post, something went wrong",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    private fun deletePostConfirmation(){
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            setCancelable(true)
            setTitle(getString(R.string.delete_confirmation))
            setMessage(getString(R.string.delete_post_message))
            setPositiveButton(
                getString(R.string.delete)
            ) { _, _ ->
                viewModelPost.deletePost(idPost)
            }
            setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
        }
        alertDialog.show()
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun getListFeedback(){
        viewModel.getListFeedbackPost(idPost).observe(this, {

            binding.textViewFeedback.text = "Feedback (${it.size})"
            adapterFeedbackUser =
                AdapterUtil(R.layout.item_list_feedback, it, {pos, view, item ->
                    view.textViewUsernameFeedback.text = item.username
                    view.textViewUsernameFeedback.setOnClickListener {
                        val intent = Intent(this, ProfileUserPreviewActivity::class.java)
                        intent.putExtra(ConstantAuth.CONSTANT_AUTH_KEY, item.authKey)
                        startActivity(intent)
                    }
                    val dateCreated =
                        SimpleDateFormat(ConstantPost.CONSTANT_POST_TIMESTAMP_FORMAT).parse(item.createdAt)
                    val dateFormatted = SimpleDateFormat("dd-MM-yyyy").format(dateCreated!!)
                    view.textViewDateFeedback.text = dateFormatted
                    if (item.photoProfile.isNullOrEmpty() || item.photoProfile == "null") {
                        Glide.with(this).load(R.drawable.ic_profile)
                            .into(view.imageViewUserFeedback)
                    } else {
                        Glide.with(this).load(item.photoProfile).circleCrop()
                            .into(view.imageViewUserFeedback)
                    }

                    view.textViewCommentFeedback.text = item.comment
                        adapterFeedbackValue = AdapterUtil(
                            R.layout.item_feedback_component,
                            it[pos].feedbackValue,
                            { _, viewValue, itemValue ->
                                viewValue.textViewComponentReview.text = itemValue.componentName
                                viewValue.textViewValueFeedback.text =
                                    itemValue.componentValue.toString()
                            },
                            { _, _ ->

                            })
                        view.rvComponentFeedback.layoutManager =
                            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                        view.rvComponentFeedback.adapter = adapterFeedbackValue

                }, { _, _ ->

                })

            binding.recyclerViewReview.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.recyclerViewReview.adapter = adapterFeedbackUser

            //recap feedback
            val listOfFeedbackValue = arrayListOf<FeedbackPostUserValue>()
            for (i in it.indices){
                listOfFeedbackValue.addAll(it[i].feedbackValue)
            }
            setFeedbackSummary(listOfFeedbackValue)
        })

    }

    private fun setFeedbackSummary(listFeedback : ArrayList<FeedbackPostUserValue>){
        Log.d("listfeedback", listFeedback.toString())
        val mapFeedback = listFeedback.groupBy { feedbackPostVal ->
            feedbackPostVal.componentName
        }.mapValues {mapEntry ->
            mapEntry.value.map { obj -> obj.componentValue }.average()
        }

        val listRecapFeedback = arrayListOf<FeedbackSummary>()
        for((key, value) in mapFeedback){
            val feedbackSummary = FeedbackSummary(key, value)
            listRecapFeedback.add(feedbackSummary)
        }

        adapterFeedbackSummary =
            AdapterUtil(R.layout.item_feedback_summary, listRecapFeedback, { _, view, feedbackSummary ->
                view.textViewFeedbackComponentSummary.text = feedbackSummary.feedbackComponent
                view.ratingBarFeedbackSummary.rating = feedbackSummary.feedbackValue.toFloat()
                view.textViewValueFeedbackSummary.text = String.format("%.1f", feedbackSummary.feedbackValue)
            }, { _, _ ->

            })
        binding.recyclerViewFeedbackSummary.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewFeedbackSummary.adapter = adapterFeedbackSummary
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDetailPost(){
        //get data detail
        viewModel.getPostDetail(idPost).observe(this,{post ->
            Glide.with(this)
                .load(post.imagePost)
                .placeholder(R.drawable.ic_placeholder_image)
                .thumbnail(Glide.with(this).load(post.imagePost).apply(
                    RequestOptions.bitmapTransform(BlurTransformation(25,3))))
                .into(binding.imageViewPost)
            binding.textViewTitle.text = post.title
            binding.textViewDescription.text = post.description
            binding.textViewUsername.text = post.username
            binding.textViewUsername.setOnClickListener {
                val intent = Intent(this, ProfileUserPreviewActivity::class.java)
                intent.putExtra(ConstantAuth.CONSTANT_AUTH_KEY, post.authKey)
                startActivity(intent)
            }
            if(post.imgUser.isNullOrEmpty() || post.imgUser == "null"){
                Glide.with(this).load(R.drawable.ic_profile).into(binding.imageViewUser)
            }else{
                Glide.with(this).load(post.imgUser).circleCrop().into(binding.imageViewUser)
            }
            val dateCreated = SimpleDateFormat(ConstantPost.CONSTANT_POST_TIMESTAMP_FORMAT).parse(post.createdAt)
            val dateFormatted = SimpleDateFormat("dd-MM-yyyy").format(dateCreated!!)
            binding.textViewDateCreated.text = dateFormatted
            binding.textViewLikeCount.text = post.likeCount.toString()
        })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        idPostOwner = intent.getStringExtra(ConstantPost.CONSTANT_POST_OWNER_KEY) as String
        if(authKey != idPostOwner){
            menuInflater.inflate(R.menu.menu_report, menu)
        }else{
            menuInflater.inflate(R.menu.menu_delete, menu)
        }
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
            return true
        }else if(item.itemId == R.id.menu_report){
            startActivity(Intent(this, ReportActivity::class.java))
        }else if(item.itemId == R.id.menu_delete){
            deletePostConfirmation()
        }
        return super.onOptionsItemSelected(item)
    }
}