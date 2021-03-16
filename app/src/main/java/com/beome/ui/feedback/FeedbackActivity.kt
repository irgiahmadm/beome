package com.beome.ui.feedback

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.beome.R
import com.beome.constant.ConstantPost
import com.beome.databinding.ActivityFeedbackBinding
import com.beome.model.ComponentFeedbackPost
import com.beome.model.ComponentFeedbackSend
import com.beome.utilities.AdapterUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.item_feedback_value.view.*
import java.text.SimpleDateFormat

class FeedbackActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFeedbackBinding
    private lateinit var adapter: AdapterUtil<ComponentFeedbackPost>
    private val listFeedbackValue = arrayListOf<ComponentFeedbackSend>()
    private val viewModel : FeedbackViewModel by lazy {
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(FeedbackViewModel::class.java)
    }
    private lateinit var idPost : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonSubmitFeedback.setOnClickListener {
            for (i in 0 until listFeedbackValue.size){
                Log.d("FEEDBACK_VALUE", listFeedbackValue[i].toString())
            }
        }
        if(intent.getStringExtra(ConstantPost.CONSTANT_ID_POST) != null){
            idPost = intent.getStringExtra(ConstantPost.CONSTANT_ID_POST)!!
            getFeedbackComponent()
            getPostDetail()
        }else{
            finish()
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getPostDetail(){
        viewModel.getPostDetail(idPost).observe(this,{
            Log.d("TEST_VALUE_DATA", it.toString())
            Glide.with(this)
                .load(it.imagePost)
                .thumbnail(
                    Glide.with(this).load(it.imagePost).apply(
                        RequestOptions.bitmapTransform(BlurTransformation(25,3))))
                .into(binding.imageViewPost)
            binding.textViewTitle.text = it.title
            binding.textViewUsername.text = it.username
            if(it.imgUser.isNullOrEmpty() || it.imgUser == "null"){
                Glide.with(this).load(R.drawable.ic_profile).into(binding.imageViewUser)
            }else{
                Glide.with(this).load(it.imgUser).circleCrop().into(binding.imageViewUser)
            }
            val dateCreated = SimpleDateFormat(ConstantPost.CONSTANT_POST_TIMESTAMP_FORMAT).parse(it.createdAt)
            val dateFormatted = SimpleDateFormat("dd-MM-yyyy").format(dateCreated!!)
            binding.textViewDateCreated.text = dateFormatted
        })
    }

    private fun getFeedbackComponent(){
        adapter = AdapterUtil(R.layout.item_feedback_value, arrayListOf(),
            { pos, view, feedbackComponent ->
                view.textViewComponentName.text = feedbackComponent.componentName
                view.radioGroup.setOnCheckedChangeListener { radioGroup, checkedId ->
                    val radioButton: RadioButton = findViewById(checkedId)
                    val feedbackPost = ComponentFeedbackSend()
                    feedbackPost.componentValue = radioButton.text.toString().toInt()
                    feedbackPost.componentName = feedbackComponent.componentName.toString()
                    listFeedbackValue[pos] = feedbackPost
                }
            }, { _, _ ->

            })
        viewModel.getFeedbackComponent(idPost).observe(this, {
            listFeedbackValue.clear()
            for (i in it.indices){
                val componentSend = ComponentFeedbackSend()
                componentSend.componentName = it[i].componentName.toString()
                componentSend.componentValue = 0
                listFeedbackValue.add(componentSend)
            }
            adapter.data = it
        })
        binding.recyclerViewFeedbackComponent.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewFeedbackComponent.adapter = adapter
    }
}