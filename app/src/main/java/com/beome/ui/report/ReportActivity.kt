package com.beome.ui.report

import android.app.Dialog
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.beome.constant.ConstantAuth
import com.beome.constant.ConstantReport
import com.beome.databinding.ActivityReportBinding
import com.beome.databinding.ComponentDialogReportAccountBinding
import com.beome.databinding.ComponentDialogReportFeedbackBinding
import com.beome.databinding.ComponentDialogReportPostBinding
import com.beome.model.*
import com.beome.utilities.GlobalHelper
import com.beome.utilities.NetworkState
import com.beome.utilities.SharedPrefUtil
import java.util.*

class ReportActivity : AppCompatActivity() {
    private lateinit var binding : ActivityReportBinding
    private lateinit var reportCategory : String
    private var reason : String? = null
    private lateinit var sharedPrefUtil: SharedPrefUtil
    private lateinit var idReport : String
    private var feedbackObject = ReportedFeedback()
    private var postObject = ReportedPost()
    private var accountObject = ReportedAccount()
    private val viewModel : ReportViewModel by lazy {
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(ReportViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPrefUtil = SharedPrefUtil()
        sharedPrefUtil.start(this, ConstantAuth.CONSTANT_PREFERENCE)
        if (intent.hasExtra(ConstantReport.CONSTANT_REPORT)){
            reportCategory = intent.getStringExtra(ConstantReport.CONSTANT_REPORT) as String
        }
        if(intent.hasExtra(ConstantReport.CONSTANT_REPORT_KEY)){
            idReport = intent.getStringExtra(ConstantReport.CONSTANT_REPORT_KEY) as String
        }
        if(intent.hasExtra(ConstantReport.CONSTANT_REPORT_OBJECT_FEEDBACK)){
            feedbackObject.feedback = intent.getParcelableExtra<FeedbackPostUser>(ConstantReport.CONSTANT_REPORT_OBJECT_FEEDBACK) as FeedbackPostUser
        }
        if(intent.hasExtra(ConstantReport.CONSTANT_REPORT_OBJECT_POST)){
            postObject.post = intent.getParcelableExtra<Post>(ConstantReport.CONSTANT_REPORT_OBJECT_POST) as Post
        }
        if(intent.hasExtra(ConstantReport.CONSTANT_REPORT_OBJECT_ACCOUNT)){
            accountObject.user = intent.getParcelableExtra<User>(ConstantReport.CONSTANT_REPORT_OBJECT_ACCOUNT) as User
        }
        binding.textViewSelectReason.setOnClickListener {
            when(reportCategory){
                ConstantReport.CONSTANT_REPORT_FEEDBACK -> {
                    showDialogReportFeedback()
                }
                ConstantReport.CONSTANT_REPORT_POST -> {
                    showDialogReportPost()
                }
                ConstantReport.CONSTANT_REPORT_ACCOUNT -> {
                    showDialogReportPost()
                }
            }
        }
        viewModel.setUpRepo()
        getStateReportFeedback()
        getStateReportAccount()
        getStateReportPost()
        binding.buttonSubmitReport.setOnClickListener {
            if(reason != null && binding.editTextTextReportDetail.text.isNotEmpty()){
                val username = sharedPrefUtil.get(ConstantAuth.CONSTANT_AUTH_USERNAME)!!
                val imageUser = sharedPrefUtil.get(ConstantAuth.CONSTANT_AUTH_IMAGE)!!
                val reason = reason
                val reportDesc = binding.editTextTextReportDetail.text.toString()
                val createdAt = Date()
                val updatedAt = Date()
                val report = ReportDetail(GlobalHelper.getRandomString(16),idReport, reportCategory, reason!!, reportDesc, username, imageUser, createdAt, updatedAt)
                when(reportCategory){
                    ConstantReport.CONSTANT_REPORT_FEEDBACK -> {
                        feedbackObject.counter = 1
                        viewModel.reportFeedback(feedbackObject, report)
                    }
                    ConstantReport.CONSTANT_REPORT_POST -> {
                        postObject.counter = 1
                        val postObject = postObject
                        viewModel.reportPost(postObject, report)
                    }
                    ConstantReport.CONSTANT_REPORT_ACCOUNT -> {
                        accountObject.counter = 1
                        val accountObject = accountObject
                        viewModel.reportAccount(accountObject, report)
                    }
                }

            }else{
                Toast.makeText(this, "Fill all form", Toast.LENGTH_SHORT).show()
            }

        }
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun getStateReportFeedback(){
        viewModel.setUpReportFeedback()
        viewModel.reportFeedbackState.observe(this,{
            when(it){
                NetworkState.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                NetworkState.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Success to report feedback", Toast.LENGTH_SHORT).show()
                    finish()
                }
                NetworkState.FAILED -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Failed to report feedback", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Failed, something went wrong", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun getStateReportPost(){
        viewModel.setUpReportPost()
        viewModel.reportPostState.observe(this,{
            when(it){
                NetworkState.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                NetworkState.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Success to report post", Toast.LENGTH_SHORT).show()
                    finish()
                }
                NetworkState.FAILED -> {
                    binding.progressBar.visibility = View.VISIBLE
                    Toast.makeText(this, "Failed to report post", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    binding.progressBar.visibility = View.VISIBLE
                    Toast.makeText(this, "Failed, something went wrong", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun getStateReportAccount(){
        viewModel.setUpReportAccount()
        viewModel.reportAccountState.observe(this,{
            when(it){
                NetworkState.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                NetworkState.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Success to report account", Toast.LENGTH_SHORT).show()
                    finish()
                }
                NetworkState.FAILED -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Failed to report account", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Failed, something went wrong", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun showDialogReportFeedback() {
        val dialog = Dialog(this)
        val bindingDialog = ComponentDialogReportFeedbackBinding.inflate(layoutInflater)
        bindingDialog.radioGroup.setOnCheckedChangeListener { rg, pos ->
            val selectedId = rg.checkedRadioButtonId
            val radioButton : RadioButton = bindingDialog.root.findViewById(selectedId)
            bindingDialog.buttonOk.setOnClickListener {
                reason = radioButton.text.toString()
                binding.textViewSelectReason.text = reason
                dialog.dismiss()
            }
        }
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(bindingDialog.root)
            setCancelable(true)
            show()
        }
    }

    private fun showDialogReportAccount(){
        val dialog = Dialog(this)
        val bindingDialog = ComponentDialogReportAccountBinding.inflate(layoutInflater)
        bindingDialog.radioGroup.setOnCheckedChangeListener { rg, pos ->
            val selectedId = rg.checkedRadioButtonId
            val radioButton : RadioButton = bindingDialog.root.findViewById(selectedId)
            bindingDialog.buttonOk.setOnClickListener {
                reason = radioButton.text.toString()
                binding.textViewSelectReason.text = reason
                dialog.dismiss()
            }
        }
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(bindingDialog.root)
            setCancelable(true)
            show()
        }
    }

    private fun showDialogReportPost(){
        val dialog = Dialog(this)
        val bindingDialog = ComponentDialogReportPostBinding.inflate(layoutInflater)
        bindingDialog.radioGroup.setOnCheckedChangeListener { rg, pos ->
            val selectedId = rg.checkedRadioButtonId
            val radioButton : RadioButton = bindingDialog.root.findViewById(selectedId)
            bindingDialog.buttonOk.setOnClickListener {
                reason = radioButton.text.toString()
                binding.textViewSelectReason.text = reason
                dialog.dismiss()
            }
        }
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(bindingDialog.root)
            setCancelable(true)
            show()
        }
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> finish()
        }
        return true
    }

}