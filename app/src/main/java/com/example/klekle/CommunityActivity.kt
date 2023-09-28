package com.example.klekle

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
            }
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