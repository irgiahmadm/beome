package com.beome.ui.feedback

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.RadioButton
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.beome.R
import com.beome.constant.ConstantAuth
import com.beome.constant.ConstantPost
import com.beome.databinding.ActivityFeedbackBinding
import com.beome.model.ComponentFeedbackPost
import com.beome.model.ComponentFeedbackSend
import com.beome.model.FeedbackPostUser
import com.beome.model.FeedbackPostUserValue
import com.beome.utilities.AdapterUtil
import com.beome.utilities.GlobalHelper
import com.beome.utilities.NetworkState
import com.beome.utilities.SharedPrefUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.item_feedback_value.view.*
import java.text.SimpleDateFormat
import java.util.*

class FeedbackActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFeedbackBinding
    private lateinit var adapter: AdapterUtil<ComponentFeedbackPost>
    private lateinit var sharedPrefUtil: SharedPrefUtil
    private var isFeedbackValueValid = false
    private val listFeedbackValue = arrayListOf<ComponentFeedbackSend>()
    private val viewModel: FeedbackViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(FeedbackViewModel::class.java)
    }
    private lateinit var idPost: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPrefUtil = SharedPrefUtil()
        sharedPrefUtil.start(this, ConstantAuth.CONSTANT_PREFERENCE)

        if (intent.getStringExtra(ConstantPost.CONSTANT_ID_POST) != null) {
            idPost = intent.getStringExtra(ConstantPost.CONSTANT_ID_POST)!!
            setToolbarProperties()
            getFeedbackComponent()
            getPostDetail()
            viewModel.setUpFeedbackValue()
            viewModel.setUpUsertoFeedback()
            getStateUsertoFeedback()
            binding.buttonSubmitFeedback.setOnClickListener {
                checkFeedbackValue()
                if (isFeedbackValueValid) {
                    submitFeedback()
                }
            }
        } else {
            finish()
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkFeedbackValue(){
        for (i in 0 until listFeedbackValue.size) {
            if (listFeedbackValue[i].componentValue == 0) {
                Toast.makeText(this, "Please choose all feedback value", Toast.LENGTH_SHORT).show()
                isFeedbackValueValid = false
                break
            } else {
                isFeedbackValueValid = true
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun submitFeedback(){
        val username: String
        val comment = binding.editTextComment.text.toString()
        val image : String = if (sharedPrefUtil.get(ConstantAuth.CONSTANT_AUTH_IMAGE) ==  null || sharedPrefUtil.get(
                ConstantAuth.CONSTANT_AUTH_IMAGE
            )!!.isEmpty()
        ) {
            ""
        }else{
            sharedPrefUtil.get(ConstantAuth.CONSTANT_AUTH_IMAGE)!!
        }
        val authKey: String = sharedPrefUtil.get(ConstantAuth.CONSTANT_AUTH_KEY)!!
        if (sharedPrefUtil.get(ConstantAuth.CONSTANT_AUTH_USERNAME) != null && sharedPrefUtil.get(
                ConstantAuth.CONSTANT_AUTH_KEY
            ) != null
        ) {
            username = sharedPrefUtil.get(ConstantAuth.CONSTANT_AUTH_USERNAME) as String
            val createdDate = Date()
            val tempListFeedbackValue = arrayListOf<FeedbackPostUserValue>()
            for (i in listFeedbackValue.indices) {
                val feedbackPostUserValue = FeedbackPostUserValue(listFeedbackValue[i].componentName, listFeedbackValue[i].componentValue!!)
                tempListFeedbackValue.add(feedbackPostUserValue)
            }
            val feedbackPostUser = FeedbackPostUser(authKey, idPost, GlobalHelper.getRandomString(20), username, image, comment, createdDate, 1, tempListFeedbackValue)
            viewModel.addUserFeedback(idPost, feedbackPostUser)
        } else {
            Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
        }
    }


    private fun getStateUsertoFeedback() {
        Log.d("feedback_stat_running", "feedback_stat_running")
        viewModel.addUserFeedbackState.observe(this, {
            Log.d("feedback_stat", it.toString())
            when(it){
                NetworkState.LOADING -> {

                }
                NetworkState.SUCCESS -> {
                    Toast.makeText(this, "Success to add feedback", Toast.LENGTH_SHORT).show()
                    finish()
                }
                NetworkState.FAILED -> {

                }
                NetworkState.NOT_FOUND -> {

                }
                else -> {

                }
            }
        })
    }

    @SuppressLint("SimpleDateFormat")
    private fun getPostDetail() {
        viewModel.getPostDetail(idPost).observe(this, {
            Log.d("TEST_VALUE_DATA", it.toString())
            Glide.with(this)
                .load(it.imagePost)
                .placeholder(R.drawable.ic_placeholder_image)
                .thumbnail(
                    Glide.with(this).load(it.imagePost).apply(
                        RequestOptions.bitmapTransform(BlurTransformation(25, 3))
                    )
                )
                .into(binding.imageViewPost)
            binding.textViewTitle.text = it.title
            binding.textViewUsername.text = it.username
            if (it.imgUser.isNullOrEmpty() || it.imgUser == "null") {
                Glide.with(this).load(R.drawable.ic_profile).into(binding.imageViewUser)
            } else {
                Glide.with(this).load(it.imgUser).circleCrop().into(binding.imageViewUser)
            }
            val dateCreated =
                SimpleDateFormat(ConstantPost.CONSTANT_POST_TIMESTAMP_FORMAT).format(it.createdAt)
            binding.textViewDateCreated.text = dateCreated
        })
    }

    private fun getFeedbackComponent() {
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
            for (i in it.indices) {
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

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setToolbarProperties() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Give Feedback"
        val upArrow = resources.getDrawable(R.drawable.ic_close_white,theme)
        supportActionBar?.setHomeAsUpIndicator(upArrow)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}