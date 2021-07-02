package com.beome.ui.authentication.login

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.beome.constant.ConstantAuth
import com.beome.utilities.NetworkState
import com.beome.utilities.SharedPrefUtil
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class LoginRepository(private val activity : Activity, private val scope: CoroutineScope) {
    private val collectionUserRef = Firebase.firestore.collection("user")
    val networkState = MutableLiveData<NetworkState>()
    private lateinit var sharedPrefUtil: SharedPrefUtil

    fun loginUser(email : String, password : String){
        scope.launch {
            withContext(Dispatchers.IO){
                try {
                    networkState.postValue(NetworkState.LOADING)
                    val querySnapshot = collectionUserRef
                        .whereEqualTo("email", email)
                        .whereEqualTo("password", password)
                        .get()
                        .await()
                    if(querySnapshot.documents.isNotEmpty()){
                        sharedPrefUtil = SharedPrefUtil()
                        sharedPrefUtil.start(activity,ConstantAuth.CONSTANT_PREFERENCE)
                        sharedPrefUtil.set(ConstantAuth.CONSTANT_AUTH_KEY, querySnapshot.documents[0].get("authKey").toString())
                        sharedPrefUtil.set(ConstantAuth.CONSTANT_AUTH_USERNAME, querySnapshot.documents[0].get("username").toString())
                        sharedPrefUtil.set(ConstantAuth.CONSTANT_AUTH_ROLE, querySnapshot.documents[0].get("role").toString())
                        sharedPrefUtil.set(ConstantAuth.CONSTANT_AUTH_STATUS, querySnapshot.documents[0].get("userStatus").toString())
                        if(querySnapshot.documents[0].get("photoProfile").toString().isNotEmpty()){
                            sharedPrefUtil.set(ConstantAuth.CONSTANT_AUTH_IMAGE, querySnapshot.documents[0].get("photoProfile").toString())
                        }else{
                            sharedPrefUtil.set(ConstantAuth.CONSTANT_AUTH_IMAGE, "")
                        }
                        networkState.postValue(NetworkState.SUCCESS)
                    }else{
                        Log.d("failed_login", "NOT FOUND")
                        networkState.postValue(NetworkState.NOT_FOUND)
                    }
                }catch (e : Exception){
                    Log.d("err_login", e.localizedMessage!!)
                    networkState.postValue(NetworkState.FAILED)
                }
            }
        }

    }
}