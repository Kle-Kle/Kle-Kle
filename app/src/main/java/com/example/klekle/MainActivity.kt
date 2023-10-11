package com.example.klekle

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.example.klekle.databinding.ActivityRegisterBinding
import com.example.klekle.main.CalendarFragment
import com.example.klekle.main.HomeFragment
import com.example.klekle.main.MypageFragment
import com.example.klekle.main.NoticeFragment
import com.example.klekle.util.DeleteArticleRequest
import com.example.klekle.util.GetCountOfNotificationsRequest
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONException
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { ok ->
            if (ok) {
                // 알림권한 허용 o
            } else {
                // 알림권한 허용 x. 자유롭게 대응..
            }
        }

    private val fragmentManager = supportFragmentManager
    private val homeFragment = HomeFragment()
    private val calendarFragment = CalendarFragment()
    private val noticeFragment = NoticeFragment()
    private val mypageFragment = MypageFragment()

    private lateinit var bottomNavigationView : BottomNavigationView

    private var countOfNotifications : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        bottomNavigationView.menu.findItem(R.id.home).isChecked = true // set default selection is 'home'

        // 첫 화면 지정
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, homeFragment).commitAllowingStateLoss()

        // 알림 badge
        getAndSetCountOfNotifications()
        requestNotificationPermission() // 알림 권한 허가 묻기

        // bottomNavigationView의 아이템이 선택될 때, 호출될 리스너 등록
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            val transaction = fragmentManager.beginTransaction()
            when (item.itemId) {
                R.id.home -> {
                    transaction.replace(R.id.frame_layout, homeFragment).commitAllowingStateLoss()
                }
                R.id.calendar -> {
                    transaction.replace(R.id.frame_layout, calendarFragment).commitAllowingStateLoss()
                }
                R.id.notice -> {
                    transaction.replace(R.id.frame_layout, noticeFragment).commitAllowingStateLoss()
                }
                R.id.user -> {
                    transaction.replace(R.id.frame_layout, mypageFragment).commitAllowingStateLoss()
                }
            }
            true
        }
    }

    override fun onStop() {
        super.onStop()
        getAndSetCountOfNotifications()
    }

    private fun getAndSetCountOfNotifications() {
        val sharedPreferences = getSharedPreferences("login_info", AppCompatActivity.MODE_PRIVATE)!!
        val loginedId = sharedPreferences.getString("loginedId", null).toString()

        val responseListener: Response.Listener<String?> =
            Response.Listener<String?> { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    countOfNotifications = jsonResponse.getInt("count")

                    if (countOfNotifications > 0) {
                        showBadge(this, bottomNavigationView, R.id.notice, "$countOfNotifications")
                    }
                    else {
                        removeBadge(bottomNavigationView, R.id.notice)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        val rqst = GetCountOfNotificationsRequest(loginedId, responseListener)
        val queue = Volley.newRequestQueue(this)
        queue.add(rqst)
    }

    fun showBadge(
        context: Context?,
        bottomNavigationView: BottomNavigationView,
        @IdRes itemId: Int,
        value: String?
    ) {
        removeBadge(bottomNavigationView, itemId)
        val itemView = bottomNavigationView.findViewById<BottomNavigationItemView>(itemId)
        val badge: View = LayoutInflater.from(context)
            .inflate(R.layout.notification_badge, bottomNavigationView, false)
        val text = badge.findViewById<TextView>(R.id.notifications_badge)
        text.text = value
        itemView.addView(badge)
    }

    fun removeBadge(bottomNavigationView: BottomNavigationView, @IdRes itemId: Int) {
        val itemView = bottomNavigationView.findViewById<BottomNavigationItemView>(itemId)
        if (itemView.childCount == 3) {
            itemView.removeViewAt(2)
        }
    }

    fun requestNotificationPermission() {
        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // 다른 런타임 퍼미션이랑 비슷한 과정
                if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                    // 왜 알림을 허용해야하는지 유저에게 알려주기를 권장
                } else {
                    requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            } else {
                // 안드로이드 12 이하는 알림에 런타임 퍼미션 없으니, 설정가서 켜보라고 권해볼 수 있겠다.
            }
        }
    }
}