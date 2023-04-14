package com.example.klekle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.klekle.databinding.ActivityCommunityBinding

class CommunityActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommunityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community)
        binding = ActivityCommunityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnGoToArticle.setOnClickListener {
            val intent = Intent(this, ArticleActivity::class.java)
            startActivity(intent)
        }

        binding.articleProfileImage.setOnClickListener {
            val intent = Intent(this, UserPageActivity::class.java)
            startActivity(intent)
        }
    }
}