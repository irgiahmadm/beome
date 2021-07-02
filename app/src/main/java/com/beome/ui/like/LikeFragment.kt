package com.beome.ui.like

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
import com.beome.databinding.FragmentLikeBinding
import com.beome.model.LikedPostList
import com.beome.ui.feedback.PostDetailActivity
import com.beome.ui.post.PostViewModel
import com.beome.utilities.AdapterUtil
import com.beome.utilities.SharedPrefUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.item_post.view.*

class LikeFragment : Fragment() {
    private lateinit var binding : FragmentLikeBinding
    private lateinit var adapterLikedPost : AdapterUtil<LikedPostList>
    private val viewModel : PostViewModel by lazy {
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(PostViewModel::class.java)
    }
    private lateinit var sharedPrefUtil: SharedPrefUtil

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLikeBinding.inflate(layoutInflater, container, false)
        sharedPrefUtil = SharedPrefUtil()
        sharedPrefUtil.start(context as Activity, ConstantAuth.CONSTANT_PREFERENCE)
        val authKey = sharedPrefUtil.get(ConstantAuth.CONSTANT_AUTH_KEY) as String
        adapterLikedPost = AdapterUtil(R.layout.item_post, arrayListOf(),
            { _: Int, view: View, likedPost: LikedPostList ->
                Glide.with(requireContext())
                    .load(likedPost.post?.imagePost)
                    .placeholder(R.drawable.ic_placeholder_image)
                    .thumbnail(
                        Glide.with(requireContext()).load(likedPost.post?.imagePost)
                            .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
                    )
                    .into(view.imageViewPost)
                if (likedPost.post?.imgUser.isNullOrEmpty() || likedPost.post?.imgUser == "null") {
                    Glide.with(requireContext()).load(R.drawable.ic_profile)
                        .into(view.imageViewUser)
                } else {
                    Glide.with(requireContext()).load(likedPost.post?.imgUser).circleCrop()
                        .into(view.imageViewUser)
                }
                view.textViewUsername.text = likedPost.post?.username
                view.textViewCountFeedback.text = likedPost.post?.feedbackCount.toString()
                view.textViewCountLike.text = likedPost.post?.likeCount.toString()
                //check post is liked or not
                if (likedPost.isLiked) {
                    view.imageViewLikeActive.visibility = View.VISIBLE
                    view.imageViewLikeInactive.visibility = View.INVISIBLE
                } else {
                    view.imageViewLikeActive.visibility = View.INVISIBLE
                    view.imageViewLikeInactive.visibility = View.VISIBLE
                }
                //toggle like button
                view.imageViewLikeInactive.setOnClickListener {
                    //like post
                    viewModel.likePost(likedPost.post?.idPost!!, authKey)
                    view.imageViewLikeInactive.visibility = View.INVISIBLE
                    view.imageViewLikeActive.visibility = View.VISIBLE

                }
                //toggle unlike button
                view.imageViewLikeActive.setOnClickListener {
                    //unlike post
                    viewModel.unlikePost(likedPost.post?.idPost!!, authKey)
                    view.imageViewLikeInactive.visibility = View.VISIBLE
                    view.imageViewLikeActive.visibility = View.INVISIBLE
                }
            }, { _, likedPost ->
                val intent = Intent(requireContext(), PostDetailActivity::class.java)
                intent.putExtra(ConstantPost.CONSTANT_ID_POST, likedPost.post?.idPost)
                intent.putExtra(ConstantPost.CONSTANT_POST_OWNER_KEY, likedPost.post?.authKey)
                intent.putExtra(ConstantPost.CONSTANT_POST_IS_LIKED, true)
                startActivity(intent)
            })
        viewModel.getListLikedPost(authKey).observe(viewLifecycleOwner, {
            adapterLikedPost.data = it
        })
        binding.recyclerLikedPost.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerLikedPost.adapter = adapterLikedPost
        return binding.root
    }

}