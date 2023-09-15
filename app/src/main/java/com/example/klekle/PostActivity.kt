package com.example.klekle

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.toolbox.Volley
import com.example.klekle.databinding.ActivityPostBinding
import com.example.klekle.util.BitmapConverter
import com.example.klekle.util.DetectHoldRequest
import org.json.JSONException
import org.json.JSONObject

class PostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        binding = ActivityPostBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        sharedPreferences = getSharedPreferences("tempData", Activity.MODE_PRIVATE)
        val wallImage = sharedPreferences.getString("tempWallImage", null)
        val bitmapD = BitmapConverter.stringToBitmap(wallImage)
        binding.ivInputImage.setImageBitmap(bitmapD)

        detectHoldRequest(wallImage!!)

        binding.btnPost.setOnClickListener {
            val intent = Intent(this, ArticleActivity::class.java)
            finish()
            startActivity(intent)
        }
    }

    private fun detectHoldRequest(wallImage: String) {
        val responseListener: com.android.volley.Response.Listener<String> =
            com.android.volley.Response.Listener { response ->
                try {
                    val jsonObject = JSONObject(response)
                    val success = jsonObject.getBoolean("success")
                    val result = jsonObject.getJSONArray("results")
                    if (success) { // 변경에 성공한 경우
                        Log.d("D:Test", "$result")
                        Log.d("D:Test", "${result[0]}")
                    } else {
                        Toast.makeText(this, "서버와 통신에 실패했습니다.\n잠시 뒤에 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        val detectHoldRequest =
            DetectHoldRequest(wallImage, responseListener)
        val queue = Volley.newRequestQueue(this)
        queue.add(detectHoldRequest)
    }
}