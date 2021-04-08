package com.beome.ui.home.recent

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
import com.beome.databinding.FragmentRecentPostBinding
import com.beome.model.LikedPost
import com.beome.model.LikedPostList
import com.beome.model.Post
import com.beome.ui.feedback.PostDetailActivity
import com.beome.ui.post.PostViewModel
import com.beome.utilities.AdapterUtil
import com.beome.utilities.SharedPrefUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.item_post.view.*

class RecentPostFragment : Fragment() {

    private lateinit var binding: FragmentRecentPostBinding
    private lateinit var adapterRecentPost: AdapterUtil<Post>
    private lateinit var sharedPrefUtil: SharedPrefUtil
    private val viewModel: RecentPostViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(RecentPostViewModel::class.java)
    }
    private val viewModelPost: PostViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(PostViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecentPostBinding.inflate(layoutInflater, container, false)
        sharedPrefUtil = SharedPrefUtil()
        sharedPrefUtil.start(context as Activity, ConstantAuth.CONSTANT_PREFERENCE)
        val authKey = sharedPrefUtil.get(ConstantAuth.CONSTANT_AUTH_KEY) as String
        adapterRecentPost = AdapterUtil(R.layout.item_post, arrayListOf(),
            { _, view, post ->
                Glide.with(requireContext())
                    .load(post.imagePost)
                    .placeholder(R.drawable.ic_placeholder_image)
                    .thumbnail(
                        Glide.with(requireContext()).load(post.imagePost)
                            .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
                    )
                    .into(view.imageViewPost)
                if (post.imgUser.isNullOrEmpty() || post.imgUser == "null") {
                    Glide.with(requireContext()).load(R.drawable.ic_profile)
                        .into(view.imageViewUser)
                } else {
                    Glide.with(requireContext()).load(post.imgUser).circleCrop()
                        .into(view.imageViewUser)
                }
                view.textViewUsername.text = post.username
                view.textViewCountFeedback.text = post.feedbackCount.toString()
                view.textViewCountLike.text = post.likeCount.toString()
                //check post is liked or not
               /* if (post.isLiked) {
                    view.imageViewLikeActive.visibility = View.VISIBLE
                    view.imageViewLikeInactive.visibility = View.INVISIBLE
                } else {
                    view.imageViewLikeActive.visibility = View.INVISIBLE
                    view.imageViewLikeInactive.visibility = View.VISIBLE
                }*/

                //toggle like button
                view.imageViewLikeInactive.setOnClickListener {
                    //like post
                    viewModelPost.likePost(LikedPost(post.idPost.toString(), authKey))
                    view.imageViewLikeInactive.visibility = View.INVISIBLE
                    view.imageViewLikeActive.visibility = View.VISIBLE

                }
                //toggle unlike button
                view.imageViewLikeActive.setOnClickListener {
                    //unlike post
                    //TODO ADD FUNCTION TO UNLIKE
                    view.imageViewLikeInactive.visibility = View.VISIBLE
                    view.imageViewLikeActive.visibility = View.INVISIBLE
                }
            }, { _, post ->
                val intent = Intent(requireContext(), PostDetailActivity::class.java)
                intent.putExtra(ConstantPost.CONSTANT_ID_POST, post.idPost.toString())
                startActivity(intent)
            })
        viewModel.getListRecentPost().observe(viewLifecycleOwner, {
            adapterRecentPost.data = it
        })
        binding.recyclerRecentPost.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerRecentPost.adapter = adapterRecentPost
        return binding.root
    }


}