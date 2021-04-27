package com.beome.model

import java.util.*

data class ReportDetail(
    var reportKey : String = "",
    var idReport: String = "",
    var reportCategory : String = "",
    var reportReason: String = "",
    var reportDesc: String = "",
    var username: String = "",
    var imageUser: String = "",
    var createdAt : Date = Date(),
    var updatedAt : Date = Date()
)