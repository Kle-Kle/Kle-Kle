package com.example.klekle

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.example.klekle.databinding.ActivityWritePostBinding
import com.example.klekle.model.ArticleModel
import com.example.klekle.util.GetMyArticleListRequest
import com.example.klekle.util.PostArticleRequest
import org.jetbrains.annotations.Nullable
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException


class WritePostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWritePostBinding
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var bit : Bitmap
    private lateinit var base64bitmap : String
    private var flagThereIsImage : Boolean = false // 이미지를 업로드 하지 않고도, 게시글 작성을 처리할 수 있도록

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_post)

        binding = ActivityWritePostBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.camera.setOnClickListener(View.OnClickListener {
            openGallery()
        })

        binding.btnPost.setOnClickListener {
            base64bitmap = if (flagThereIsImage) {
                // ByteArrayOutputStream을 사용하여 Bitmap을 ByteArray로 변환
                val byteArrayOutputStream = ByteArrayOutputStream()
                bit.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()

                // ByteArray를 Base64로 인코딩
                val base64String = Base64.encodeToString(byteArray, Base64.DEFAULT)

                base64String
            } else {
                ""
            }
            PostArticle()
        }

    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            // 선택한 이미지의 URI 가져오기
            val selectedImageUri = data.data

            try {
                // URI에서 비트맵 이미지로 변환
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImageUri)
                bit = bitmap;
                // ImageView에 이미지 설정
                binding.imageView.setImageBitmap(bitmap)

                flagThereIsImage = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun PostArticle() {

        val sharedPreferences = getSharedPreferences("login_info", Activity.MODE_PRIVATE)
        val userid = sharedPreferences.getString("loginedId", null)

        val responseListener: Response.Listener<String?> =
            Response.Listener<String?> { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val success = jsonResponse.getBoolean("success")
                    if(success) {
                        intent.putExtra("memo", "update")
                        setResult(RESULT_OK, intent);
                        this.finish();
                    } else {
                        Toast.makeText(this,"업로드 실패",Toast.LENGTH_LONG);
                        return@Listener
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        val loginRequest = PostArticleRequest(base64bitmap,userid,binding.content.text.toString() ,responseListener)
        val queue = Volley.newRequestQueue(this)
        queue.add(loginRequest)
    }

}