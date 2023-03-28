package com.example.klekle

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.klekle.auth.LoginActivity

class StartActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    var loginedId: String? = null
    var loginedPw: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        // 자동 로그인
        val sharedPreferences = getSharedPreferences("login_info", MODE_PRIVATE)
        loginedId = sharedPreferences.getString("loginedId", null)
//        loginedPw = sharedPreferences.getString("loginedPw", null)

        if (loginedId != null) { // 전에 로그인 한 기록이 있을 시
            Handler().postDelayed(Runnable {
                val intent = Intent(this@StartActivity, MainActivity::class.java)
                finishAffinity()
                startActivity(intent)
            }, 2000)
        }
        else { // 전에 로그인 한 적이 없거나, 로그아웃 해서 기록이 사라졌을 시
            Handler().postDelayed(Runnable {
                val intent = Intent(this@StartActivity, LoginActivity::class.java)
                finishAffinity()
                startActivity(intent)
            }, 2000)
        }
    }
}