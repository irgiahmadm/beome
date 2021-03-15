package com.beome.ui.add

import androidx.lifecycle.*
import com.beome.model.ComponentFeedbackPost
import com.beome.model.Post
import com.beome.ui.authentication.signup.SignupRepository
import com.beome.utilities.NetworkState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddPostViewModel : ViewModel() {
    private val addPostRepo = AddPostRepository(Dispatchers.IO)
    lateinit var addPostState : LiveData<NetworkState>
    lateinit var addComponentState : LiveData<NetworkState>
    private val _addPostRepo = MutableLiveData<AddPostRepository>()

    fun setUpAddPost(){
        addPostState = Transformations.switchMap(_addPostRepo, AddPostRepository::addPostState)
        _addPostRepo.postValue(addPostRepo)
    }

    fun setUpComponentFeedback(){
        addComponentState = Transformations.switchMap(_addPostRepo,
            AddPostRepository::addComponentState)
        _addPostRepo.postValue(addPostRepo)
    }

    fun addPost(post : Post) = viewModelScope.launch{
        withContext(Dispatchers.IO){
            addPostRepo.addPost(post)
        }
    }

    fun addComponentFeedbackPost(componentFeedbackPost: ComponentFeedbackPost, list: Int, counter : Int) = viewModelScope.launch{
        withContext(Dispatchers.IO){
            addPostRepo.addComponentFeedbackPost(componentFeedbackPost, list, counter)
        }
    }
}