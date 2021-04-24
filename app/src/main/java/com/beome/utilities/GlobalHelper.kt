package com.beome.utilities

import android.app.Activity
import android.content.Context
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.legacy.coreutils.R
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

    fun startImagePickerFromFragment(fragment : Fragment){
        ImagePicker.create(fragment)
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
}