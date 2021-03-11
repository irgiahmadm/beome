package com.beome.ui.authentication.signup

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.beome.model.User
import com.beome.utilities.NetworkState
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class SignupRepository {
    private val collectionUserRef = Firebase.firestore.collection("user")
    val networkState = MutableLiveData<NetworkState>()

    suspend fun registerUser(user : User){
        try {
            networkState.postValue(NetworkState.LOADING)
            collectionUserRef.add(user).await()
            networkState.postValue(NetworkState.SUCCESS)
        }catch(e : Exception){
            networkState.postValue(NetworkState.FAILED)
            Log.d("error_signup", e.localizedMessage!!)
        }
    }
}