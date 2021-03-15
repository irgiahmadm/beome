package com.beome.ui.home.recent

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.beome.R
import com.beome.databinding.FragmentRecentPostBinding
import com.beome.model.Post
import com.beome.utilities.AdapterUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.item_post.view.*

class RecentPostFragment : Fragment() {

    private lateinit var binding : FragmentRecentPostBinding
    private lateinit var adapterRecentPost : AdapterUtil<Post>
    private val viewModel : RecentPostViewModel by lazy {
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(RecentPostViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecentPostBinding.inflate(layoutInflater, container, false)
        adapterRecentPost = AdapterUtil(R.layout.item_post, arrayListOf(),
            {_: Int, view: View, post: Post ->
                Glide.with(requireContext())
                    .load(post.imagePost)
                    .thumbnail(Glide.with(requireContext()).load(post.imagePost).apply(RequestOptions.bitmapTransform(BlurTransformation(25,3))))
                    .into(view.imageViewPost)
                if(post.imgUser.isNullOrEmpty() || post.imgUser == "null"){
                    Glide.with(requireContext()).load(R.drawable.ic_profile).into(view.imageViewUser)
                }else{
                    Glide.with(requireContext()).load(post.imgUser).circleCrop().into(view.imageViewUser)
                }
                view.textViewUsername.text = post.username
                view.textViewCountFeedback.text = post.feedbackCount.toString()
                view.textViewCountLike.text = post.likeCount.toString()
            },{ _, _ ->

            })
        viewModel.getListRecentPost().observe(viewLifecycleOwner,{
            adapterRecentPost.data = it
        })
        binding.recyclerRecentPost.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerRecentPost.adapter = adapterRecentPost
        return binding.root
    }


}