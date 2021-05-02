package com.beome.ui.admin.account

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
import com.beome.constant.ConstantReport
import com.beome.databinding.FragmentReportedAccountBinding
import com.beome.model.ReportedAccount
import com.beome.utilities.AdapterUtil
import com.beome.utilities.NetworkState
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_reported_account.view.*


class ReportedAccountFragment : Fragment() {
    private lateinit var binding: FragmentReportedAccountBinding
    private lateinit var adapter: AdapterUtil<ReportedAccount>

    private val viewModel: ReportedAccountViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(ReportedAccountViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentReportedAccountBinding.inflate(layoutInflater, container, false)
        getLisReportedAccount()
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun getLisReportedAccount() {
        viewModel.apply {
            setUpRepo()
            setUpReportedAccount()
            viewModel.getListReportedAccount().observe(viewLifecycleOwner, {
                adapter.data = it
            })
        }
        adapter =
            AdapterUtil(R.layout.item_reported_account, arrayListOf(), { _, view, reportedData ->
                view.textViewUsernameReportAccount.text = reportedData.user.username
                view.textViewReportCountAccount.text = "${reportedData.counter} Reported"
                if (reportedData.user.photoProfile.isEmpty() || reportedData.user.photoProfile == "null") {
                    Glide.with(requireContext()).load(R.drawable.ic_profile)
                        .into(view.imageViewUserReportAccount)
                } else {
                    Glide.with(requireContext()).load(reportedData.user.photoProfile).circleCrop()
                        .into(view.imageViewUserReportAccount)
                }
            }, { _, reportedData ->
                startActivity(
                    Intent(
                        requireContext(),
                        ReportedAccountDetailActivity::class.java
                    ).putExtra(ConstantReport.CONSTANT_REPORT_KEY, reportedData.user.authKey)
                )
            })
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter
        viewModel.stateReportedAccount.observe(viewLifecycleOwner, {
            when (it) {
                NetworkState.LOADING -> {

                }
                NetworkState.SUCCESS -> {

                }
                NetworkState.FAILED -> {

                }
                NetworkState.NOT_FOUND -> {

                }
                else -> {
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })
    }

}