package com.beome.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.beome.R

class AddPostFragment : Fragment() {

    private lateinit var addPostViewModel: AddPostViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        addPostViewModel = ViewModelProvider(this).get(AddPostViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_add_post, container, false)
        val textView: TextView = root.findViewById(R.id.text_notifications)

        return root
    }
}