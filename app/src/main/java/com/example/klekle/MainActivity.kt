package com.example.klekle

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.klekle.databinding.ActivityRegisterBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private val fragmentManager = supportFragmentManager
    private val homeFragment = HomeFragment()
    private val mypageFragment = MypageFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        bottomNavigationView.menu.findItem(R.id.home).isChecked = true // set default selection is 'home'

        // 첫 화면 지정
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, homeFragment).commitAllowingStateLoss()

        // bottomNavigationView의 아이템이 선택될 때, 호출될 리스너 등록
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            val transaction = fragmentManager.beginTransaction()
            when (item.itemId) {
                R.id.home -> {
                    transaction.replace(R.id.frame_layout, homeFragment).commitAllowingStateLoss()
                }
                R.id.calendar -> {}
                R.id.notice -> {}
                R.id.user -> {
                    transaction.replace(R.id.frame_layout, mypageFragment).commitAllowingStateLoss()
                }
            }
            true
        }
    }
}