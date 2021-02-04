package com.beome.ui.report

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import com.beome.R
import com.beome.databinding.ActivityReportBinding
import com.beome.databinding.ComponentDialogReportBinding

class ReportActivity : AppCompatActivity() {
    private lateinit var binding : ActivityReportBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.textViewSelectReason.setOnClickListener {
            showDialogReason("")
        }
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun showDialogReason(reasonType: String) {
        val dialog = Dialog(this)
        val binding = ComponentDialogReportBinding.inflate(layoutInflater)
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(binding.root)
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