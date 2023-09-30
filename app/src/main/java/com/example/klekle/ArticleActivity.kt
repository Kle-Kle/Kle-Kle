package com.example.klekle

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.example.klekle.databinding.ActivityArticleBinding
import com.example.klekle.main.community.CommentAdapter
import com.example.klekle.model.CommentModel
import com.example.klekle.util.BitmapConverter
import com.example.klekle.util.CreateCommentsRequest
import com.example.klekle.util.GetArticleDetailRequest
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

    lateinit var userid : String
    lateinit var articleNo : String

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
        userid = sharedPreferences.getString("loginedId", null).toString() // 해당 게시글 수정/삭제 or 댓글들 수정/삭제 시, 권한이 있는 사용자인지 판단키 위해
        articleNo = "2" // TODO: 해당 페이지로 넘어오기 전에, 선택한 글의 article no를 넘겨 받도록 해서 처리할 것

        with(binding) {
            // 댓글 어댑터 설정
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
        getAndSetArticleDetail()

        // 댓글 글자 수 세기
        binding.etComment.addTextChangedListener {
            var currentCommentLength = binding.etComment.length()
            binding.tvCurrentCommentLength.text = "($currentCommentLength/280)"

            if (currentCommentLength == 0) {
                binding.btnComment.isEnabled = false
                binding.btnComment.alpha = 0.5F
            }
            else {
                binding.btnComment.isEnabled = true
                binding.btnComment.alpha = 1F
            }
        }
        binding.btnComment.isEnabled = false // 첫 입장 시 '댓글 작성' 버튼 비활성화
    }

    override fun onStart() {
        super.onStart()

        articleAuthorProfile.setOnClickListener(this)
        articleAuthorId.setOnClickListener(this)
        btnLike.setOnClickListener(this)
        btnComment.setOnClickListener(this)
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
                createComment()
            }
        }
    }

    private fun createComment() {
        val currentCommentContent = binding.etComment.text.toString()

        val responseListener: Response.Listener<String?> =
            Response.Listener<String?> { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val success = jsonResponse.getBoolean("success")
                    if(success) {
                        // 페이지 새로고침
                        finish() // 인텐트 종료
                        overridePendingTransition(0, 0) // 인텐트 효과 없애기
                        val intent = intent // 인텐트
                        startActivity(intent) // 액티비티 열기
                        overridePendingTransition(0, 0) // 인텐트 효과 없애기
                    } else {
                        Toast.makeText(this, "댓글 작성에 실패했습니다.\n잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        val createCommentsRequest = CreateCommentsRequest(currentCommentContent, userid, articleNo, responseListener)
        val queue = Volley.newRequestQueue(this)
        queue.add(createCommentsRequest)
    }

    private fun moveToUserPage() {
        val intent = Intent(this, UserPageActivity::class.java)
        startActivity(intent)
    }

    private fun getComments() {
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

    private fun getAndSetArticleDetail() {
        val responseListener: Response.Listener<String?> =
            Response.Listener<String?> { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val success = jsonResponse.getBoolean("success")
                    val results = jsonResponse.getJSONArray("result")
                    if(success) {
                        results.getJSONObject(0).apply {
                            var bm = BitmapConverter.stringToBitmap(getString("userProfile") ?: "") // 사용자 프로필 이미지
                            binding.articleAuthorProfileImage.setImageBitmap(bm)

                            binding.articleAuthorNickname.text = getString("userNickname")
                            binding.articleAuthorUserid.text = getString("userId")
                            binding.tvPublished.text = getString("published")
                            binding.tvArticleContent.text = getString("articleContent")
                            binding.tvStatusCommentAndLike.text = "댓글 ${getString("commentCount")}개 | 좋아요 0회"

                            bm = BitmapConverter.stringToBitmap(getString("articleImage") ?: "") // article image
                            binding.ivArticleImage.setImageBitmap(bm)
                        }
                    } else {
                        return@Listener
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        val getArticleDetailRequest = GetArticleDetailRequest(articleNo, responseListener)
        val queue = Volley.newRequestQueue(this)
        queue.add(getArticleDetailRequest)
    }
}