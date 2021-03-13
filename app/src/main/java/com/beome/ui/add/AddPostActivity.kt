package com.beome.ui.add

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.beome.R
import com.beome.databinding.ActivityAddPostBinding
import com.beome.utilities.GlobalHelper
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.component_feedback.view.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class AddPostActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAddPostBinding
    private var image : Image? = null
    private var imageCroppedResult : Uri? = null
    private val storageRef = FirebaseStorage.getInstance().getReference("imagepost")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(image == null){
            binding.imageViewAddImage.visibility = View.VISIBLE
            binding.textViewAddImage.visibility = View.VISIBLE
            binding.textViewChangeImage.visibility = View.GONE
        }else{
            binding.imageViewAddImage.visibility = View.GONE
            binding.textViewAddImage.visibility = View.GONE
            binding.textViewChangeImage.visibility = View.VISIBLE
        }
        //init feedback field
        addFeedbackField()

        binding.imageViewAddImage.setOnClickListener {
            GlobalHelper.startImagePickerFromActvitty(this)
        }
        binding.buttonPublish.setOnClickListener {
            if(image == null){
                Toast.makeText(this, "Image is not added", Toast.LENGTH_SHORT).show()
            }
            if(imageCroppedResult != null){
                val reference = storageRef.child("${imageCroppedResult!!.lastPathSegment}")


                reference.putFile(imageCroppedResult!!)
                    .addOnSuccessListener {
                        binding.imageProgress.progress = 0
                        Toast.makeText(this, "Success to upload image", Toast.LENGTH_SHORT).show()
                      reference.downloadUrl.addOnSuccessListener {
                          val downloadUri = it
                          Log.d("image_url", downloadUri.toString())
                          binding.imageProgress.visibility = View.GONE
                      }
                        binding.buttonPublish.isEnabled = true
                    }
                    .addOnFailureListener{
                        Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                        Log.d("err_upload_image", it.localizedMessage!!.toString())
                    }
                    .addOnProgressListener {
                        binding.buttonPublish.isEnabled = false
                        binding.imageProgress.visibility = View.VISIBLE
                        binding.buttonPublish.text = ""
                        val progress: Double = (100.0 * it.bytesTransferred) / it.totalByteCount
                        binding.imageProgress.progress = progress.toInt()
                    }
            }
            getDataFeedbackField()
        }
        binding.textViewChangeImage.setOnClickListener {
            GlobalHelper.startImagePickerFromActvitty(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(data == null){
            binding.imageViewAddImage.visibility = View.VISIBLE
            binding.textViewAddImage.visibility = View.VISIBLE
            binding.textViewChangeImage.visibility = View.GONE
        }else{
            binding.imageViewAddImage.visibility = View.GONE
            binding.textViewAddImage.visibility = View.GONE
            binding.textViewChangeImage.visibility = View.VISIBLE
            if(ImagePicker.shouldHandle(requestCode, resultCode, data)){
                image = ImagePicker.getFirstImageOrNull(data)
                val selectedBitmap: Bitmap = getBitmap(this, image!!.uri)!!
                val selectedImgFile = File(
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    GlobalHelper.getRandomString(20) + ".jpg"
                )
                convertBitmaptoFile(selectedImgFile, selectedBitmap)
                val croppedImgFile = File(
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    GlobalHelper.getRandomString(20) + ".jpg"
                )
                imageCroppedResult = Uri.fromFile(croppedImgFile)
                openCropActivity(Uri.fromFile(selectedImgFile), Uri.fromFile(croppedImgFile))

            }
            if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
                val resultUri =  UCrop.getOutput(data)
                if(resultUri != null){
                    try {
                        Glide.with(this).load(resultUri).into(binding.imagePost)
                    } catch (e: Exception) { e.printStackTrace()}
                }
            }
        }


        super.onActivityResult(requestCode, resultCode, data)
    }

    @SuppressLint("InflateParams")
    private fun addFeedbackField(){
        if(binding.feedbackComponent.childCount < 5){
            val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val rowView: View = inflater.inflate(R.layout.component_feedback, null)
            binding.feedbackComponent.addView(rowView)
        }else{
            Toast.makeText(this, "Feedback components is full", Toast.LENGTH_SHORT).show()
        }

    }

    private fun getDataFeedbackField(){
        val feedbackCount = binding.feedbackComponent.childCount
        if(feedbackCount == 1){
            val row: View = binding.feedbackComponent.getChildAt(0)
            if(row.editTextFeedbackComponent.text.isEmpty()){
                Toast.makeText(this, "Feedback name is still empty", Toast.LENGTH_SHORT).show()
            }
        }
        for (i in 0 until feedbackCount){
            val row: View = binding.feedbackComponent.getChildAt(i)
            if(row.editTextFeedbackComponent.text.isNotEmpty()){
                Log.d("data_feedback", row.editTextFeedbackComponent.text.toString())
            }
        }
    }

    fun onAddFieldFeedback(v: View) {
        addFeedbackField()
    }

    fun onDeleteFieldFeedback(v: View) {
        val feedbackCount = binding.feedbackComponent.childCount
        if(feedbackCount >= 2){
            binding.feedbackComponent.removeView(v.parent as View)
        }else{
            Toast.makeText(this, "You should add at least 1 feedback", Toast.LENGTH_SHORT).show()
        }

    }

    private fun getBitmap(context: Context, imageUri: Uri): Bitmap? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(
                    context.contentResolver,
                    imageUri
                )
            )
        } else {
            context.contentResolver.openInputStream(imageUri) ?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        }
    }

    private fun convertBitmaptoFile(destinationFile: File, bitmap: Bitmap) {
        //create a file to write bitmap data
        destinationFile.createNewFile()   //Convert bitmap to byte array
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos)
        val bitmapData = bos.toByteArray()   //write the bytes in file
        val fos = FileOutputStream(destinationFile)
        fos.write(bitmapData)
        fos.flush()
        fos.close()
    }

    private fun openCropActivity(sourceUri: Uri, destinationUri: Uri) {
        val options = UCrop.Options()
        options.setHideBottomControls(true)
        UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withOptions(options)
            .start(this)
    }
}