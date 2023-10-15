package com.example.klekle.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.example.klekle.ArticleActivity
import com.example.klekle.CameraActivity
import com.example.klekle.CommunityActivity
import com.example.klekle.R
import com.example.klekle.databinding.FragmentNoticeBinding
import com.example.klekle.main.community.CommentAdapter
import com.example.klekle.main.notification.NotificationAdapter
import com.example.klekle.model.CommentModel
import com.example.klekle.model.NotificationModel
import com.example.klekle.util.GetCommentsRequest
import com.example.klekle.util.GetNotificationRequest
import org.json.JSONException
import org.json.JSONObject

class NoticeFragment : Fragment() {
    private var _binding: FragmentNoticeBinding? = null
    private val binding get() = _binding!!
    private lateinit var notificationAdapter: NotificationAdapter

    lateinit var userid : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoticeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sharedPreferences = activity?.getSharedPreferences("login_info", Activity.MODE_PRIVATE)!!
        userid = sharedPreferences.getString("loginedId", null).toString()

        with(binding) {
            // 댓글 어댑터 설정
            with(rvNotificationList) {
                notificationAdapter = NotificationAdapter()
                adapter = notificationAdapter
            }
        }
        getNotifications()
    }

    private fun getNotifications() {
        val responseListener: Response.Listener<String?> =
            Response.Listener<String?> { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val success = jsonResponse.getBoolean("success")
                    val results = jsonResponse.getJSONArray("result")
                    val notificationList = ArrayList<NotificationModel>()
                    if(success) {
                        for(i in 0 until results.length()) {
                            val notificationModel = NotificationModel()
                            results.getJSONObject(i).apply {
                                notificationModel.notification_no = getString("notification_no")
                                notificationModel.article_no = getString("article_no")
                                notificationModel.is_read = getInt("is_read")
                                notificationModel.comment_content = getString("comment_content")
                                notificationModel.commented = getString("commented")
                                notificationModel.user_id = getString("comment_author_userid")
                                notificationModel.user_nickname = getString("comment_author_nickname")
                                notificationModel.user_profile = getString("comment_author_profile")
                            }
                            notificationList.add(notificationModel)
                        }
                        notificationAdapter.submitList(notificationList)
                    } else {
                        return@Listener
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        val getNotificationRequest = GetNotificationRequest(userid, responseListener)
        val queue = Volley.newRequestQueue(activity)
        queue.add(getNotificationRequest)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStop() {
        super.onStop()
        // 해당 알림을 클릭해서, 알림 유발 게시글로 이동 했을 시 -> article activity가 본 프레그먼트를 가리면서 `onStop` 호출됨
        // 이 때 새로고침을 하자.. 라는 아이디어..
        with(binding) {
            // 댓글 어댑터 설정
            with(rvNotificationList) {
                notificationAdapter = NotificationAdapter()
                adapter = notificationAdapter
            }
        }
        getNotifications()
    }
}