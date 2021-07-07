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
import androidx.recyclerview.widget.LinearLayoutManager
import com.beome.MainActivity
import com.beome.R
import com.beome.constant.ConstantAuth
import com.beome.databinding.ActivityAddPostBinding
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
import kotlinx.android.synthetic.main.item_suggested_feedback.view.*
import kotlinx.android.synthetic.main.item_tags.view.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class AddPostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddPostBinding
    private lateinit var sharedPrefUtil: SharedPrefUtil

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
    private lateinit var adapterSuggestedFeedback : AdapterUtil<String>
    private lateinit var adapterTags : AdapterUtil<String>
    private val storageRef = FirebaseStorage.getInstance().getReference("imagepost")
    private var arrayListCategoryFeedback : ArrayList<String> = arrayListOf()
    private var arrayListComponentFeedback : ArrayList<String> = arrayListOf()
    private var arrayListTagPost : ArrayList<String> = arrayListOf()

    private var arrayListSearchKeyword : ArrayList<String> = arrayListOf()

    private var arrayListSuggestedFeedback : ArrayList<String> = arrayListOf()
    private var tempListSuggestedFeedback : ArrayList<String> = arrayListOf()
    private var tempListCustomFeedback : ArrayList<String> = arrayListOf()
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
            viewModel.setUpAddPost()
            getAddPostState()
            handleCheckBox()
            handleTagPost()
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

    private fun handleTagPost(){
        binding.buttonAddTag.setOnClickListener {
            val editTextTag = binding.editTextTag
            if(arrayListTagPost.size < 5){
                if(editTextTag.text.toString().isEmpty()){
                    editTextTag.apply {
                        error = "Tag can not be empty"
                        requestFocus()
                    }
                }else{
                    if(arrayListTagPost.size > 0){
                        if(arrayListTagPost.size == arrayListTagPost.distinct().count()){
                            Toast.makeText(this, "There is same tag", Toast.LENGTH_SHORT).show()
                        }else{
                            arrayListTagPost.add(editTextTag.text.toString())
                            adapterTags.refresh()
                        }
                    }else{
                        arrayListTagPost.add(editTextTag.text.toString())
                        adapterTags.refresh()
                    }
                }
            }else{
                Toast.makeText(this, "Tags is full", Toast.LENGTH_SHORT).show()
            }
        }
        setTagList(arrayListTagPost)
    }

    private fun setTagList(lisTagsPost : ArrayList<String>){
        adapterTags = AdapterUtil(R.layout.item_tags, lisTagsPost, {_, view, item ->
            view.textViewTag.text = item
            view.ivDeleteTag.setOnClickListener {
                arrayListTagPost.remove(item)
                adapterTags.refresh()
            }
        },{pos, item ->

        })
        binding.rvTags.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvTags.adapter = adapterTags
    }

    private fun handleCheckBox(){
        val listGraphicDesign = arrayListOf("Symmetry and Balance", "Golden Ratio", "Rule of Third")
        binding.cbGraphicDesign.setOnCheckedChangeListener { button, isChecked ->
            if(isChecked){
                arrayListSuggestedFeedback.addAll(listGraphicDesign)
                adapterSuggestedFeedback.refresh()
                arrayListCategoryFeedback.add(button.text.toString())
            }else{
                arrayListSuggestedFeedback.removeAll(listGraphicDesign)
                adapterSuggestedFeedback.refresh()
                arrayListCategoryFeedback.remove(button.text.toString())
                handleUncheckCategory(listGraphicDesign, tempListSuggestedFeedback, arrayListComponentFeedback)
            }
        }
        val listIllustration = arrayListOf("Value", "Pattern", "Style")
        binding.cbIllustration.setOnCheckedChangeListener { button, isChecked ->
            if(isChecked){
                arrayListSuggestedFeedback.addAll(listIllustration)
                adapterSuggestedFeedback.refresh()
                arrayListCategoryFeedback.add(button.text.toString())
            }else{
                arrayListSuggestedFeedback.removeAll(listIllustration)
                adapterSuggestedFeedback.refresh()
                arrayListCategoryFeedback.remove(button.text.toString())
                handleUncheckCategory(listIllustration, tempListSuggestedFeedback, arrayListComponentFeedback)
            }
        }
        val listUIUX = arrayListOf("Intuitive", "Usability", "User Friendly")
        binding.cbUIUX.setOnCheckedChangeListener { button, isChecked ->
            if(isChecked){
                arrayListSuggestedFeedback.addAll(listUIUX)
                adapterSuggestedFeedback.refresh()
                arrayListCategoryFeedback.add(button.text.toString())
            }else{
                arrayListSuggestedFeedback.removeAll(listUIUX)
                adapterSuggestedFeedback.refresh()
                arrayListCategoryFeedback.remove(button.text.toString())
                handleUncheckCategory(listUIUX, tempListSuggestedFeedback, arrayListComponentFeedback)
            }
        }
        setListSuggestedFeedback(arrayListSuggestedFeedback)
    }

    private fun handleUncheckCategory(listCategory : ArrayList<String>, listTempFeedback : ArrayList<String>, listFeedbackComponent : ArrayList<String>){
        if(listTempFeedback.size > 0){
            for (i in listTempFeedback.indices){
                for (j in listCategory.indices){
                    Log.d("DATA_POST_TEMP", "$listTempFeedback - $listCategory")
                    if(listTempFeedback.size > 0){
                        if(listTempFeedback[i] == listCategory[j]){
                            listTempFeedback.remove(listTempFeedback[i])
                        }
                    }
                }
            }
        }
        if(listFeedbackComponent.size > 0){
            for (i in listFeedbackComponent.indices){
                for (j in listCategory.indices){
                    Log.d("DATA_POST_COMP", "$listFeedbackComponent - $listCategory")
                    if(listFeedbackComponent.size > 0){
                        if(listFeedbackComponent[i] == listCategory[j]){
                            listFeedbackComponent.remove(listFeedbackComponent[i])
                        }
                    }
                }
            }
        }
    }

    private fun setListSuggestedFeedback(listSuggestedFeedback : ArrayList<String>){
        adapterSuggestedFeedback = AdapterUtil(R.layout.item_suggested_feedback, listSuggestedFeedback, {pos,view,item->
            view.checkBox.text = item
            view.checkBox.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked){
                    tempListSuggestedFeedback.add(item)
                }else{
                    tempListSuggestedFeedback.remove(item)
                }
            }
        },{pos,item ->

        })
        binding.rvSuggestedFeedback.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvSuggestedFeedback.adapter = adapterSuggestedFeedback
    }

    private fun hideShowViewNotLogedIn(visibility: Int) {
        binding.textViewChangeImage.visibility = visibility
        binding.imageViewAddImage.visibility = visibility
        binding.imagePost.visibility = visibility
        binding.textViewLabelTitle.visibility = visibility
        binding.textViewLabelDescription.visibility = visibility
        binding.textViewLabelFeedback.visibility = visibility
        binding.editTextPostDesc.visibility = visibility
        binding.editTextPostTitle.visibility = visibility
        binding.constraintLayoutPublish.visibility = visibility
        binding.buttonAddFeedback.visibility = visibility
    }

    @SuppressLint("SimpleDateFormat", "UseCompatLoadingForDrawables", "SetTextI18n")
    private fun publishPost() {
        val feedbackCounter = binding.feedbackComponent.childCount
        val viewCustomFeedback1 = binding.feedbackComponent.getChildAt(0)
        val viewCustomFeedback2 = binding.feedbackComponent.getChildAt(1)
        //clear list custom feedback if click publish again
        tempListCustomFeedback.clear()

        if(arrayListCategoryFeedback.isEmpty()){
            //check if there is any category checked
            Toast.makeText(this, "Please add 1 suggested feedback", Toast.LENGTH_LONG).show()
        }
        else if(tempListSuggestedFeedback.isEmpty()){
            //check if there is any suggested feedback that checked
            Toast.makeText(this, "Please add 1 suggested feedback", Toast.LENGTH_LONG).show()
        }else if(feedbackCounter > 0){
            for (i in 0 until feedbackCounter) {
                val newRow: View = binding.feedbackComponent.getChildAt(i)
                if(newRow.editTextFeedbackComponent.text.toString().length > 25){
                    newRow.editTextFeedbackComponent.apply {
                        error = "Character can not more than 25"
                        requestFocus()
                    }
                }else{
                    if (newRow.editTextFeedbackComponent.text.isNotEmpty()) {
                        tempListCustomFeedback.add(
                            newRow.editTextFeedbackComponent.text.toString()
                                .toLowerCase(Locale.getDefault())
                        )
                    }
                }
            }
        }else if(feedbackCounter > 1) {
            val customFeedback1 = viewCustomFeedback1.editTextFeedbackComponent.text.toString()
            val customFeedback2 = viewCustomFeedback2.editTextFeedbackComponent.text.toString()
            //check if there is any same component on custom feedback
            isFeedBackComponentValid = customFeedback1 == customFeedback2
            messageErrComponent = "There is same feedback, please change"
        }else{
            isFeedBackComponentValid = true
            if (isFeedBackComponentValid) {
                idPost = GlobalHelper.getRandomString(20)
                val title = binding.editTextPostTitle.text.toString().toLowerCase(Locale.getDefault())
                val desc = binding.editTextPostDesc.text.toString()
                val username = sharedPrefUtil.get(ConstantAuth.CONSTANT_AUTH_USERNAME)!!
                val imageUser = sharedPrefUtil.get(ConstantAuth.CONSTANT_AUTH_IMAGE)!!
                when {
                    image == null -> {
                        Toast.makeText(this, "Image is not added", Toast.LENGTH_SHORT).show()
                        hideProgressBar()
                    }
                    title.isEmpty() -> {
                        binding.editTextPostTitle.apply {
                            error = "Title can not be empty"
                            requestFocus()
                        }
                        hideProgressBar()
                    }
                    title.length > 32 -> {
                        binding.editTextPostTitle.apply {
                            error = "Title characters up to 32 characters"
                            requestFocus()
                        }
                        hideProgressBar()
                    }
                    desc.isEmpty() -> {
                        binding.editTextPostDesc.apply {
                            error = "Description can not be empty"
                            requestFocus()
                        }
                        hideProgressBar()
                    }
                    desc.length > 500 -> {
                        binding.editTextPostDesc.apply {
                            error = "Description characters up to 500 characters"
                            requestFocus()
                        }
                        hideProgressBar()
                    }
                    else -> {
                        if (imageCroppedResult != null) {
                            binding.buttonPublish.isEnabled = false
                            binding.progressBar.visibility = View.VISIBLE
                            binding.buttonPublish.text = ""
                            //add list feedback component
                            arrayListComponentFeedback.addAll(tempListSuggestedFeedback)
                            arrayListComponentFeedback.addAll(tempListCustomFeedback)
                            //ad list searchKeyword
                            arrayListSearchKeyword.addAll(arrayListComponentFeedback)
                            arrayListSearchKeyword.addAll(arrayListCategoryFeedback)
                            arrayListSearchKeyword.addAll(arrayListTagPost)
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
                                            GlobalHelper.listToLowerCase(arrayListComponentFeedback),
                                            GlobalHelper.listToLowerCase(arrayListTagPost),
                                            GlobalHelper.listToLowerCase(arrayListSearchKeyword),
                                            1,
                                            Date(),
                                            Date()
                                        )
                                        Log.d("DATA_POST, ", post.toString())
                                        //viewModel.addPost(post)
                                    }
                                    binding.buttonPublish.isEnabled = true
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT)
                                        .show()
                                    Log.d("err_upload_image", it.localizedMessage!!.toString())
                                    binding.buttonPublish.isEnabled = true
                                    binding.progressBar.visibility = View.GONE
                                    binding.buttonPublish.text = "PUBLISH"
                                }
                                .addOnProgressListener {
                                    binding.buttonPublish.isEnabled = false
                                    binding.progressBar.visibility = View.VISIBLE
                                    binding.buttonPublish.text = ""
                                    /*val progress: Double = (100.0 * it.bytesTransferred) / it.totalByteCount
                                binding.imageProgress.progress = progress.toInt()*/
                                }
                        } else {
                            Toast.makeText(this, "Image is not added", Toast.LENGTH_SHORT).show()
                            hideProgressBar()
                        }
                    }
                }
            } else {
                Toast.makeText(
                    this,
                    messageErrComponent,
                    Toast.LENGTH_SHORT
                ).show()
                hideProgressBar()
            }
        }

    }

    private fun getAddPostState() {
        viewModel.addPostState.observe(this, {
            when (it) {
                NetworkState.LOADING -> {

                }
                NetworkState.SUCCESS -> {
                    /*viewModel.setUpComponentFeedback()
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
                    }*/
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
        if (binding.feedbackComponent.childCount < 2 && tempListSuggestedFeedback.size < 5 && arrayListComponentFeedback.size < 5) {
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
        binding.feedbackComponent.removeView(v.parent as View)
    }

    @SuppressLint("SetTextI18n")
    private fun hideProgressBar(){
        binding.progressBar.visibility = View.GONE
        binding.buttonPublish.text = "PUBLISH"
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