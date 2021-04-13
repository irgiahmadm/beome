package com.beome.ui.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.beome.MainActivity
import com.beome.R
import com.beome.constant.ConstantAuth
import com.beome.constant.ConstantPost
import com.beome.databinding.FragmentProfileBinding
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

class ProfileFragment : Fragment() {
    private lateinit var binding : FragmentProfileBinding
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
    private lateinit var authKey : String
    private lateinit var sharedPrefUtil: SharedPrefUtil
    private lateinit var adapterListPost : AdapterUtil<LikedPostList>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        sharedPrefUtil = SharedPrefUtil()
        sharedPrefUtil.start(context as Activity, ConstantAuth.CONSTANT_PREFERENCE)
        if (sharedPrefUtil.get(ConstantAuth.CONSTANT_AUTH_KEY).isNullOrEmpty() || sharedPrefUtil.get(ConstantAuth.CONSTANT_AUTH_KEY) == "null") {
            //belum login
            binding.groupSignedIn.visibility = View.GONE
            binding.groupNotSignedIn.visibility = View.VISIBLE
            binding.buttonSignin.setOnClickListener {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
            }
        }else{
            binding.toolbar.inflateMenu(R.menu.logout_menu)
            binding.toolbar.setOnMenuItemClickListener {
                onOptionsItemSelected(it)
            }
            binding.groupSignedIn.visibility = View.VISIBLE
            binding.groupNotSignedIn.visibility = View.GONE
            authKey = sharedPrefUtil.get(ConstantAuth.CONSTANT_AUTH_KEY) as String
            getProfileUser()
            getListPost()
        }
        return binding.root
    }

    private fun getProfileUser(){
        viewModel.getProfileUser(authKey).observe(viewLifecycleOwner,{
            binding.textViewFullname.text = it.fullName
            if(it.photoProfile.isEmpty() || it.photoProfile == "null"){
                Glide.with(requireContext()).load(R.drawable.ic_profile).into(binding.imageViewUserProfile)
            }else{
                Glide.with(requireContext()).load(it.photoProfile).circleCrop().into(binding.imageViewUserProfile)
            }
            binding.textViewFollowersCount.text = it.follower.toString()
            binding.textViewPostsCount.text = it.post.toString()
            binding.toolbar.title = it.username
        })

        viewModel.userState.observe(viewLifecycleOwner, {
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
                        requireContext(),
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
                Glide.with(requireContext())
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
                    val intent = Intent(requireContext(), ProfileUserPreviewActivity::class.java)
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
                    viewModelPost.likePost(post.post?.idPost.toString(), authKey)
                    view.imageViewLikeInactive.visibility = View.INVISIBLE
                    view.imageViewLikeActive.visibility = View.VISIBLE
                }
                //toggle unlike button
                view.imageViewLikeActive.setOnClickListener {
                    //unlike post
                    viewModelPost.unlikePost(post.post?.idPost.toString(), authKey)
                    view.imageViewLikeInactive.visibility = View.VISIBLE
                    view.imageViewLikeActive.visibility = View.INVISIBLE
                }
            }, { _, post ->
                val intent = Intent(requireContext(), PostDetailActivity::class.java)
                intent.putExtra(ConstantPost.CONSTANT_ID_POST, post.post?.idPost.toString())
                startActivity(intent)
            })
        viewModel.getListPostUser(authKey).observe(viewLifecycleOwner,{
            adapterListPost.data = it!!
            binding.textViewPostsCount.text = it.size.toString()
        })
        binding.recyclerProfilePost.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerProfilePost.adapter = adapterListPost
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.logout_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_logout){
            sharedPrefUtil.clear()
            startActivity(Intent(requireContext(), MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK))
        }
        return super.onOptionsItemSelected(item)
    }

}