package com.example.klekle.model

data class CommentModel(
    var user_nickname: String? = null,
    var user_id: String? = null,
    var user_profile: String? = null,
    var comment_no: String? = null,
    var comment_content: String? = null,
    var commented: String? = null,
    var articleNo: String? = null
)