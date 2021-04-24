package com.beome.ui.profile

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.beome.constant.ConstantAuth
import com.beome.model.Follow
import com.beome.model.User
import com.beome.utilities.GlobalHelper
import com.beome.utilities.NetworkState
import com.beome.utilities.SharedPrefUtil
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class ProfileRepository(private val coroutineScope: CoroutineScope) {
    val profileState = MutableLiveData<NetworkState>()
    private val followRef = Firebase.firestore.collection("follow")
    val editProfileState = MutableLiveData<NetworkState>()
    val oldPasswordState = MutableLiveData<NetworkState>()
    val changePasswordState = MutableLiveData<NetworkState>()
    private val userRef = Firebase.firestore.collection("user")

    fun getUserProfile() : CollectionReference{
        return Firebase.firestore.collection("user")
    }
    fun getPostByUser(): CollectionReference {
        return Firebase.firestore.collection("post")
    }

    fun followUser(follow : Follow){
        coroutineScope.launch {
            withContext(Dispatchers.IO){
                try {
                    Firebase.firestore.runTransaction {transaction ->
                        val userRef = Firebase.firestore.collection("user").document(follow.followedId)
                        val user = transaction.get(userRef)
                        val newFollower = user["follower"] as Long + 1
                        transaction.update(userRef,"follower", newFollower)
                        transaction.set(followRef.document(), follow)
                        null
                    }
                }catch (e : Exception){
                    Log.d("error_follow", e.localizedMessage!!)
                }
            }
        }
    }

    fun unFollowUser(followingId : String, followedId : String){
        coroutineScope.launch {
            withContext(Dispatchers.IO){
                try {
                    //get follow status between 2 users
                    val followQuery = followRef
                        .whereEqualTo("followingId", followingId)
                        .whereEqualTo("followedId", followedId)
                        .get()
                        .await()
                    if(followQuery.documents.isNotEmpty()){
                        Firebase.firestore.runTransaction {transaction ->
                            val userRef = Firebase.firestore.collection("user").document(followedId)
                            val user = transaction.get(userRef)
                            //delete follow
                            for (document in followQuery){
                                Log.d("follow_doc_id", document.id)
                                val followerDoc = followRef.document(document.id)
                                val follow = transaction.get(followerDoc)
                                transaction.delete(follow.reference)
                            }
                            //decrement follower user
                            val newFollower = user["follower"] as Long - 1
                            transaction.update(userRef,"follower", newFollower)
                            null
                        }.await()
                    }
                }catch (e : Exception){
                    Log.d("error_follow", e.localizedMessage!!)
                }

            }
        }
    }

    fun getFollowStatus() : CollectionReference{
        return followRef
    }

    fun updateProfile(authKey : String, user : User, activity : Activity){
        coroutineScope.launch {
            withContext(Dispatchers.IO){
                try {
                    val sharedPrefUtil = SharedPrefUtil()
                    sharedPrefUtil.start(activity,ConstantAuth.CONSTANT_PREFERENCE)
                    sharedPrefUtil.set(ConstantAuth.CONSTANT_AUTH_USERNAME, user.username)
                    sharedPrefUtil.set(ConstantAuth.CONSTANT_AUTH_IMAGE, user.photoProfile)
                    Firebase.firestore.runTransaction {transaction ->
                        editProfileState.postValue(NetworkState.LOADING)
                        val docUserRef = Firebase.firestore.collection("user").document(authKey)
                        val data = hashMapOf(
                            "photoProfile" to user.photoProfile,
                            "username" to user.username,
                            "fullName" to user.fullName,
                            "email" to user.email,
                            "birthDate" to user.birthDate,
                            "updatedAt" to user.updatedAt
                        )
                        transaction.set(docUserRef,data, SetOptions.merge())
                    }.await()
                    val collectionPostRef = Firebase.firestore.collection("post")
                    collectionPostRef.whereEqualTo("authKey", sharedPrefUtil.get(ConstantAuth.CONSTANT_AUTH_KEY))
                        .get().addOnSuccessListener {
                            for (document in it){
                                val docRef = collectionPostRef.document(document.id)
                                docRef.update("username", user.username)
                                docRef.update( "imgUser", user.photoProfile)
                            }
                        }.await()
                    editProfileState.postValue(NetworkState.SUCCESS)
                }catch (e : Exception){
                    Log.d("err_update_profile", e.message.toString())
                    editProfileState.postValue(NetworkState.FAILED)
                }
            }
        }
    }

    fun getOldPassword(password : String, authKey: String){
        coroutineScope.launch {
            withContext(Dispatchers.IO){
                try {
                    oldPasswordState.postValue(NetworkState.LOADING)
                    userRef
                        .whereEqualTo("password", GlobalHelper.sha256(password))
                        .whereEqualTo("authKey", authKey).get().addOnSuccessListener {
                        if(it.documents.isNotEmpty()){
                            oldPasswordState.postValue(NetworkState.SUCCESS)
                        }else{
                            oldPasswordState.postValue(NetworkState.NOT_FOUND)
                        }
                    }.addOnFailureListener {
                        oldPasswordState.postValue(NetworkState.FAILED)
                    }.await()
                }catch (e : Exception){
                    oldPasswordState.postValue(NetworkState.FAILED)
                    Log.d("err_get_old_pass", e.message.toString())
                }
            }
        }
    }

    fun changePassword(password : String, authKey: String){
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    changePasswordState.postValue(NetworkState.LOADING)
                    userRef.document(authKey).update("password", GlobalHelper.sha256(password)).await()
                    changePasswordState.postValue(NetworkState.SUCCESS)
                } catch (e: Exception) {
                    changePasswordState.postValue(NetworkState.FAILED)
                }
            }
        }
    }

}