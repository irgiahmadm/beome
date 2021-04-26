package com.beome.ui.report

import android.app.Dialog
import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.beome.constant.ConstantAuth
import com.beome.constant.ConstantReport
import com.beome.databinding.ActivityReportBinding
import com.beome.databinding.ComponentDialogReportFeedbackBinding
import com.beome.model.Report
import com.beome.utilities.SharedPrefUtil
import java.util.*

class ReportActivity : AppCompatActivity() {
    private lateinit var binding : ActivityReportBinding
    private lateinit var reportCategory : String
    private var reason : String? = null
    private lateinit var sharedPrefUtil: SharedPrefUtil
    private lateinit var idReport : String
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
        binding.textViewSelectReason.setOnClickListener {
            when(reportCategory){
                ConstantReport.CONSTANT_REPORT_FEEDBACK -> {
                    showDialogReportFeedback()
                }
                ConstantReport.CONSTANT_REPORT_POST -> {

                }
                ConstantReport.CONSTANT_REPORT_ACCOUNT -> {

                }
            }
        }
        binding.buttonSubmitReport.setOnClickListener {
            if(reason != null && binding.editTextTextReportDetail.text.isEmpty()){
                val username = sharedPrefUtil.get(ConstantAuth.CONSTANT_AUTH_USERNAME)!!
                val imageUser = sharedPrefUtil.get(ConstantAuth.CONSTANT_AUTH_IMAGE)!!
                val reason = reason
                val reportDesc = binding.editTextTextReportDetail.text.toString()
                val createdAt = Date()
                val updatedAt = Date()

                val report = Report(idReport, reportCategory, reason!!, reportDesc, username, imageUser, createdAt, updatedAt)
            }else{
                Toast.makeText(this, "Fill all form", Toast.LENGTH_SHORT).show()
            }


        }
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> finish()
        }
        return true
    }

}