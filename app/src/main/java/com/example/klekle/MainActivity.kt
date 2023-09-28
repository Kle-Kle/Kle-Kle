package com.example.klekle

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import com.example.klekle.databinding.ActivityRegisterBinding
import com.example.klekle.main.CalendarFragment
import com.example.klekle.main.HomeFragment
import com.example.klekle.main.MypageFragment
import com.example.klekle.main.NoticeFragment
import com.example.klekle.main.newhome.NewHomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private val fragmentManager = supportFragmentManager
    //private val homeFragment = HomeFragment()
    private val newHomeFragment = NewHomeFragment()
    private val calendarFragment = CalendarFragment()
    private val noticeFragment = NoticeFragment()
    private val mypageFragment = MypageFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        bottomNavigationView.menu.findItem(R.id.home).isChecked = true // set default selection is 'home'

        // 첫 화면 지정
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, newHomeFragment).commitAllowingStateLoss()

        // 알림 badge (임시 고정값)
        showBadge(this, bottomNavigationView, R.id.notice, "4")

        // bottomNavigationView의 아이템이 선택될 때, 호출될 리스너 등록
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            val transaction = fragmentManager.beginTransaction()
            when (item.itemId) {
                R.id.home -> {
                    transaction.replace(R.id.frame_layout, newHomeFragment).commitAllowingStateLoss()
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
}