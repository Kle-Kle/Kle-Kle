package com.example.klekle.model

data class NotificationModel(
    var notification_no: String? = null,
    var is_read: Int? = null,
    var article_no: String? = null,
    var user_id: String? = null, // comment author
    var user_nickname: String? = null, // comment author
    var user_profile: String? = null, // comment author
    var comment_content: String? = null,
    var commented: String? = null
)