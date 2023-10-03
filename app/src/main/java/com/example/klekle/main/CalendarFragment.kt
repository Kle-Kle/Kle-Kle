package com.example.klekle.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.example.klekle.ArticleActivity
import com.example.klekle.R
import com.example.klekle.databinding.FragmentCalendarBinding
import com.example.klekle.util.BitmapConverter
import com.example.klekle.util.GetMyTodayOneArticleRequest
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class CalendarFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    lateinit var btnGoToArticle : LinearLayout

    lateinit var userid : String
    lateinit var currentArticleNo : String
    var mNow: Long = 0
    var mDate: Date? = null
    var mFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd") // SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sharedPreferences = activity?.getSharedPreferences("login_info", Activity.MODE_PRIVATE)!!
        userid = sharedPreferences.getString("loginedId", null).toString()

        binding.calendarView.setSelectedDate(CalendarDay.today()) // 캘린더 입장 하자마자, 오늘 날짜에 포커스
        // TODO: 포커스 되는 순간, 해당 일자의 글 중 가장 최신 글을 btn_goToArticle 영역에 띄워야 함
        getTodayArticle(getTime().toString())

        binding.calendarView.state().edit()
            .setFirstDayOfWeek(Calendar.SUNDAY)
            .setCalendarDisplayMode(CalendarMode.MONTHS)
            .commit()
        
        binding.calendarView.addDecorators(
            SundayDecorator(),
            SaturdayDecorator()
        )

        btnGoToArticle = view.findViewById(R.id.btn_goToArticle)
    }

    override fun onStart() {
        super.onStart()
        btnGoToArticle.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_goToArticle -> {
//                Toast.makeText(activity, "${getTime()}", Toast.LENGTH_SHORT).show()
                val intent = Intent(activity, ArticleActivity::class.java)
                intent.putExtra("currentArticleNo", currentArticleNo) // 화면 넘어가면서, 해당 글의 article no 를 같이 넘겨줌
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getTime(): String? {
        mNow = System.currentTimeMillis()
        mDate = Date(mNow)
        return mFormat.format(mDate)
    }

    private fun getTodayArticle(currentDate: String) {
        val responseListener: Response.Listener<String> =
            Response.Listener { response ->
                try {
                    val jsonObject = JSONObject(response)
                    val success = jsonObject.getBoolean("success")
                    if (success) {
                        binding.tvGuide.text = "${userid}님의 ${currentDate} 기록"

                        var user_profile = BitmapConverter.stringToBitmap(jsonObject.getString("user_profile") ?: "")
                        binding.ivProfile.setImageBitmap(user_profile)
                        binding.tvNickName.text = jsonObject.getString("user_nickname")
                        binding.tvUserId.text = jsonObject.getString("userid")
                        binding.tvRegAt.text = jsonObject.getString("published")
                        binding.tvContent.text = jsonObject.getString("article_content")
                        val article_image = BitmapConverter.stringToBitmap(jsonObject.getString("article_image") ?: "")
                        binding.ivContent.setImageBitmap(article_image)
                        binding.tvComment.text = jsonObject.getString("comment_count")
                        binding.tvFavorite.text = "0"
                        currentArticleNo = jsonObject.getString("article_no")
                    } else {
                        // TODO: 오늘은 작성한 게시글이 없네요~ 작성하러 가보시겠어요? 하는 버튼을 대신 생성 ㄱㄱ
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        val getMyTodayOneArticleRequest =
            GetMyTodayOneArticleRequest(
                "$currentDate%",
                userid,
                responseListener
            )
        val queue = Volley.newRequestQueue(activity)
        queue.add(getMyTodayOneArticleRequest)
    }
}