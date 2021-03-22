package com.beome.model

data class FeedbackPostUser(
    var authKey: String = "",
    var username: String = "",
    var photoProfile: String? = "",
    var comment: String = "",
    var createdAt : String = ""
)