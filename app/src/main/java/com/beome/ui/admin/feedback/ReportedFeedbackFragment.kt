package com.beome.ui.admin.feedback

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.beome.R
import com.beome.constant.ConstantPost
import com.beome.constant.ConstantReport
import com.beome.databinding.FragmentReportedFeedbackBinding
import com.beome.model.FeedbackPostUserValue
import com.beome.model.ReportedFeedback
import com.beome.utilities.AdapterUtil
import com.beome.utilities.NetworkState
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_feedback_component.view.*
import kotlinx.android.synthetic.main.item_reported_feedback.view.*
import java.text.SimpleDateFormat

class ReportedFeedbackFragment : Fragment() {
    private lateinit var adapter : AdapterUtil<ReportedFeedback>
    private lateinit var adapterFeedbackValue : AdapterUtil<FeedbackPostUserValue>
    private val viewModel: ReportedFeedbackViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(ReportedFeedbackViewModel::class.java)
    }
    private lateinit var binding : FragmentReportedFeedbackBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentReportedFeedbackBinding.inflate(layoutInflater, container, false)
        getLisReportedFeedback()
        return binding.root
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun getLisReportedFeedback(){
        viewModel.apply {
            setUpRepo()
            setUpReportedFeedback()
            viewModel.getListReportedFeedback().observe(viewLifecycleOwner,{

                adapter.data = it
            })
        }
        adapter =
            AdapterUtil(R.layout.item_reported_feedback, arrayListOf(), { _, view, reportedData ->
                view.textViewReportCountFeedback.text = "${reportedData.counter} Reported"
                view.textViewUsernameReportedFeedback.text = reportedData.feedback.username
                val dateCreated =
                    SimpleDateFormat(ConstantPost.CONSTANT_POST_TIMESTAMP_FORMAT).format(
                        reportedData.feedback.createdAt
                    )
                view.textViewDateReportedFeedback.text = dateCreated

                if (reportedData.feedback.photoProfile.isNullOrEmpty() || reportedData.feedback.photoProfile == "null") {
                    Glide.with(this).load(R.drawable.ic_profile)
                        .into(view.imageViewUserReportedFeedback)
                } else {
                    Glide.with(this).load(reportedData.feedback.photoProfile).circleCrop()
                        .into(view.imageViewUserReportedFeedback)
                }

                view.textViewCommentReportedFeedback.text = reportedData.feedback.comment
                adapterFeedbackValue = AdapterUtil(
                    R.layout.item_feedback_component,
                    reportedData.feedback.feedbackValue,
                    { _, viewValue, itemValue ->
                        viewValue.textViewComponentReview.text = itemValue.componentName
                        viewValue.textViewValueFeedback.text =
                            itemValue.componentValue.toString()
                    },
                    { _, _ ->

                    })
                view.rvComponentReportedFeedback.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                view.rvComponentReportedFeedback.adapter = adapterFeedbackValue

            }, { _, reportedData ->
                startActivity(
                    Intent(
                        requireContext(),
                        ReportedFeedbackdetailActivity::class.java
                    ).putExtra(ConstantReport.CONSTANT_REPORT_KEY, reportedData.feedback.idFeedback)
                )
            })
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter
        viewModel.stateReportedFeedback.observe(viewLifecycleOwner,{
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