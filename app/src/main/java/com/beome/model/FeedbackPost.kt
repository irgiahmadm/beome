package com.beome.model

class FeedbackPost(
    var user: ArrayList<FeedbackPostUser> = arrayListOf(),
    var feedbackValue : ArrayList<FeedbackPostUserValue> = arrayListOf()
)