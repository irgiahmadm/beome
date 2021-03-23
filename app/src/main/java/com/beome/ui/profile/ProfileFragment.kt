package com.beome.ui.profile

import android.app.Activity
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.beome.MainActivity
import com.beome.R
import com.beome.constant.ConstantAuth
import com.beome.databinding.FragmentProfileBinding
import com.beome.ui.authentication.login.LoginActivity
import com.beome.utilities.NetworkState
import com.beome.utilities.SharedPrefUtil
import com.bumptech.glide.Glide

class ProfileFragment : Fragment() {
    private lateinit var binding : FragmentProfileBinding
    private val viewModel: ProfileViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(ProfileViewModel::class.java)
    }
    private lateinit var authKey : String
    private lateinit var sharedPrefUtil: SharedPrefUtil
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        sharedPrefUtil = SharedPrefUtil()
        sharedPrefUtil.start(context as Activity, ConstantAuth.CONSTANT_PREFERENCE)
        if (sharedPrefUtil.get(ConstantAuth.CONSTANT_AUTH_KEY).isNullOrEmpty() || sharedPrefUtil.get(ConstantAuth.CONSTANT_AUTH_KEY) == "null") {
            //belum login
            binding.groupSignedIn.visibility = View.GONE
            binding.groupNotSignedIn.visibility = View.VISIBLE
            binding.buttonSignin.setOnClickListener {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
            }
        }else{
            binding.toolbar.inflateMenu(R.menu.logout_menu)
            binding.toolbar.setOnMenuItemClickListener {
                onOptionsItemSelected(it)
            }
            binding.groupSignedIn.visibility = View.VISIBLE
            binding.groupNotSignedIn.visibility = View.GONE
            authKey = sharedPrefUtil.get(ConstantAuth.CONSTANT_AUTH_KEY) as String
            getProfileUser(authKey)
        }
        return binding.root
    }

    private fun getProfileUser(authKey : String){
        viewModel.getProfileUser(authKey).observe(viewLifecycleOwner,{
            binding.textViewFullname.text = it.fullName
            if(it.photoProfile.isEmpty() || it.photoProfile == "null"){
                Glide.with(requireContext()).load(R.drawable.ic_profile).into(binding.imageViewUserProfile)
            }else{
                Glide.with(requireContext()).load(it.photoProfile).circleCrop().into(binding.imageViewUserProfile)
            }
            binding.textViewFollowersCount.text = it.follower.toString()
            binding.textViewPostsCount.text = it.post.toString()
            binding.toolbar.title = it.username
        })

        viewModel._userState.observe(viewLifecycleOwner, {
            when(it){
                NetworkState.LOADING -> {

                }
                NetworkState.SUCCESS -> {

                }
                NetworkState.FAILED -> { }
                NetworkState.NOT_FOUND -> { }
                else -> {
                    Toast.makeText(
                        requireContext(),
                        "Something went wrong",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.logout_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_logout){
            sharedPrefUtil.clear()
            startActivity(Intent(requireContext(), MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK))
        }
        return super.onOptionsItemSelected(item)
    }

}