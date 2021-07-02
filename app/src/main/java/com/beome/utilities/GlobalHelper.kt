package com.beome.utilities

import android.app.Activity
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.EditText
import android.widget.ImageView
import androidx.transition.Fade
import androidx.transition.TransitionManager
import com.esafirm.imagepicker.features.ImagePicker
import java.security.MessageDigest

object GlobalHelper {
    fun getRandomString(length: Int) : String {
        val allowedChars =  ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    fun sha256(base: String): String {
        return try {
            val digest: MessageDigest = MessageDigest.getInstance("SHA-256")
            val hash: ByteArray = digest.digest(base.toByteArray(charset("UTF-8")))
            val hexString = StringBuffer()
            for (i in hash.indices) {
                val hex = Integer.toHexString(0xff and hash[i].toInt())
                if (hex.length == 1) hexString.append('0')
                hexString.append(hex)
            }
            hexString.toString()
        } catch (ex: Exception) {
            throw RuntimeException(ex)
        }
    }

    fun startImagePickerFromActvitty(activity : Activity){
        ImagePicker.create(activity)
            .single()
            .showCamera(false)
            .start()
    }


    fun hideShowPassword(editTextPassword : EditText, togglePassword : ImageView){
        var isPasswordVisible = false
        togglePassword.setOnClickListener {
            if(!isPasswordVisible){
                editTextPassword.transformationMethod = HideReturnsTransformationMethod()
                togglePassword.setImageResource(com.beome.R.drawable.ic_password_hide)
                isPasswordVisible = true
            }else{
                editTextPassword.transformationMethod = PasswordTransformationMethod()
                togglePassword.setImageResource(com.beome.R.drawable.ic_password_visible)
                isPasswordVisible = false
            }

        }
    }

    fun slideHideAndShowAnimation(visible : Boolean, imageView : View, view : View, viewGroup : ViewGroup){
        val transition = Fade(Fade.IN)
        transition.duration = 250
        transition.addTarget(view)
        TransitionManager.beginDelayedTransition(viewGroup, transition)
        if(visible){
            view.visibility = View.VISIBLE
            val rotate = RotateAnimation(
                0f,
                180f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )
            rotate.duration = 300
            rotate.interpolator = LinearInterpolator()
            rotate.fillAfter = true
            imageView.startAnimation(rotate)
        }else{
            view.visibility = View.GONE
            val rotate = RotateAnimation(
                180f,
                0f,
                Animation.RELATIVE_TO_PARENT,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )
            transition.duration = 300
            rotate.interpolator = LinearInterpolator()
            rotate.fillAfter = true
            imageView.startAnimation(rotate)
        }
    }

}