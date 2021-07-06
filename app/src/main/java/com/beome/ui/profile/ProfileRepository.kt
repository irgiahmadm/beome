package com.beome.ui.profile

import android.app.Activity
import android.util.Log
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
                    Firebase.firestore.runTransaction {transaction ->
                        val userRef = Firebase.firestore.collection("user").document(follow.followedId)
                        val reportedUserRef = Firebase.firestore.collection("reported_account").document(follow.followedId)
                        val user = transaction.get(userRef)
                        val reportedUser = transaction.get(reportedUserRef)
                        val newFollower = user["follower"] as Long + 1
                        val newPoint = user["userPoint"] as Long + 1
                        transaction.update(userRef,"follower", newFollower)
                        transaction.update(userRef,"userPoint", newPoint)
                        if(reportedUser.exists()){
                            val newFolloweronReport = reportedUser["user.follower"] as Long + 1
                            val newPointonReport = reportedUser["user.follower"] as Long + 1
                            transaction.update(reportedUserRef,"user.follower", newFolloweronReport)
                            transaction.update(reportedUserRef,"user.userPoint", newPointonReport)
                        }
                        transaction.set(followRef.document(), follow)
                    }.addOnSuccessListener {

                    }.addOnFailureListener {
                        Log.d("err_follow_user", it.message.toString())
                    }.await()
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
                            val reportedUserRef = Firebase.firestore.collection("reported_account").document(followedId)
                            val user = transaction.get(userRef)
                            val reportedUser = transaction.get(reportedUserRef)
                            //delete follow
                            for (document in followQuery){
                                Log.d("follow_doc_id", document.id)
                                val followerDoc = followRef.document(document.id)
                                val follow = transaction.get(followerDoc)
                                transaction.delete(follow.reference)
                            }
                            //decrement follower user
                            val followCounter = user["follower"] as Long - 1
                            val pointCounter = user["userPoint"] as Long - 1
                            transaction.update(userRef,"follower", followCounter)
                            transaction.update(userRef,"userPoint", pointCounter)
                            if(reportedUser.exists()){
                                val followCounteronReport = reportedUser["user.follower"] as Long - 1
                                val pointCounteronReport = reportedUser["user.userPoint"] as Long - 1
                                transaction.update(reportedUserRef,"user.follower", followCounteronReport)
                                transaction.update(reportedUserRef,"user.userPoint", pointCounteronReport)
                            }
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

                    //update reported feedback
                    val collectionFeedbackRef = Firebase.firestore.collection("reported_feedback")
                    collectionPostRef.whereEqualTo("authKey", user.authKey).get().addOnSuccessListener {
                        for (document in it){
                            val docRef = collectionFeedbackRef.document(document.id)
                            docRef.update("username", user.username)
                            docRef.update("photoProfile", user.photoProfile)
                        }
                    }.await()
                    //update reported feedback detail
                    val collectionReportFeedbackDetail = Firebase.firestore.collection("reported_feedback_detail")
                    collectionReportFeedbackDetail.whereEqualTo("username", user.username).get().addOnSuccessListener {
                        for (document in it){
                            val docRef = collectionFeedbackRef.document(document.id)
                            docRef.update("username", user.username)
                            docRef.update("imageUser", user.photoProfile)
                        }
                    }
                    //update reported post
                    val collectionReportPost = Firebase.firestore.collection("reported_post")
                    collectionReportPost.whereEqualTo("username", user.username).get().addOnSuccessListener {
                        for (document in it){
                            val docRef = collectionReportPost.document(document.id)
                            docRef.update("username", user.username)
                            docRef.update("imgUser", user.photoProfile)
                        }
                    }
                    //update reported post detail
                    val collectionReportPostDetail = Firebase.firestore.collection("reported_post_detail")
                    collectionReportPostDetail.whereEqualTo("username", user.username).get().addOnSuccessListener {
                        for (document in it){
                            val docRef = collectionReportPostDetail.document(document.id)
                            docRef.update("username", user.username)
                            docRef.update("imageUser", user.photoProfile)
                        }
                    }
                    //update reported account
                    val collectionReportAccount = Firebase.firestore.collection("reported_account")
                    collectionReportAccount.whereEqualTo("username", user.username).get().addOnSuccessListener {
                        for (document in it){
                            val docRef = collectionReportAccount.document(document.id)
                            docRef.update("username", user.username)
                            docRef.update("photoProfile", user.photoProfile)
                        }
                    }

                    //update reported account detail
                    val collectionReportAccountDetail = Firebase.firestore.collection("reported_account_detail")
                    collectionReportAccountDetail.whereEqualTo("username", user.username).get().addOnSuccessListener {
                        for (document in it){
                            val docRef = collectionReportAccountDetail.document(document.id)
                            docRef.update("username", user.username)
                            docRef.update("imageUser", user.photoProfile)
                        }
                    }
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

    fun updateToken(authKey: String, currentToken: String) {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    Firebase.firestore.runTransaction { transaction ->
                        val docUserRef = Firebase.firestore.collection("user").document(authKey)
                        val user = transaction.get(docUserRef)
                        val tokenUser = user["token"]
                        if (tokenUser != currentToken) {
                            val data = hashMapOf("token" to currentToken)
                            transaction.set(docUserRef, data, SetOptions.merge())
                        } else {
                            Log.d("token_is_same", currentToken)
                        }
                    }.addOnSuccessListener {
                        Log.d("success_updt_token", currentToken)
                    }.addOnFailureListener {
                        Log.d("failed_updt_token", it.message.toString())
                    }.await()
                } catch (e: Exception) {

                }
            }
        }
    }

    //this function is used when user logout from application
    fun deleteToken(authKey: String) {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    Firebase.firestore.runTransaction { transaction ->
                        val userRef = Firebase.firestore.collection("user").document(authKey)
                        val data = hashMapOf("token" to "")
                        transaction.set(userRef, data, SetOptions.merge())
                    }.addOnSuccessListener {
                        Log.d("success_delete_token", "")
                    }.addOnFailureListener {
                        Log.d("failed_updt_token", it.message.toString())
                    }.await()

                } catch (e: Exception) {


                }
            }
        }
    }

}