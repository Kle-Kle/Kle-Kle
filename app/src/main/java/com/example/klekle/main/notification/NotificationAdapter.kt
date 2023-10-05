package com.example.klekle.main.notification

import android.R
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.example.klekle.ArticleActivity
import com.example.klekle.MainActivity
import com.example.klekle.databinding.ListItemNotificationBinding
import com.example.klekle.main.community.BaseViewHolder
import com.example.klekle.model.NotificationModel
import com.example.klekle.util.BitmapConverter
import com.example.klekle.util.UpdateNotificationToRead
import com.example.klekle.util.UpdateNotificationToUnread
import org.json.JSONException
import org.json.JSONObject


class NotificationAdapter: ListAdapter<NotificationModel, BaseViewHolder>(NOTIFICATION_LIST_COMPARATOR) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        BaseViewHolder(ListItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        getItem(position)?.run item@ {
            with(holder.binding as ListItemNotificationBinding) {
                with(btnGoToArticle) {
                    setOnClickListener {
                        // 읽지 않음을 읽음 상태로 업데이트
                        val responseListener: Response.Listener<String?> =
                            Response.Listener<String?> { response ->
                                try {
                                    val jsonResponse = JSONObject(response)
                                    val success = jsonResponse.getBoolean("success")
                                    if(success) {
                                        // TODO 새로고침 같은 효과를 주세요


                                    } else {
                                        // TODO: 에러메시지 출력?
                                    }
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                            }
                        val updateNotificationToRead = UpdateNotificationToRead(notification_no, responseListener)
                        val queue = Volley.newRequestQueue(context.applicationContext)
                        queue.add(updateNotificationToRead)

                        val intent = Intent(context.applicationContext, ArticleActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.putExtra("currentArticleNo", article_no) // 화면 넘어가면서, 해당 글의 article no 를 같이 넘겨줌
                        context.applicationContext.startActivity(intent)
                    }
                }

                // 읽지 않음으로 표시
                // TODO: is read 업데이트 후, 화면을 새로고침 할 방법을 찾고 나면 다시 활성화 시키자
//                with(tvMarkAsUnread) {
//                    if (is_read == 0) {
//                        // 읽은 적 없는 알림에만 보이게
//                        isVisible = false
//                    }
//                    paintFlags = Paint.UNDERLINE_TEXT_FLAG // 밑줄
//                    setOnClickListener {
//                        // '읽지 않음'으로 상태 업데이트
//                        val responseListener: Response.Listener<String?> =
//                            Response.Listener<String?> { response ->
//                                try {
//                                    val jsonResponse = JSONObject(response)
//                                    val success = jsonResponse.getBoolean("success")
//                                    if(success) {
//                                        // TODO 새로고침 같은 효과를 주세요
//                                    } else {
//                                        // TODO: 에러메시지 출력?
//                                    }
//                                } catch (e: JSONException) {
//                                    e.printStackTrace()
//                                }
//                            }
//                        val updateNotificationToUnread = UpdateNotificationToUnread(notification_no, responseListener)
//                        val queue = Volley.newRequestQueue(context.applicationContext)
//                        queue.add(updateNotificationToUnread)
//                    }
//                }

                // 프로필 이미지
                with(ivProfile) {
                    val bm = BitmapConverter.stringToBitmap(user_profile ?: "")
                    setImageBitmap(bm)
                    if (is_read == 1) {
                        alpha = 0.3F
                    }
                }

                // 닉네임
                with(tvNickName) {
                    text = user_nickname ?: ""
                    if (is_read == 1) {
                        alpha = 0.5F
                    }
                }

                // 아이디
                with(tvUserId) {
                    text = user_id ?: ""
                    if (is_read == 1) {
                        alpha = 0.5F
                    }
                }

                // 작성 날짜
                with(tvRegAt) {
                    text = commented ?: ""
                    if (is_read == 1) {
                        alpha = 0.5F
                    }
                }

                // 본문
                with(tvContent) {
                    text = comment_content ?: ""
                    if (is_read == 1) {
                        alpha = 0.5F
                    }
                }
            }
        }
    }

    companion object {
        private val NOTIFICATION_LIST_COMPARATOR = object: DiffUtil.ItemCallback<NotificationModel>() {
            override fun areItemsTheSame(oldItem: NotificationModel, newItem: NotificationModel) =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: NotificationModel, newItem: NotificationModel) =
                oldItem == newItem
        }
    }
}