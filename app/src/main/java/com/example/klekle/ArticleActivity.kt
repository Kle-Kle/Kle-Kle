package com.example.klekle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.klekle.databinding.ActivityArticleBinding
import com.example.klekle.databinding.FragmentHomeBinding

class ArticleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityArticleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)
        binding = ActivityArticleBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.commentProfileImage.setOnClickListener {
            val intent = Intent(this, UserPageActivity::class.java)
            startActivity(intent)
        }
    }
}