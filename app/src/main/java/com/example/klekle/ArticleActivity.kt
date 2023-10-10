package com.example.klekle

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.klekle.auth.LoginActivity
import com.example.klekle.databinding.ActivityArticleBinding
import com.example.klekle.main.community.CommentAdapter
import com.example.klekle.model.CommentModel
import com.example.klekle.util.BitmapConverter
import com.example.klekle.util.CreateCommentsRequest
import com.example.klekle.util.DeleteArticleRequest
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
    lateinit var btnMore : ImageView
    lateinit var btnLike : LinearLayout
    lateinit var btnComment : Button

    lateinit var authorUserFcmToken : String // article의 게시자 fcm token
    lateinit var articleContent : String
    lateinit var userid : String
    lateinit var articleNo : String
    lateinit var articleImage : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)
        binding = ActivityArticleBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        articleAuthorProfile = findViewById(R.id.article_author_profile_image)
        articleAuthorId = findViewById(R.id.article_author_id)
        btnMore = findViewById(R.id.btn_more)
        btnLike = findViewById(R.id.btn_like)
        btnComment = findViewById(R.id.btn_comment)

        val sharedPreferences = getSharedPreferences("login_info", Activity.MODE_PRIVATE)
        userid = sharedPreferences.getString("loginedId", null).toString() // 해당 게시글 수정/삭제 or 댓글들 수정/삭제 시, 권한이 있는 사용자인지 판단키 위해
        val intent = intent // 해당 페이지로 넘어오기 전에, 선택한 글의 article no를 넘겨 받음
        articleNo = intent.getStringExtra("currentArticleNo").toString()

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
        btnMore.setOnClickListener(this)
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
            R.id.btn_more -> {
                showPopupMenu(v, articleNo, articleContent, articleImage)
            }
            R.id.btn_like -> {
                // todo
            }
            R.id.btn_comment -> {
                createComment()
            }
        }
    }

    private fun showPopupMenu(view: View, article_no: String?, article_content: String?, article_image: String?) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.inflate(R.menu.popup_menu) // 팝업 메뉴 리소스 파일 지정

        // 팝업 메뉴 아이템 클릭 이벤트 처리
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_item_1 -> {
                    // 메뉴 아이템 1 클릭 시 수행할 동작 추가
                    // 게시글 삭제하기
                    deleteArticle(article_no)
                    true
                }
                R.id.menu_item_2 -> {
                    // 메뉴 아이템 2 클릭 시 수행할 동작 추가
                    // 게시글 수정하기
                    updateArticle(article_no, article_content, article_image)
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    private fun updateArticle(article_no: String?, article_content: String?, article_image: String?) {
        val intent = Intent(this, WritePostActivity::class.java)

        intent.putExtra("article_no", article_no)
        intent.putExtra("content", article_content)
        if ("$article_image" == "null" || "$article_image" == "") intent.putExtra("mode", "updateIsNotImage") // 왜인지 article_image == null 이건 안 먹힘
        else {
            intent.putExtra("mode", "updateIsImage")
            val sharedPreferences = getSharedPreferences("tempData", Activity.MODE_PRIVATE) // 이미지가 너무 커서, 이 방식으로 데이터 전송
            val temp = sharedPreferences.edit() // bitmap을 encoding 한 string의 크기가 너무 커서, putExtra로는 넘어가지 않음.. shared preference로 재도전
            temp.putString("existingArticleImage", article_image)
            temp.apply()
        }

        startActivity(intent)
    }

    private fun deleteArticle(article_no: String?) {
        try{
            val str_tittle = "게시글 삭제"
            val str_message = "이 게시글을 삭제하시겠습니까?"
            val str_buttonOK = "확인"
            val str_buttonNO = "취소"

            val builder = AlertDialog.Builder(this)
            builder.setTitle(str_tittle) // 팝업창 타이틀 지정
            builder.setMessage(str_message) // 팝업창 내용 지정
            builder.setPositiveButton(str_buttonOK, DialogInterface.OnClickListener { dialog, which ->
                val responseListener: Response.Listener<String?> =
                    Response.Listener<String?> { response ->
                        try {
                            val jsonResponse = JSONObject(response)
                            val success = jsonResponse.getBoolean("success")
                            if (success) {
                                // 새로고침.. ^^
                                val intent = Intent(this, MainActivity::class.java)
                                val intent2 = Intent(this, CommunityActivity::class.java)
                                finishAffinity()
                                overridePendingTransition(0, 0) // 인텐트 효과 없애기
                                startActivity(intent)
                                overridePendingTransition(0, 0) // 인텐트 효과 없애기
                                startActivity(intent2)
                                overridePendingTransition(0, 0) // 인텐트 효과 없애기
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                val rqst = DeleteArticleRequest(article_no, responseListener)
                val queue = Volley.newRequestQueue(this)
                queue.add(rqst)
            })
            builder.setNegativeButton(str_buttonNO, DialogInterface.OnClickListener { dialog, which ->
                try {
                    dialog.dismiss()
                }
                catch (e : Exception){
                    e.printStackTrace()
                }
            })
            val dialog = builder.create()
            try {
                dialog.show()
            }
            catch (e : Exception){
                e.printStackTrace()
            }
        }
        catch(e : Exception){
            e.printStackTrace()
        }
    }

    private fun createComment() {
        val currentCommentContent = binding.etComment.text.toString()
        pushComentNotification(currentCommentContent)

        val responseListener: Response.Listener<String?> =
            Response.Listener<String?> { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val success = jsonResponse.getBoolean("success")
                    if(success) {
                        // 페이지 새로고침
                        refresh()
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

    private fun pushComentNotification(currentCommentContent: String) {
        val notiTitle = "새 댓글이 달렸어요: ${articleContent}"
        val notiBody = currentCommentContent
        val url = "https://fcm.googleapis.com/fcm/send"

        val jsonObject = JSONObject()
        val notification = JSONObject()
        notification.put("title", notiTitle)
        notification.put("body", notiBody)
        jsonObject.put("to", authorUserFcmToken)
        jsonObject.put("priority", "high")
        jsonObject.put("notification", notification)

        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            Response.Listener<JSONObject?> {
                Log.d("test:D", jsonObject.toString())
            }, Response.ErrorListener { error ->
                error.printStackTrace()
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params.put("Content-Type", "application/json")
                params.put("Authorization", BuildConfig.FCM_API_KEY)
                return params
            }
        }

        val queue = Volley.newRequestQueue(this)
        queue.add(jsonObjectRequest)
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
                            articleImage = getString("articleImage") ?: "" // article image
                            bm = BitmapConverter.stringToBitmap(articleImage)
                            binding.ivArticleImage.setImageBitmap(bm)

                            authorUserFcmToken = getString("userFcmToken")

                            val authorUserid = getString("userId")
                            binding.articleAuthorUserid.text = authorUserid
                            binding.articleAuthorNickname.text = getString("userNickname")
                            binding.tvPublished.text = getString("published")
//                            binding.tvIsEdited.text
                            val is_edited = getInt("isEdited")
                            binding.tvIsEdited.text = if (is_edited == 1) "  •  (편집됨)"
                            else ""
                            articleContent = getString("articleContent")
                            binding.tvArticleContent.text = articleContent
                            binding.tvStatusCommentAndLike.text = "댓글 ${getString("commentCount")}개 | 좋아요 0회"

                            // 자신이 작성한 글이면 more unit 나타나게
                            if (authorUserid == userid) binding.btnMore.visibility = View.VISIBLE
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

    private fun refresh() {
        val intent = intent // 인텐트
        finish()
        overridePendingTransition(0, 0) // 인텐트 효과 없애기
        startActivity(intent) // 액티비티 열기
        overridePendingTransition(0, 0) // 인텐트 효과 없애기
    }
}