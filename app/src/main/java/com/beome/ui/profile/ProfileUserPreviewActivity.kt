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
import com.beome.model.LikedPostList
import com.beome.ui.authentication.login.LoginActivity
import com.beome.ui.feedback.PostDetailActivity
import com.beome.ui.post.PostViewModel
import com.beome.utilities.AdapterUtil
import com.beome.utilities.NetworkState
import com.beome.utilities.SharedPrefUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.item_post.view.*

class ProfileUserPreviewActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProfileUserPreviewBinding
    private lateinit var authKeyUserPreview : String
    private lateinit var authKeyUserLogedIn : String
    private lateinit var sharedPrefUtil: SharedPrefUtil
    private lateinit var adapterListPost : AdapterUtil<LikedPostList>
    private val viewModel: ProfileViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(ProfileViewModel::class.java)
    }
    private val viewModelPost: PostViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(PostViewModel::class.java)
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
                viewModel.followUser(Follow(authKeyUserLogedIn, authKeyUserPreview))
            }
            //unfollow action
            binding.buttonUnfollow.setOnClickListener {
                viewModel.unFollowUser(authKeyUserLogedIn, authKeyUserPreview)
            }
            //editprofile action
            binding.buttonEditProfile.setOnClickListener {
                //TODO intent to edit profile
            }
            getProfileUser()
            getListPost()
            Log.d("auth_key", "$authKeyUserLogedIn - $authKeyUserPreview")
            if(authKeyUserLogedIn == authKeyUserPreview){
                binding.buttonEditProfile.visibility = View.VISIBLE
                binding.buttonFollow.visibility = View.INVISIBLE
            }else{
                getFollowStatus()
            }

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
        adapterListPost = AdapterUtil(R.layout.item_post, arrayListOf(),
            { _, view, post ->
                Glide.with(this)
                    .load(post.post?.imagePost)
                    .placeholder(R.drawable.ic_placeholder_image)
                    .thumbnail(
                        Glide.with(this).load(post.post?.imagePost)
                            .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
                    )
                    .into(view.imageViewPost)
                if (post.post?.imgUser.isNullOrEmpty() || post.post?.imgUser == "null") {
                    Glide.with(this).load(R.drawable.ic_profile)
                        .into(view.imageViewUser)
                } else {
                    Glide.with(this).load(post.post?.imgUser).circleCrop()
                        .into(view.imageViewUser)
                }
                view.textViewUsername.text = post.post?.username
                view.textViewUsername.setOnClickListener {
                    val intent = Intent(this, ProfileUserPreviewActivity::class.java)
                    intent.putExtra(ConstantAuth.CONSTANT_AUTH_KEY, post.post?.authKey)
                    startActivity(intent)
                }
                view.textViewCountFeedback.text = post.post?.feedbackCount.toString()
                view.textViewCountLike.text = post.post?.likeCount.toString()
                //check post is liked or not
                if (post.isLiked) {
                    view.imageViewLikeActive.visibility = View.VISIBLE
                    view.imageViewLikeInactive.visibility = View.INVISIBLE
                } else {
                    view.imageViewLikeActive.visibility = View.INVISIBLE
                    view.imageViewLikeInactive.visibility = View.VISIBLE
                }

                //toggle like button
                view.imageViewLikeInactive.setOnClickListener {
                    //like post
                    viewModelPost.likePost(post.post?.idPost.toString(), authKeyUserLogedIn)
                    view.imageViewLikeInactive.visibility = View.INVISIBLE
                    view.imageViewLikeActive.visibility = View.VISIBLE
                }
                //toggle unlike button
                view.imageViewLikeActive.setOnClickListener {
                    //unlike post
                    viewModelPost.unlikePost(post.post?.idPost.toString(), authKeyUserLogedIn)
                    view.imageViewLikeInactive.visibility = View.VISIBLE
                    view.imageViewLikeActive.visibility = View.INVISIBLE
                }
            }, { _, post ->
                val intent = Intent(this, PostDetailActivity::class.java)
                intent.putExtra(ConstantPost.CONSTANT_ID_POST, post.post?.idPost.toString())
                intent.putExtra(ConstantPost.CONSTANT_POST_OWNER_KEY, post.post?.authKey)
                intent.putExtra(ConstantPost.CONSTANT_POST_IS_LIKED, post.isLiked)
                startActivity(intent)
            })
        viewModel.getListPostUser(authKeyUserPreview).observe(this,{
            adapterListPost.data = it
            binding.textViewPostsCount.text = it.size.toString()
        })
        binding.recyclerProfilePost.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerProfilePost.adapter = adapterListPost
    }

    private fun getFollowStatus(){
        //following state
        viewModel.getFollowStatus(authKeyUserLogedIn, authKeyUserPreview).observe(this,{
            Log.d("follow_state", it.toString())
            when(it) {
                NetworkState.LOADING -> {
                    binding.progressBarProfileUser.visibility = View.VISIBLE
                }
                NetworkState.SUCCESS -> {
                    binding.progressBarProfileUser.visibility = View.GONE
                    binding.buttonFollow.visibility = View.INVISIBLE
                    binding.buttonUnfollow.visibility = View.VISIBLE
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
                    binding.buttonFollow.visibility = View.VISIBLE
                    binding.buttonUnfollow.visibility = View.INVISIBLE
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