package com.beome.utilities

import com.beome.constant.ConstantAuth

object AuthHelper {
    fun getAuth(sharedPrefUtil: SharedPrefUtil) : String = sharedPrefUtil.get(ConstantAuth.CONSTANT_AUTH)!!
}