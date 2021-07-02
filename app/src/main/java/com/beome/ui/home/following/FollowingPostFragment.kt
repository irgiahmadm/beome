package com.beome.ui.home.following

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.beome.R
import com.beome.constant.ConstantAuth
import com.beome.constant.ConstantPost
import com.beome.databinding.FragmentFollowingPostBinding
import com.beome.model.LikedPostList
import com.beome.ui.feedback.PostDetailActivity
import com.beome.ui.post.PostViewModel
import com.beome.ui.profile.ProfileUserPreviewActivity
import com.beome.utilities.AdapterUtil
import com.beome.utilities.SharedPrefUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.item_post.view.*


class FollowingPostFragment : Fragment() {

    private lateinit var binding: FragmentFollowingPostBinding
    private lateinit var sharedPrefUtil: SharedPrefUtil
    private lateinit var adapter: AdapterUtil<LikedPostList>
    private val viewModelPost: PostViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(PostViewModel::class.java)
    }
    private val viewModelFollowing: FollowingPostViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(FollowingPostViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFollowingPostBinding.inflate(layoutInflater, container, false)
        sharedPrefUtil = SharedPrefUtil()
        sharedPrefUtil.start(context as Activity, ConstantAuth.CONSTANT_PREFERENCE)
        initUi()
        return binding.root
    }

    private fun initUi(){
        val authKey = sharedPrefUtil.get(ConstantAuth.CONSTANT_AUTH_KEY) as String
        adapter = AdapterUtil(R.layout.item_post, arrayListOf(),
            { _, view, post ->
                Glide.with(requireContext())
                    .load(post.post?.imagePost)
                    .placeholder(R.drawable.ic_placeholder_image)
                    .thumbnail(
                        Glide.with(requireContext()).load(post.post?.imagePost)
                            .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
                    )
                    .into(view.imageViewPost)
                if (post.post?.imgUser.isNullOrEmpty() || post.post?.imgUser == "null") {
                    Glide.with(requireContext()).load(R.drawable.ic_profile)
                        .into(view.imageViewUser)
                } else {
                    Glide.with(requireContext()).load(post.post?.imgUser).circleCrop()
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
                intent.putExtra(ConstantPost.CONSTANT_POST_OWNER_KEY, post.post?.authKey)
                intent.putExtra(ConstantPost.CONSTANT_POST_IS_LIKED, post.isLiked)
                startActivity(intent)
            })
        viewModelFollowing.getFollowingPost(authKey).observe(viewLifecycleOwner, {
            adapter.data = it
        })
        binding.recyclerFollowingPost.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerFollowingPost.adapter = adapter
    }
}