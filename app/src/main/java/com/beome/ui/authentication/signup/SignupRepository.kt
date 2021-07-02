package com.beome.ui.authentication.signup

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.beome.model.User
import com.beome.utilities.NetworkState
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class SignupRepository(private val scope : CoroutineScope) {

    val networkState = MutableLiveData<NetworkState>()
    val isUserNameExist = MutableLiveData<Boolean>()
    val isEmailExist = MutableLiveData<Boolean>()
    private val collectionUserRef = Firebase.firestore.collection("user")
    fun registerUser(user : User){
        val collectionUserRef = Firebase.firestore.collection("user").document(user.authKey)
        scope.launch {
            withContext(Dispatchers.IO){
                try {
                    networkState.postValue(NetworkState.LOADING)
                    collectionUserRef.set(user).await()
                    networkState.postValue(NetworkState.SUCCESS)
                }catch(e : Exception){
                    networkState.postValue(NetworkState.FAILED)
                    Log.d("error_signup", e.localizedMessage!!)
                }
            }

        }

    }

    fun isEmailExist(email : String){
        scope.launch {
            withContext(Dispatchers.IO) {
                collectionUserRef.whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener {
                        isEmailExist.postValue(it.documents.isNotEmpty())
                    }
                    .addOnFailureListener {
                        Log.d("err_get_email", it.message.toString())
                    }.await()
            }
        }
    }

    fun isUsernameExist(username : String){
        scope.launch {
            withContext(Dispatchers.IO){
                collectionUserRef.whereEqualTo("username", username)
                    .get()
                    .addOnSuccessListener {
                        isUserNameExist.postValue(it.documents.isNotEmpty())
                    }
                    .addOnFailureListener {
                        Log.d("err_get_email", it.message.toString())
                    }.await()
            }
        }
    }
}