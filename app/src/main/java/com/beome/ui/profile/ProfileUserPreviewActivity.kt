package com.beome.ui.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.beome.R
import com.beome.constant.ConstantAuth
import com.beome.constant.ConstantPost
import com.beome.databinding.ActivityProfileUserPreviewBinding
import com.beome.model.Follow
import com.beome.model.Post
import com.beome.ui.authentication.login.LoginActivity
import com.beome.ui.feedback.PostDetailActivity
import com.beome.utilities.AdapterUtil
import com.beome.utilities.NetworkState
import com.beome.utilities.SharedPrefUtil
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_post.view.*

class ProfileUserPreviewActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProfileUserPreviewBinding
    private lateinit var authKeyUserPreview : String
    private lateinit var authKeyUserLogedIn : String
    private lateinit var sharedPrefUtil: SharedPrefUtil
    private lateinit var adapterListPost : AdapterUtil<Post>
    private val viewModel: ProfileViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(ProfileViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileUserPreviewBinding.inflate(layoutInflater)
        sharedPrefUtil = SharedPrefUtil()
        sharedPrefUtil.start(this, ConstantAuth.CONSTANT_PREFERENCE)
        if (sharedPrefUtil.get(ConstantAuth.CONSTANT_AUTH_KEY).isNullOrEmpty() || sharedPrefUtil.get(
                ConstantAuth.CONSTANT_AUTH_KEY) == "null") {
            //belum login
            binding.groupSignedIn.visibility = View.GONE
            binding.groupNotSignedIn.visibility = View.VISIBLE
            binding.buttonSignin.setOnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }else{
            authKeyUserLogedIn = sharedPrefUtil.get(ConstantAuth.CONSTANT_AUTH_KEY) as String
            binding.toolbar.inflateMenu(R.menu.logout_menu)
            binding.toolbar.setOnMenuItemClickListener {
                onOptionsItemSelected(it)
            }
            binding.groupSignedIn.visibility = View.VISIBLE
            binding.groupNotSignedIn.visibility = View.GONE
            if(intent.hasExtra(ConstantAuth.CONSTANT_AUTH_KEY)){
                authKeyUserPreview = intent.getStringExtra(ConstantAuth.CONSTANT_AUTH_KEY) as String
            }

            //follow action
            binding.buttonFollow.setOnClickListener {
                viewModel.followUser(Follow(authKeyUserLogedIn, authKeyUserPreview, 1))
            }
            getProfileUser()
            getListPost()
            getFollowStatus()
        }
        setContentView(binding.root)
    }

    private fun getProfileUser(){
        viewModel.getProfileUser(authKeyUserPreview).observe(this,{
            binding.textViewFullname.text = it.fullName
            if(it.photoProfile.isEmpty() || it.photoProfile == "null"){
                Glide.with(this).load(R.drawable.ic_profile).into(binding.imageViewUserProfile)
            }else{
                Glide.with(this).load(it.photoProfile).circleCrop().into(binding.imageViewUserProfile)
            }
            binding.textViewFollowersCount.text = it.follower.toString()
            binding.textViewPostsCount.text = it.post.toString()
            binding.toolbar.title = it.username
        })

        viewModel.userState.observe(this, {
            when(it){
                NetworkState.LOADING -> {
                    binding.progressBarProfileUser.visibility = View.VISIBLE
                }
                NetworkState.SUCCESS -> {
                    binding.progressBarProfileUser.visibility = View.GONE
                }
                NetworkState.FAILED -> {
                    binding.progressBarProfileUser.visibility = View.GONE
                }
                NetworkState.NOT_FOUND -> {
                    binding.progressBarProfileUser.visibility = View.GONE
                }
                else -> {
                    binding.progressBarProfileUser.visibility = View.GONE
                    Toast.makeText(
                        this,
                        "Something went wrong",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    private fun getListPost(){
        adapterListPost = AdapterUtil(
            R.layout.item_post, arrayListOf(),
            { _: Int, view: View, post: Post ->
                Glide.with(this)
                    .load(post.imagePost)
                    .placeholder(R.drawable.ic_placeholder_image)
                    .into(view.imageViewPost)
                if(post.imgUser.isNullOrEmpty() || post.imgUser == "null"){
                    Glide.with(this).load(R.drawable.ic_profile).into(view.imageViewUser)
                }else{
                    Glide.with(this).load(post.imgUser).circleCrop().into(view.imageViewUser)
                }
                view.textViewUsername.text = post.username
                view.textViewCountFeedback.text = post.feedbackCount.toString()
                view.textViewCountLike.text = post.likeCount.toString()
            },{ _, post ->
                val intent = Intent(this, PostDetailActivity::class.java)
                intent.putExtra(ConstantPost.CONSTANT_ID_POST, post.idPost)
                intent.putExtra(ConstantPost.CONSTANT_INTENT_FROM, ConstantPost.CONSTANT_INTENT_PROFILE_FRAGMENT)
                startActivity(intent)
            })
        viewModel.getListPostUser(authKeyUserPreview).observe(this,{
            adapterListPost.data = it!!
            binding.textViewPostsCount.text = it.size.toString()
        })
        binding.recyclerProfilePost.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerProfilePost.adapter = adapterListPost
    }

    private fun getFollowStatus(){
        viewModel.getFollowStatus(authKeyUserLogedIn, authKeyUserPreview).observe(this,{
            Log.d("follow", it.toString())
            when (it.followStatus) {
                1 -> {
                    //follow status 1 mean user who loged in follow user that they preview
                    binding.buttonFollow.visibility = View.GONE
                    binding.buttonUnfollow.visibility = View.VISIBLE
                }
                2 -> {
                    //follow status 2 mean user who loged in unfollow user that they preview
                    binding.buttonFollow.visibility = View.VISIBLE
                    binding.buttonUnfollow.visibility = View.GONE
                }
                else -> {
                    binding.buttonFollow.visibility = View.VISIBLE
                    binding.buttonUnfollow.visibility = View.GONE
                }
            }
        })

        //following state
        viewModel.followState.observe(this,{
            Log.d("follow_state", it.toString())
            when(it) {
                NetworkState.LOADING -> {
                    binding.progressBarProfileUser.visibility = View.VISIBLE
                }
                NetworkState.SUCCESS -> {
                    binding.progressBarProfileUser.visibility = View.GONE
                }
                NetworkState.FAILED -> {
                    binding.progressBarProfileUser.visibility = View.GONE
                    Toast.makeText(
                        this,
                        "Error get follow status",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                NetworkState.NOT_FOUND -> {
                    binding.progressBarProfileUser.visibility = View.GONE
                }
                else -> {
                    binding.progressBarProfileUser.visibility = View.GONE
                    Toast.makeText(
                        this,
                        "Something went wrong",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

}