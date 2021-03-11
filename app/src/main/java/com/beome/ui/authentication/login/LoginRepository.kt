package com.beome.ui.authentication.login

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.beome.constant.ConstantAuth
import com.beome.utilities.NetworkState
import com.beome.utilities.SharedPrefUtil
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class LoginRepository(private val activity : Activity) {
    private val collectionUserRef = Firebase.firestore.collection("user")
    val networkState = MutableLiveData<NetworkState>()
    private lateinit var sharedPrefUtil: SharedPrefUtil

    suspend fun loginUser(email : String, password : String){
        try {
            val querySnapshot = collectionUserRef
                .whereEqualTo("email", email)
                .whereEqualTo("password", password)
                .get()
                .await()
            if(querySnapshot.documents.isNotEmpty()){
                sharedPrefUtil = SharedPrefUtil()
                sharedPrefUtil.start(activity,ConstantAuth.CONSTANT_PREFERENCE)
                sharedPrefUtil.set(ConstantAuth.CONSTANT_AUTH, querySnapshot.documents[0].get("authKey").toString())
                Log.d("authKey", querySnapshot.documents[0].get("authKey").toString())
            }else{
                Log.d("failed_login", "NOT FOUND")
                networkState.postValue(NetworkState.NOT_FOUND)
            }
        }catch (e : Exception){
            Log.d("err_login", e.localizedMessage!!)
        }
    }
}