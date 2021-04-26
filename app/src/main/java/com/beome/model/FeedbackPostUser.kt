package com.beome.model

data class FeedbackPostUser(
    var authKey: String = "",
    var idPost : String = "",
    var username: String = "",
    var photoProfile: String? = "",
    var comment: String = "",
    var createdAt : String = "",
    var status : Int = 1,
    var feedbackValue : List<FeedbackPostUserValue> = arrayListOf()
)