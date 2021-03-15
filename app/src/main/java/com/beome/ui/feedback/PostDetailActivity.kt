package com.beome.ui.feedback

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import com.beome.R
import com.beome.constant.ConstantPost
import com.beome.databinding.ActivityPostDetailBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.BlurTransformation
import java.text.SimpleDateFormat

class PostDetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityPostDetailBinding
    private lateinit var idPost : String
    private val viewModel: FeedbackViewModel by lazy{
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(FeedbackViewModel::class.java)
    }
    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Post Detail"
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if(intent.getStringExtra(ConstantPost.CONSTANT_ID_POST) != null){
            idPost = intent.getStringExtra(ConstantPost.CONSTANT_ID_POST) as String
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
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}