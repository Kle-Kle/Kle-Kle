package com.example.klekle.main.my

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.example.klekle.R
import com.example.klekle.databinding.ActivityAppInfoBinding

class AppInfoActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityAppInfoBinding

    lateinit var btnTermsAndConditions : Button
    lateinit var btnPrivacyPolicy : Button
    lateinit var btnOpensource : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_info)
        binding = ActivityAppInfoBinding.inflate(layoutInflater)

        btnTermsAndConditions = findViewById(R.id.btn_terms_and_conditions)
        btnPrivacyPolicy = findViewById(R.id.btn_privacy_policy)
        btnOpensource = findViewById(R.id.btn_opensource)
    }

    override fun onStart() {
        super.onStart()
        btnTermsAndConditions.setOnClickListener(this)
        btnPrivacyPolicy.setOnClickListener(this)
        btnOpensource.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_terms_and_conditions -> {
                // todo
            }
            R.id.btn_privacy_policy -> {
                // todo
            }
            R.id.btn_opensource -> {
                // todo
            }
        }
    }
}