package com.beome.model

data class Post(
    var idPost: String = "",
    var userName: String = "", 
    var imgUser: String = "",
    var imagePost: String = "",
    var title: String = "",
    var description: String = "",
    var likeCount: Int = 0,
    var createdAt: String = "",
    var updatedAt: String  = ""
)