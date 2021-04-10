package com.beome.ui.authentication.signup

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.beome.model.User
import com.beome.utilities.NetworkState
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class SignupRepository(coroutineContext: CoroutineContext) {

    val networkState = MutableLiveData<NetworkState>()
    private val job= Job()
    private val scope = CoroutineScope(coroutineContext+job)

    fun registerUser(user : User){
        val collectionUserRef = Firebase.firestore.collection("user").document(user.authKey)
        scope.launch {
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