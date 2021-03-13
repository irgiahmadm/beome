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
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.beome.R
import com.beome.databinding.ActivityAddPostBinding
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
import com.yalantis.ucrop.UCrop
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*

class AddPostActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAddPostBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*ImagePicker.create(this)
            .single()
            .showCamera(false)
            .start()*/
       /*
       val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.bottomMargin = 8
        val initialEditText = EditText(this)
        binding.feedbackComponent.addView(initialEditText)
        listFeedbackComponent.add(initialEditText)
        binding.buttonAddFeedback.setOnClickListener {
            if(listFeedbackComponent.size < 5){
                val newEditText = EditText(this)
                binding.feedbackComponent.addView(newEditText)
                listFeedbackComponent.add(newEditText)
            }else{
                Toast.makeText(this, "Component is full", Toast.LENGTH_SHORT).show()
            }

        }*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(ImagePicker.shouldHandle(requestCode, resultCode, data)){
            val image : Image = ImagePicker.getFirstImageOrNull(data)
            val selectedBitmap: Bitmap = getBitmap(this, image.uri)!!
            val selectedImgFile = File(
                getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                Date().toString() + "_selectedImg.jpg"
            )
            convertBitmaptoFile(selectedImgFile, selectedBitmap)
            val croppedImgFile = File(
                getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                Date().toString() + "_croppedImg.jpg"
            )
            openCropActivity(Uri.fromFile(selectedImgFile), Uri.fromFile(croppedImgFile))
        }
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            try {
                Glide.with(this).load(resultUri).into(binding.imagePost)
            } catch (e: Exception) { e.printStackTrace()}
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    @SuppressLint("InflateParams")
    private fun addFeedbackField(){
        if(binding.feedbackComponent.childCount < 5){
            val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val rowView: View = inflater.inflate(R.layout.component_feedback, null)
            binding.feedbackComponent.addView(rowView, binding.feedbackComponent.childCount - 1)
        }else{
            Toast.makeText(this, "Feedback components is full", Toast.LENGTH_SHORT).show()
        }

    }

    fun onAddFieldFeedback(v: View) {
        addFeedbackField()
    }

    fun onDeleteFieldFeedback(v: View) {
        binding.feedbackComponent.removeView(v.parent as View)
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