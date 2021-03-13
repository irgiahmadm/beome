package com.beome.utilities

import android.app.Activity
import androidx.fragment.app.Fragment
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
}