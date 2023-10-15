package com.example.klekle

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.example.klekle.databinding.ActivityCommunityBinding
import com.example.klekle.databinding.ActivityPersonalProfileBinding
import com.example.klekle.main.community.ArticleAdapter
import com.example.klekle.model.ArticleModel
import com.example.klekle.util.BitmapConverter
import com.example.klekle.util.GetArticleListRequest
import com.example.klekle.util.GetMyArticleListRequest
import org.json.JSONException
import org.json.JSONObject

class PersonalProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonalProfileBinding
    private lateinit var articleAdapter: ArticleAdapter

    private var userid : String = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_profile)
        binding = ActivityPersonalProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val sharedPreferences = getSharedPreferences("login_info", MODE_PRIVATE)!!
        val loginedId = sharedPreferences.getString("loginedId", null).toString()

        // 바이트 배열 데이터를 추출
        val Profile = intent.getByteArrayExtra("Profile")
        var NickName = intent.getStringExtra("NickName")
        var UserId = intent.getStringExtra("UserId")

        if (UserId != null) {
            userid = UserId
        }

        if(Profile != null) {
            val bitmap = BitmapFactory.decodeByteArray(Profile, 0, Profile.size)
            binding.articleAuthorProfileImage.setImageBitmap(bitmap)
        }

        binding.tvNickName.text = NickName
        binding.tvUserId.text = UserId


        with(binding) {
            // 아티클 어댑터 설정
            with(rvArticleList) {
                articleAdapter = ArticleAdapter().apply {
                    moreUnit {
                        //
                    }
                }
                adapter = articleAdapter

                // articlAdapter 에서 Community 에서 만 다른 화면을 넘어가게 확인하는 flag 추가
                articleAdapter.setPersonProfile(true)
                articleAdapter.setCurrentUserid(loginedId)
                articleAdapter.setActivity(this@PersonalProfileActivity)
            }
        }
        getArticleList()
    }

    private fun getArticleList() {
        val sharedPreferences = getSharedPreferences("login_info", Activity.MODE_PRIVATE)

        //val userid = sharedPreferences.getString("loginedId", null)

        val responseListener: Response.Listener<String?> =
            Response.Listener<String?> { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val success = jsonResponse.getBoolean("success")
                    val results = jsonResponse.getJSONArray("result")
                    val articleList = ArrayList<ArticleModel>()
                    if(success) {
                        for(i in 0 until results.length()) {
                            val articleModel = ArticleModel()
                            results.getJSONObject(i).apply {
                                articleModel.user_nickname = getString("userNickname")
                                articleModel.user_id = getString("userId")
                                articleModel.user_profile = getString("userProfile")
                                articleModel.article_no = getString("articleNo")
                                articleModel.published = getString("published")
                                articleModel.article_content = getString("articleContent")
                                articleModel.comment_count = getString("commentCount")
                                articleModel.article_image = getString("articleImage")
                                articleModel.is_edited = getInt("isEdited")
                            }
                            articleList.add(articleModel)
                        }

                        articleAdapter.submitList(articleList)

                    } else {
                        return@Listener
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        val loginRequest = GetMyArticleListRequest(userid, responseListener)
        val queue = Volley.newRequestQueue(this)
        queue.add(loginRequest)
    }
}