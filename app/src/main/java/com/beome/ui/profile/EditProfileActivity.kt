package com.beome.ui.profile

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.text.InputType
import android.util.Log
import android.util.Patterns
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.beome.R
import com.beome.constant.ConstantAuth
import com.beome.databinding.ActivityEditProfileBinding
import com.beome.model.Post
import com.beome.model.User
import com.beome.utilities.*
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
import com.google.firebase.storage.FirebaseStorage
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_post_detail.*
import java.io.File
import java.util.*
import java.util.regex.Pattern

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityEditProfileBinding
    private lateinit var authKey : String
    private val viewModel: ProfileViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(ProfileViewModel::class.java)
    }
    private var image: Image? = null
    private var imageCroppedResult: Uri? = null
    private lateinit  var sharedPrefUtil : SharedPrefUtil
    private val c = Calendar.getInstance()
    private val year = c.get(Calendar.YEAR)
    private val month = c.get(Calendar.MONTH)
    private val day = c.get(Calendar.DAY_OF_MONTH)


    private val storageUserProfileRef = FirebaseStorage.getInstance().getReference("userprofile")
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setToolbarProperties()
        setContentView(binding.root)
        if(intent.hasExtra(ConstantAuth.CONSTANT_AUTH_KEY)){
            authKey = intent.getStringExtra(ConstantAuth.CONSTANT_AUTH_KEY) as String
        }
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        sharedPrefUtil = SharedPrefUtil()
        sharedPrefUtil.start(this,ConstantAuth.CONSTANT_PREFERENCE)
        binding.editTextBirthDate.inputType = InputType.TYPE_NULL
        binding.editTextBirthDate.setOnClickListener {
            val dpd = DatePickerDialog(this, { _, year, month, dayOfMonth ->
                val monthStr = if (month.toString().length > 1){
                    month.toString()
                }else{
                    "0${(month+1)}"
                }
                val dayStr = if(dayOfMonth.toString().length > 1){
                    dayOfMonth.toString()
                }else{
                    "0$dayOfMonth"
                }
                binding.editTextBirthDate.setText("$dayStr-$monthStr-$year")
            }, year, month, day)
            dpd.show()
        }
        binding.buttonChangePassword.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    ChangePasswordActivity::class.java
                ).putExtra(ConstantAuth.CONSTANT_AUTH_KEY, authKey)
            )
        }
        getUserData()
        viewModel.setUpEditProfile()
        getStateEditProfile()
        binding.imageViewUser.setOnClickListener {
            GlobalHelper.startImagePickerFromActvitty(this)
        }
    }

    private fun getUserData(){
        viewModel.getProfileUser(authKey).observe(this,{
            if (it.photoProfile.isEmpty() || it.photoProfile == "null") {
                Glide.with(this).load(R.drawable.ic_profile)
                    .into(binding.imageViewUser)
            } else {
                Glide.with(this).load(it.photoProfile).circleCrop()
                    .into(binding.imageViewUser)
            }
            binding.editTextUsername.setText(it.username)
            binding.editTextFullname.setText(it.fullName)
            binding.editTextEmail.setText(it.email)
            binding.editTextBirthDate.setText(it.birthDate)
        })
    }

    private fun editProfile(){
        val username = binding.editTextUsername.text.toString()
        val fullname = binding.editTextFullname.text.toString()
        val email = binding.editTextEmail.text.toString()
        val dateOfBirth = binding.editTextBirthDate.text.toString()
        var sendImage: String
        when {
            username.isEmpty() -> {
                binding.editTextUsername.apply {
                    error = "Username can not be empty"
                    requestFocus()
                }
            }
            username.contains(" ") -> {
                binding.editTextUsername.apply {
                    error = "Username can not contain whitespace"
                    requestFocus()
                }
            }
            username.length < 6 -> {
                binding.editTextUsername.apply {
                    error = "Username must be longer than 6 characters"
                    requestFocus()
                }
            }
            username.length > 16 -> {
                binding.editTextUsername.apply {
                    error = "Username characters up to 16 characters"
                    requestFocus()
                }
            }
            fullname.isEmpty() -> {
                binding.editTextFullname.apply {
                    error = "Fullname can not be empty"
                    requestFocus()
                }
            }
            email.isEmpty() -> {
                binding.editTextEmail.apply {
                    error = "Email can not be empty"
                    requestFocus()
                }
            }
            !Patterns.EMAIL_ADDRESS.matcher(binding.editTextEmail.text.toString()).matches() -> {
                binding.editTextEmail.apply {
                    error = "Invalid Email Address"
                    requestFocus()
                }
            }
            username != username.toLowerCase(Locale.ROOT) -> {
                binding.editTextEmail.apply {
                    error = "Username can not contain capital letters"
                    requestFocus()
                }
            }
            else -> {
                Log.d("hasil_crop", imageCroppedResult.toString())
                if(imageCroppedResult != null){
                    val reference = storageUserProfileRef.child("${imageCroppedResult!!.lastPathSegment}")
                    reference.putFile(imageCroppedResult!!)
                        .addOnSuccessListener {
                            reference.downloadUrl.addOnSuccessListener {
                                Log.d("image_uri", it.toString())
                                sendImage = it.toString()
                                val user = User(photoProfile = sendImage, fullName = fullname, username = username, email = email,
                                birthDate = dateOfBirth, updatedAt = Date())
                                viewModel.editProfile(authKey, user, this)
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                            Log.d("err_upload_image", it.localizedMessage!!.toString())
                        }
                        .addOnProgressListener {

                        }
                }else{
                    sendImage = sharedPrefUtil.get(ConstantAuth.CONSTANT_AUTH_IMAGE)!!
                    val user = User(photoProfile = sendImage, fullName = fullname, username = username, email = email,
                        birthDate = dateOfBirth, updatedAt = Date())
                    viewModel.editProfile(authKey, user, this)
                }
            }
        }
    }

    private fun getStateEditProfile(){
        viewModel.editProfileState.observe(this,{
            when(it){
                NetworkState.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                NetworkState.SUCCESS -> {
                    finish()
                    binding.progressBar.visibility = View.GONE
                }
                NetworkState.FAILED -> {
                    Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                }
                NetworkState.NOT_FOUND -> {
                    binding.progressBar.visibility = View.GONE
                }
                else ->{
                    Toast.makeText(this, "Failed to update profile, something went wrong", Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.submit_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setToolbarProperties() {
        binding.toolbar.setOnMenuItemClickListener {
            onOptionsItemSelected(it)
        }
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
        }else if(item.itemId == R.id.submit_menu){
            editProfile()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            image = ImagePicker.getFirstImageOrNull(data)
            val selectedBitmap: Bitmap = ConverterHelper.getBitmap(this, image!!.uri)!!
            val selectedImgFile = File(
                getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                GlobalHelper.getRandomString(20) + ".jpg"
            )
            ConverterHelper.convertBitmaptoFile(selectedImgFile, selectedBitmap)
            val croppedImgFile = File(
                getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                GlobalHelper.getRandomString(20) + ".jpg"
            )
            imageCroppedResult = Uri.fromFile(croppedImgFile)
            UcropHelper.openCropActivity(Uri.fromFile(selectedImgFile), Uri.fromFile(croppedImgFile), this)
        }
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            if (resultUri != null) {
                try {
                    Glide.with(this).load(resultUri).circleCrop().into(binding.imageViewUser)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}