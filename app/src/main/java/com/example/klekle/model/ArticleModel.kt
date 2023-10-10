package com.example.klekle.model

data class ArticleModel(
    var user_nickname: String? = null,
    var user_id: String? = null,
    var user_profile: String? = null,
    var article_no: String? = null,
    var published: String? = null,
    var article_content: String? = null,
    var comment_count: String? = null,
    var article_image: String? = null,
    var is_edited: Int? = null
)