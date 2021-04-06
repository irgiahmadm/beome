package com.beome.ui.home.recent

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.beome.model.LikedPost
import com.beome.utilities.NetworkState
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class RecentPostRepository(coroutineContext : CoroutineContext) {

    fun getRecentPost(): CollectionReference {
        return Firebase.firestore.collection("post")
    }


}