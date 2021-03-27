package com.beome.ui.search

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.beome.R
import com.beome.constant.ConstantPost
import com.beome.databinding.FragmentSearchPostBinding
import com.beome.model.Post
import com.beome.ui.feedback.PostDetailActivity
import com.beome.utilities.AdapterUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.item_post.view.*

class SearchPostFragment : Fragment() {

    private lateinit var binding : FragmentSearchPostBinding
    private lateinit var adapterSearchPost : AdapterUtil<Post>
    private val viewModel : SearchViewModel by lazy {
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(SearchViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchPostBinding.inflate(inflater,container, false)
        binding.searchView.apply {
            requestFocus()
            isIconified = false
            requestFocusFromTouch()
            setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    if (TextUtils.isEmpty(newText)) {
                        searchPost("")
                    }else{
                        searchPost(newText)
                    }
                    return true
                }


            })
        }
        return binding.root
    }

    private fun searchPost(searchQuery : String){
        adapterSearchPost = AdapterUtil(R.layout.item_post, arrayListOf(),
            {_: Int, view: View, post: Post ->
                Glide.with(requireContext())
                    .load(post.imagePost)
                    .placeholder(R.drawable.ic_placeholder_image)
                    .thumbnail(
                        Glide.with(requireContext()).load(post.imagePost).apply(
                            RequestOptions.bitmapTransform(BlurTransformation(25,3))))
                    .into(view.imageViewPost)
                if(post.imgUser.isNullOrEmpty() || post.imgUser == "null"){
                    Glide.with(requireContext()).load(R.drawable.ic_profile).into(view.imageViewUser)
                }else{
                    Glide.with(requireContext()).load(post.imgUser).circleCrop().into(view.imageViewUser)
                }
                view.textViewUsername.text = post.username
                view.textViewCountFeedback.text = post.feedbackCount.toString()
                view.textViewCountLike.text = post.likeCount.toString()
            },{ _, post ->
                val intent = Intent(requireContext(), PostDetailActivity::class.java)
                intent.putExtra(ConstantPost.CONSTANT_ID_POST, post.idPost)
                startActivity(intent)
            })
        viewModel.getListPost(searchQuery).observe(viewLifecycleOwner,{
            Log.d("data_search_post", it.toString())
            adapterSearchPost.data = it
        })
        binding.recyclerSearchPost.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerSearchPost.adapter = adapterSearchPost
    }



}