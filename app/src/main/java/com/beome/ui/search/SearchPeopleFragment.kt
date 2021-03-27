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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.beome.R
import com.beome.databinding.FragmentSearchPeopleBinding
import com.beome.model.User
import com.beome.utilities.AdapterUtil
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_list_user.view.*
import kotlinx.android.synthetic.main.item_post.view.*

class SearchPeopleFragment : Fragment() {
    private lateinit var binding : FragmentSearchPeopleBinding
    private lateinit var adapterSearchUser : AdapterUtil<User>
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
        binding = FragmentSearchPeopleBinding.inflate(inflater,container, false)
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
                        searchUser("")
                    }else{
                        searchUser(newText)
                    }
                    return true
                }

            })
        }
        return binding.root
    }

    private fun searchUser(searchQuery : String){
        adapterSearchUser = AdapterUtil(R.layout.item_list_user, arrayListOf(),
            {_: Int, view: View, user: User ->
                if(user.photoProfile.isEmpty() || user.photoProfile == "null"){
                    Glide.with(requireContext()).load(R.drawable.ic_profile).into(view.imageViewUserSearch)
                }else{
                    Glide.with(requireContext()).load(user.photoProfile).circleCrop().into(view.imageViewUserSearch)
                }
                view.textViewUsernameSearch.text = user.username
            },{ _, post ->
                /*val intent = Intent(requireContext(), PostDetailActivity::class.java)
                intent.putExtra(ConstantPost.CONSTANT_ID_POST, post.idPost)
                startActivity(intent)*/
            })
        viewModel.getListUser(searchQuery).observe(viewLifecycleOwner,{
            Log.d("data_search_user", it.toString())
            adapterSearchUser.data = it
        })

        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerSearchUser.layoutManager = layoutManager
        val divider = DividerItemDecoration(binding.recyclerSearchUser.context, layoutManager.orientation)
        binding.recyclerSearchUser.addItemDecoration(divider)
        binding.recyclerSearchUser.adapter = adapterSearchUser
    }

}