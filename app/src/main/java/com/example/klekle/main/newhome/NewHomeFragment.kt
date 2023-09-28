package com.example.klekle.main.newhome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.example.klekle.databinding.FragmentNewHomeBinding
import com.example.klekle.model.ArticleModel
import com.example.klekle.util.GetArticleListRequest
import org.json.JSONException
import org.json.JSONObject

class NewHomeFragment : Fragment() {
    private var _binding: FragmentNewHomeBinding? = null
    private val binding get() = _binding!! // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private lateinit var articleAdapter: ArticleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getArticleList() {
        val sharedPreferences = requireContext().getSharedPreferences("login_info", AppCompatActivity.MODE_PRIVATE)
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
        val queue = Volley.newRequestQueue(requireContext())
        queue.add(loginRequest)
    }
}