package com.example.klekle.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.example.klekle.ArticleActivity
import com.example.klekle.CameraActivity
import com.example.klekle.R
import com.example.klekle.databinding.FragmentCalendarBinding
import com.example.klekle.util.BitmapConverter
import com.example.klekle.util.GetMyArticlesThisMonthRequest
import com.example.klekle.util.GetMyTodayOneArticleRequest
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


class CalendarFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    lateinit var btnGoToArticle : LinearLayout
    lateinit var btnGoToPostArticle : LinearLayout

    lateinit var userid : String
    lateinit var currentArticleNo : String
    var mNow: Long = 0
    var mDate: Date? = null
    var mFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd") // SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val calendarDayList = ArrayList<CalendarDay>()

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
        getTodayArticle(getTime().toString()) // 포커스 되는 순간, 해당 일자의 글 중 가장 최신 글을 btn_goToArticle 영역에 띄우게
        decorateRecorded(CalendarDay.today())
        binding.layoutTodayRecord.removeAllViews()
        binding.layoutTodayRecord.addView(binding.btnGoToArticle)

        binding.calendarView.state().edit()
            .setFirstDayOfWeek(Calendar.SUNDAY)
            .setCalendarDisplayMode(CalendarMode.MONTHS)
            .commit()
        binding.calendarView.addDecorators(
            SundayDecorator(),
            SaturdayDecorator()
        )
//        binding.calendarView.setTitleFormatter(MonthArrayTitleFormatter(resources.getTextArray(R.array.custom_months)))
        binding.calendarView.setWeekDayFormatter(ArrayWeekDayFormatter(resources.getTextArray(R.array.custom_weekdays)))
        binding.calendarView.setOnDateChangedListener { widget, date, selected ->
            // 포커스 되는 순간, 해당 일자의 글 중 가장 최신 글을 btn_goToArticle 영역에 띄우게 2
            val selectedDate = getDateInMySQLFormat(binding.calendarView.selectedDate)
            getTodayArticle(selectedDate)
        }
        binding.calendarView.setOnMonthChangedListener { widget, date ->
            Handler().postDelayed(Runnable {
                // 바로 실행하니까, 슬라이드 되기 전의 title을 반환함
                val f = binding.calendarView.javaClass.getDeclaredField("title")
                f.isAccessible = true
                val titleTextView = f.get(widget) as TextView
                var month = titleTextView.text.replace("[^a-zA-Z]".toRegex(), "") // 현재 title에는 월+년도 정보가 같이 있는데, 거기서 숫자인 년도 정보는 쳐내고 월 정보만 가져옴
                val year = titleTextView.text.replace("[^0-9]".toRegex(), "")

                when (month) {
                    "January" -> month = "01"
                    "February" -> month = "02"
                    "March" -> month = "03"
                    "April" -> month = "04"
                    "May" -> month = "05"
                    "June" -> month = "06"
                    "July" -> month = "07"
                    "August" -> month = "08"
                    "September" -> month = "09"
                    "October" -> month = "10"
                    "November" -> month = "11"
                    "December" -> month = "12"
                }

                val monthAreLookingAt = "${year}-${month}-01"
                decorateRecorded(monthAreLookingAt)
            }, 200)
        }

        btnGoToArticle = view.findViewById(R.id.btn_goToArticle)
    }

    override fun onStart() {
        super.onStart()
        btnGoToArticle.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_goToArticle -> {
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
        Handler().postDelayed(Runnable {
            binding.tvGuide.text = "${userid}님의 ${currentDate} 기록"

            val responseListener: Response.Listener<String> =
                Response.Listener { response ->
                    try {
                        val jsonObject = JSONObject(response)
                        val success = jsonObject.getBoolean("success")
                        if (success) {
                            binding.layoutTodayRecord.removeViewAt(0)
                            binding.layoutTodayRecord.addView(binding.btnGoToArticle)

                            val user_profile = BitmapConverter.stringToBitmap(jsonObject.getString("user_profile") ?: "")
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
                            binding.layoutTodayRecord.removeViewAt(0)
                            binding.layoutTodayRecord.addView(binding.btnGoToPostArticle) // 오늘은 작성한 게시글이 없네요~ 작성하러 가보시겠어요? 하는 버튼을 대신 생성
                            binding.btnGoToPostArticle.setOnClickListener {
                                val intent = Intent(activity, CameraActivity::class.java)
                                startActivity(intent)
                            }

                            binding.tvRegAt.text = ""
                            binding.tvContent.text = ""
                            val article_image = BitmapConverter.stringToBitmap("")
                            binding.ivContent.setImageBitmap(article_image)
                            binding.tvComment.text = "0"
                            binding.tvFavorite.text = "0"
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
        }, 1000)
    }

    private fun getDateInMySQLFormat(selectedDate: CalendarDay): String {
        val tempYear = selectedDate.year.toString()
        var tempMonth = (selectedDate.month + 1).toString() // 왜인지 모르겠지만, 10월에서 선택하면 9월로, 9월에서 선택하면 8월 이런식으로 나옴
        var tempDay = selectedDate.day.toString()

        if (tempMonth.length < 2) {
            // 9월이면 09, 10월이면 10 그대로
            tempMonth = "0" + tempMonth
        }
        if (tempDay.length < 2) {
            tempDay = "0" + tempDay
        }

        val thisDayDate = "${tempYear}-${tempMonth}-${tempDay}"
        return thisDayDate
    }

    private fun decorateRecorded(currentDate: Any) {
        // 현재 캘린더가 보여주고 있는 달..에서, 기록이 있는 날짜들에 색다른 표시를 해 둠
        val yearAndMonth = ""
        if (currentDate.javaClass.name == "CalendarDay") {
            val date = getDateInMySQLFormat(currentDate as CalendarDay)
            date.substring(0, 7)
        } else if (currentDate.javaClass.name == "String") {
            currentDate as String
        }

        val responseListener: Response.Listener<String?> =
            Response.Listener<String?> { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val success = jsonResponse.getBoolean("success")
                    val results = jsonResponse.getJSONArray("result")
                    if(success) {
                        for(i in 0 until results.length()) {
                            val aRecordDay : String
                            results.getJSONObject(i).apply {
                                aRecordDay = getString("published")
                            }
                            val tempYear = aRecordDay.substring(0, 4).toInt()
                            val tempMonth = aRecordDay.substring(5, 7).toInt()
                            val tempDay = aRecordDay.substring(8, 10).toInt()

                            calendarDayList.add(CalendarDay.from(tempYear, tempMonth - 1, tempDay))
                        }

                        val eventDecorator = EventDecorator(calendarDayList, activity)
                        binding.calendarView.addDecorators(eventDecorator)
                    } else {
                        return@Listener
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        val getMyArticlesThisMonthRequest = GetMyArticlesThisMonthRequest("$yearAndMonth%", userid, responseListener)
        val queue = Volley.newRequestQueue(activity)
        queue.add(getMyArticlesThisMonthRequest)
    }
}