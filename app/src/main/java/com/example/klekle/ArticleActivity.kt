package com.example.klekle

import android.app.Activity
import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.example.klekle.databinding.ActivityArticleBinding
import com.example.klekle.main.community.ArticleAdapter
import com.example.klekle.main.community.CommentAdapter
import com.example.klekle.model.ArticleModel
import com.example.klekle.model.CommentModel
import com.example.klekle.util.GetArticleListRequest
import com.example.klekle.util.GetCommentsRequest
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONException
import org.json.JSONObject

class ArticleActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityArticleBinding
    private lateinit var commentAdapter: CommentAdapter

    lateinit var articleAuthorProfile : CircleImageView
    lateinit var articleAuthorId : LinearLayout
    lateinit var btnLike : LinearLayout
    lateinit var btnComment : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)
        binding = ActivityArticleBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        articleAuthorProfile = findViewById(R.id.article_author_profile_image)
        articleAuthorId = findViewById(R.id.article_author_id)
        btnLike = findViewById(R.id.btn_like)
        btnComment = findViewById(R.id.btn_comment)

        val sharedPreferences = getSharedPreferences("login_info", Activity.MODE_PRIVATE)
        val userid = sharedPreferences.getString("loginedId", null) // todo: 해당 게시글 추가/삭제 or 댓글들 추가/삭제 시, 권한이 있는 사용자인지 판단키 위해

        with(binding) {
            // 아티클 어댑터 설정
            with(rvCommentList) {
                commentAdapter = CommentAdapter().apply {
                    moreUnit {
                        // todo
                    }
                }
                adapter = commentAdapter
            }
        }
        getComments()
    }

    override fun onStart() {
        super.onStart()

        articleAuthorProfile.setOnClickListener(this)
        articleAuthorId.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.article_author_profile_image -> {
                // todo
                moveToUserPage()
            }
            R.id.article_author_id -> {
                // todo
                moveToUserPage()
            }
            R.id.btn_like -> {
                // todo
            }
            R.id.btn_comment -> {
                // todo
            }
        }
    }

    private fun moveToUserPage() {
        val intent = Intent(this, UserPageActivity::class.java)
        startActivity(intent)
    }

    private fun getComments() {
        val articleNo = "2" // TODO: 해당 페이지로 넘어오기 전에, 선택한 글의 article no를 넘겨 받도록 해서 처리할 것

        val responseListener: Response.Listener<String?> =
            Response.Listener<String?> { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val success = jsonResponse.getBoolean("success")
                    val results = jsonResponse.getJSONArray("result")
                    val commentList = ArrayList<CommentModel>()
                    if(success) {
                        for(i in 0 until results.length()) {
                            val commentModel = CommentModel()
                            results.getJSONObject(i).apply {
                                commentModel.user_nickname = getString("userNickname")
                                commentModel.user_id = getString("userid")
                                commentModel.user_profile = getString("userProfile")
                                commentModel.comment_no = getString("commentNo")
                                commentModel.comment_content = getString("commentContent")
                                commentModel.commented = getString("commented")
                                commentModel.articleNo = getString("articleNo")
                            }
                            commentList.add(commentModel)
                        }
                        commentAdapter.submitList(commentList)
                    } else {
                        return@Listener
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        val getCommentsRequest = GetCommentsRequest(articleNo, responseListener)
        val queue = Volley.newRequestQueue(this)
        queue.add(getCommentsRequest)
    }
}