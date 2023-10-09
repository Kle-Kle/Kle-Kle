package com.example.klekle

import android.R.attr
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.example.klekle.databinding.ActivityCommunityBinding
import com.example.klekle.main.community.ArticleAdapter
import com.example.klekle.model.ArticleModel
import com.example.klekle.util.GetArticleListRequest
import org.json.JSONException
import org.json.JSONObject


class CommunityActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommunityBinding
    private lateinit var articleAdapter: ArticleAdapter

    val REQUEST_CODE = 123 // 원하는 값을 사용할 수 있습니다.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community)
        binding = ActivityCommunityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

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
                articleAdapter.setPersonProfile(true);
            }
        }

        binding.fBtn.setOnClickListener {
            val intent = Intent(this, WritePostActivity::class.java)
            startActivityForResult(intent,REQUEST_CODE)
        }

        getArticleList()
    }

    private fun getArticleList() {
        val sharedPreferences = getSharedPreferences("login_info", Activity.MODE_PRIVATE)
        val userid = sharedPreferences.getString("loginedId", null)
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
                                articleModel.user_nickname = getString("user_nickname")
                                articleModel.user_id = getString("user_id")
                                articleModel.user_profile = getString("user_profile")
                                articleModel.article_no = getString("article_no")
                                articleModel.published = getString("published")
                                articleModel.article_content = getString("article_content")
                                articleModel.comment_count = getString("comment_count")
                                articleModel.article_image = getString("article_image")
                            }
                            articleList.add(articleModel)
                        }
                        println("NewHomeFragment: response: ${jsonResponse}, articleList: ${articleList}")

                        articleAdapter.submitList(articleList)

                    } else {
                        return@Listener
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        val loginRequest = GetArticleListRequest(userid, responseListener)
        val queue = Volley.newRequestQueue(this)
        queue.add(loginRequest)
    }
}