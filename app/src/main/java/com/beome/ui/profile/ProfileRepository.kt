package com.beome.ui.profile

import androidx.lifecycle.MutableLiveData
import com.beome.utilities.NetworkState
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ProfileRepository(coroutineContext: CoroutineContext) {
    val profileState = MutableLiveData<NetworkState>()
    private val job= Job()
    private val scope = CoroutineScope(coroutineContext+job)

    fun getUserProfile() : CollectionReference{
        return Firebase.firestore.collection("user")
    }
}