package com.beome.ui.admin

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.beome.R
import com.beome.databinding.FragmentReportedPostBinding
import com.beome.model.ReportedPost
import com.beome.utilities.AdapterUtil
import com.beome.utilities.NetworkState
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_reported_post.view.*
import java.util.*


class ReportedPostFragment : Fragment() {
    private lateinit var binding : FragmentReportedPostBinding
    private lateinit var adapter : AdapterUtil<ReportedPost>
    private val viewModel: ReportedPostViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(ReportedPostViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentReportedPostBinding.inflate(layoutInflater, container, false)
        getLisReportedPost()
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun getLisReportedPost(){
        viewModel.apply {
            setUpRepo()
            setUpReportedPost()
            viewModel.getListReportedPost().observe(viewLifecycleOwner,{
                adapter.data = it
            })
        }
        adapter =
            AdapterUtil(R.layout.item_reported_post, arrayListOf(), { pos, view, reportedPost ->
                view.textViewTitleReportedPost.text = reportedPost.post.title.capitalize(Locale.getDefault())
                view.textViewReportCountPost.text = "${reportedPost.counter} Reported"
                Glide.with(requireContext()).load(reportedPost.post.imagePost).into(view.imageViewReportedPost)
            }, { pos, reportedPost ->
//                startActivity(Intent(this, DetailReportedPost::class.java))
            })
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter
        viewModel.stateReportedPost.observe(viewLifecycleOwner,{
            when(it){
                NetworkState.LOADING -> {

                }
                NetworkState.SUCCESS -> {

                }
                NetworkState.FAILED -> {

                }
                NetworkState.NOT_FOUND -> {

                }
                else -> {
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}