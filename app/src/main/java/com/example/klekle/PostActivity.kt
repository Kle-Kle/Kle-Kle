package com.example.klekle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.klekle.databinding.ActivityPostBinding

class PostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        binding = ActivityPostBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnPost.setOnClickListener {
            val intent = Intent(this, ArticleActivity::class.java)
            finish()
            startActivity(intent)
        }
    }
}