package com.beome.ui.add

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.beome.MainActivity
import com.beome.R
import com.beome.constant.ConstantAuth
import com.beome.databinding.ActivityAddPostBinding
import com.beome.model.ComponentFeedbackPost
import com.beome.model.Post
import com.beome.ui.authentication.login.LoginActivity
import com.beome.utilities.*
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.component_feedback.view.*
import java.io.File
import java.util.*

class AddPostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddPostBinding
    private lateinit var sharedPrefUtil: SharedPrefUtil
    private lateinit var listOfFeedback: ArrayList<String>
    private lateinit var idPost: String
    private lateinit var messageErrComponent : String
    private var isFeedBackComponentValid = false
    private val viewModel: AddPostViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(AddPostViewModel::class.java)
    }
    private var image: Image? = null
    private var imageCroppedResult: Uri? = null
    private lateinit var authKey: String
    private val storageRef = FirebaseStorage.getInstance().getReference("imagepost")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPrefUtil = SharedPrefUtil()
        sharedPrefUtil.start(this, ConstantAuth.CONSTANT_PREFERENCE)

        //check auth is not empty
        if (sharedPrefUtil.get(ConstantAuth.CONSTANT_AUTH_KEY).isNullOrEmpty()) {
            startActivity(Intent(this, LoginActivity::class.java))
            hideShowViewNotLogedIn(View.GONE)
            binding.groupNotSignedIn.visibility = View.VISIBLE
            binding.buttonSignin.setOnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        } else {
            setToolbarProperties()
            hideShowViewNotLogedIn(View.VISIBLE)
            binding.groupNotSignedIn.visibility = View.GONE
            authKey = sharedPrefUtil.get(ConstantAuth.CONSTANT_AUTH_KEY)!!
            if (image == null) {
                binding.imageViewAddImage.visibility = View.VISIBLE
                binding.textViewChangeImage.visibility = View.GONE
            } else {
                binding.imageViewAddImage.visibility = View.GONE
                binding.textViewChangeImage.visibility = View.VISIBLE
            }
            //init feedback field
            addFeedbackField()
            viewModel.setUpAddPost()
            getAddPostState()
            binding.imageViewAddImage.setOnClickListener {
                GlobalHelper.startImagePickerFromActvitty(this)
            }
            binding.buttonPublish.setOnClickListener {
                publishPost()
            }
            binding.textViewChangeImage.setOnClickListener {
                GlobalHelper.startImagePickerFromActvitty(this)
            }
        }
    }

    private fun hideShowViewNotLogedIn(visibility: Int) {
        binding.textViewChangeImage.visibility = visibility
        binding.imageViewAddImage.visibility = visibility
        binding.imagePost.visibility = visibility
        binding.textViewLabelTitle.visibility = visibility
        binding.textViewLabelDescription.visibility = visibility
        binding.textViewLabelFeedback.visibility = visibility
        binding.textViewNoteFeedback.visibility = visibility
        binding.editTextPostDesc.visibility = visibility
        binding.editTextPostTitle.visibility = visibility
        binding.constraintLayoutPublish.visibility = visibility
        binding.buttonAddFeedback.visibility = visibility
    }

    @SuppressLint("SimpleDateFormat", "UseCompatLoadingForDrawables", "SetTextI18n")
    private fun publishPost() {
        binding.buttonPublish.isEnabled = false
        binding.progressBar.visibility = View.VISIBLE
        binding.buttonPublish.text = ""
        val feedbackCounter = binding.feedbackComponent.childCount
        val rowFirst: View = binding.feedbackComponent.getChildAt(0)
        listOfFeedback = arrayListOf()
        listOfFeedback.clear()
        for (i in 0 until feedbackCounter) {
            val newRow: View = binding.feedbackComponent.getChildAt(i)
            if(newRow.editTextFeedbackComponent.text.toString().length > 25){
                newRow.editTextFeedbackComponent.apply {
                    error = "Character can not more than 25"
                    requestFocus()
                }
            }else{
                if (newRow.editTextFeedbackComponent.text.isNotEmpty()) {
                    listOfFeedback.add(
                        newRow.editTextFeedbackComponent.text.toString()
                            .toLowerCase(Locale.getDefault())
                    )
                }
            }
        }
        Log.d("list of feedback", listOfFeedback.toString())
        if (feedbackCounter != 1) {
            isFeedBackComponentValid = listOfFeedback.size == listOfFeedback.distinct().count()
            messageErrComponent = "There is same feedback, please change"
        }
        if (feedbackCounter == 1 && rowFirst.editTextFeedbackComponent.text.toString().isEmpty()) {
            rowFirst.editTextFeedbackComponent.apply {
                error = "Feedback component can not be empty"
                requestFocus()
            }
            messageErrComponent = "Feedback component can not be empty"
        }
        if(rowFirst.editTextFeedbackComponent.text.toString().length > 25){
            rowFirst.editTextFeedbackComponent.apply {
                error = "Character can not more than 25"
                requestFocus()
            }
        }else{
            isFeedBackComponentValid = true
        }
        if (isFeedBackComponentValid) {
            idPost = GlobalHelper.getRandomString(20)
            val title = binding.editTextPostTitle.text.toString().toLowerCase(Locale.getDefault())
            val desc = binding.editTextPostDesc.text.toString()
            val username = sharedPrefUtil.get(ConstantAuth.CONSTANT_AUTH_USERNAME)!!
            val imageUser = sharedPrefUtil.get(ConstantAuth.CONSTANT_AUTH_IMAGE)!!
            if (image == null) {
                Toast.makeText(this, "Image is not added", Toast.LENGTH_SHORT).show()
            }
            if (title.isEmpty()) {
                binding.editTextPostTitle.apply {
                    error = "Title can not be empty"
                    requestFocus()
                }
            }
            if(title.length > 32){
                binding.editTextPostTitle.apply {
                    error = "Title characters up to 32 characters"
                    requestFocus()
                }
            }
            if (desc.isEmpty()) {
                binding.editTextPostDesc.apply {
                    error = "Description can not be empty"
                    requestFocus()
                }
            }
            if(desc.length > 120){
                binding.editTextPostDesc.apply {
                    error = "Description characters up to 120 characters"
                    requestFocus()
                }
            }
            if (imageCroppedResult != null) {
                val reference = storageRef.child("${imageCroppedResult!!.lastPathSegment}")
                reference.putFile(imageCroppedResult!!)
                    .addOnSuccessListener {
                        reference.downloadUrl.addOnSuccessListener {
                            val downloadUri = it.toString()
                            val likedBy = arrayListOf<String>()
                            val post = Post(
                                idPost,
                                authKey,
                                username,
                                imageUser,
                                downloadUri,
                                title,
                                desc,
                                0,
                                0,
                                likedBy,
                                1,
                                Date(),
                                Date()
                            )
                            viewModel.addPost(post)
                        }
                        binding.buttonPublish.isEnabled = true
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                        Log.d("err_upload_image", it.localizedMessage!!.toString())
                        binding.buttonPublish.isEnabled = true
                        binding.progressBar.visibility = View.INVISIBLE
                        binding.buttonPublish.text = "PUBLISH"
                    }
                    .addOnProgressListener {
                        binding.buttonPublish.isEnabled = false
                        binding.progressBar.visibility = View.VISIBLE
                        binding.buttonPublish.text = ""
                        /*val progress: Double = (100.0 * it.bytesTransferred) / it.totalByteCount
                        binding.imageProgress.progress = progress.toInt()*/
                    }
            }else{
                Toast.makeText(this, "Image is not added", Toast.LENGTH_SHORT).show()
            }

        } else {
            Toast.makeText(
                this,
                messageErrComponent,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getAddPostState() {
        viewModel.addPostState.observe(this, {
            when (it) {
                NetworkState.LOADING -> {

                }
                NetworkState.SUCCESS -> {
                    viewModel.setUpComponentFeedback()
                    getComponentFeedbackState()
                    var counter = 0
                    (0 until listOfFeedback.size).forEach { i ->
                        counter++
                        val randomString = GlobalHelper.getRandomString(10)
                        val componentFeedbackPost =
                            ComponentFeedbackPost(randomString, idPost, listOfFeedback[i])
                        viewModel.addComponentFeedbackPost(
                            componentFeedbackPost,
                            listOfFeedback.size,
                            counter
                        )
                    }
                }
                NetworkState.FAILED -> {
                    Toast.makeText(this, "Add post failed", Toast.LENGTH_SHORT).show()
                }
                NetworkState.NOT_FOUND -> {

                }
                else -> {
                    Toast.makeText(
                        this,
                        "Add post failed, something went wrong",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    private fun getComponentFeedbackState() {
        viewModel.addComponentState.observe(this, {
            when (it) {
                NetworkState.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                NetworkState.SUCCESS -> {
                    binding.buttonPublish.text = ""
                    binding.progressBar.visibility = View.GONE
                    Snackbar.make(
                        this,
                        binding.constraintLayoutAddPost,
                        "Your artwork is published",
                        Snackbar.LENGTH_SHORT
                    )
                    startActivity(
                        Intent(
                            this,
                            MainActivity::class.java
                        ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                }
                NetworkState.FAILED -> {
                    Toast.makeText(this, "Add component failed", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(
                        this,
                        "Add component failed, something went wrong",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data == null) {
            binding.imageViewAddImage.visibility = View.VISIBLE
            binding.textViewChangeImage.visibility = View.GONE
        } else {
            binding.imageViewAddImage.visibility = View.GONE
            binding.textViewChangeImage.visibility = View.VISIBLE
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
                val resultUri = UCrop.getOutput(data)
                if (resultUri != null) {
                    try {
                        Glide.with(this).load(resultUri).into(binding.imagePost)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    @SuppressLint("InflateParams")
    private fun addFeedbackField() {
        if (binding.feedbackComponent.childCount < 5) {
            val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val rowView: View = inflater.inflate(R.layout.component_feedback, null)
            binding.feedbackComponent.addView(rowView)
        } else {
            Toast.makeText(this, "Feedback components is full", Toast.LENGTH_SHORT).show()
        }

    }

    fun onAddFieldFeedback(v: View) {
        addFeedbackField()
    }

    fun onDeleteFieldFeedback(v: View) {
        val feedbackCount = binding.feedbackComponent.childCount
        if (feedbackCount >= 2) {
            binding.feedbackComponent.removeView(v.parent as View)
        } else {
            Toast.makeText(this, "You should add at least 1 feedback", Toast.LENGTH_SHORT).show()
        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setToolbarProperties() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        startActivity(
            Intent(
                this,
                MainActivity::class.java
            ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
        finish()
        super.onBackPressed()
    }
}