package com.beome.ui.post

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.beome.MainActivity
import com.beome.R
import com.beome.constant.ConstantPost
import com.beome.databinding.ActivityEditPostBinding
import com.beome.ui.feedback.FeedbackViewModel
import com.beome.utilities.NetworkState
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import java.util.*

class EditPostActivity : AppCompatActivity() {
    private lateinit var binding : ActivityEditPostBinding
    private lateinit var idPost : String
    private val viewModelDetailPost: FeedbackViewModel by lazy{
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(FeedbackViewModel::class.java)
    }
    private val viewModelPost : PostViewModel by lazy {
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(PostViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if(intent.hasExtra(ConstantPost.CONSTANT_ID_POST)){
            idPost = intent.getStringExtra(ConstantPost.CONSTANT_ID_POST) as String
            getPostDetail(idPost)
            editPost()
        }
    }

    private fun getPostDetail(idPost : String){
        viewModelDetailPost.getPostDetail(idPost).observe(this, {
            Glide.with(this)
                .load(it.imagePost)
                .placeholder(R.drawable.ic_placeholder_image)
                .into(binding.imagePost)
            binding.editTextPostTitle.setText(it.title)
            binding.editTextPostDesc.setText(it.description)
        })
    }

    private fun editPost(){
        viewModelPost.setUpUpdatePost()
        getStateEditPost()
        binding.buttonEdit.setOnClickListener {
            val title = binding.editTextPostTitle
            val desc = binding.editTextPostDesc
            when {
                title.text.toString().isEmpty() -> {
                    title.error = "Title can not be empty"
                    title.requestFocus()
                }
                desc.text.toString().isEmpty() -> {
                    desc.error = "Description can not be empty"
                    desc.requestFocus()
                }
                else -> {
                    //edit post
                    viewModelPost.updatePost(idPost, title.text.toString()
                        .toLowerCase(Locale.getDefault()), desc.text.toString())
                }
            }
        }
    }

    private fun getStateEditPost(){
        viewModelPost.editPostState.observe(this, {
            when(it){
                NetworkState.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                NetworkState.SUCCESS -> {
                    binding.buttonEdit.text = ""
                  Toast.makeText(
                        this,
                        "Succes, your post is updated",
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> finish()
        }
        return true
    }
}