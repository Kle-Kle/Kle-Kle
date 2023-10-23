package com.example.klekle

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.core.widget.addTextChangedListener
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.example.klekle.databinding.ActivityWritePostBinding
import com.example.klekle.util.BitmapConverter
import com.example.klekle.util.PostArticleRequest
import com.example.klekle.util.UpdateArticleRequest
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream


class WritePostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWritePostBinding
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var bit : Bitmap
    private lateinit var base64bitmap : String
    private var flagThereIsImage : Boolean = false // 이미지를 업로드 하지 않고도, 게시글 작성을 처리할 수 있도록

    private var mode : String = "" // 모드: '게시글 수정하기'를 통해 입장했을 경우, mode가 'update'로 셋팅
    private lateinit var existingArticleNo : String
    private lateinit var existingArticleImage : Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_post)

        binding = ActivityWritePostBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val intent = intent // 해당 페이지로 넘어오기 전에, 선택한 글의 article no를 넘겨 받음
        mode = intent.getStringExtra("mode").toString()
        if (mode == "updateIsImage") {
            // 해당 게시글의 기존 내용 불러오기
            binding.content.setText(intent.getStringExtra("content").toString())
            existingArticleNo = intent.getStringExtra("article_no").toString()

            val sharedPreferences = getSharedPreferences("tempData", Activity.MODE_PRIVATE) // 이미지가 너무 커서, 이 방식으로 데이터 전송
            val existingArticleImageString = sharedPreferences.getString("existingArticleImage", null)
            existingArticleImage = BitmapConverter.stringToBitmap(existingArticleImageString)
            binding.imageView.setImageBitmap(existingArticleImage)
        }
        else if (mode == "updateIsNotImage") {
            // 해당 게시글의 기존 내용 불러오기
            Log.d("test:d", "mode: $mode")
            binding.content.setText(intent.getStringExtra("content").toString())
            existingArticleNo = intent.getStringExtra("article_no").toString()
        }

        binding.camera.setOnClickListener(View.OnClickListener {
            openGallery()
        })

        binding.btnPost.setOnClickListener {
            base64bitmap = if (flagThereIsImage) {
                // ByteArrayOutputStream을 사용하여 Bitmap을 ByteArray로 변환
                val byteArrayOutputStream = ByteArrayOutputStream()
                if (mode == "updateIsImage") existingArticleImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                else bit.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()

                // ByteArray를 Base64로 인코딩
                val base64String = Base64.encodeToString(byteArray, Base64.DEFAULT)

                base64String
            } else {
                ""
            }

            if (mode == "updateIsImage" || mode == "updateIsNotImage") updateArticle()
            else PostArticle()
        }

        // 게시글 본문 내용 글자 수 세기
        binding.content.addTextChangedListener {
            var currentArticleLength = binding.content.length()
            binding.tvCurrentArticleLength.text = "($currentArticleLength/280)"

            if (currentArticleLength == 0) {
                binding.btnPost.isEnabled = false
                binding.btnPost.alpha = 0.5F
            }
            else {
                binding.btnPost.isEnabled = true
                binding.btnPost.alpha = 1F
            }
        }
        binding.btnPost.isEnabled = false // 첫 입장 시 '게시하기' 버튼 비활성화
        binding.tvCurrentArticleLength.text = "(${binding.content.length()}/280)" // 첫 입장 시 글자 수 세팅 - 업데이트 모드로 입장했을 때를 고려..
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
                        Toast.makeText(this,"업로드에 실패하였습니다.\n" +
                                "잠시 뒤 다시 시도해 주세요.",Toast.LENGTH_LONG);
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

    private fun updateArticle() {
        val img : Bitmap = binding.imageView.drawable.toBitmap()
        var newImage = ""

        val baos = ByteArrayOutputStream()
        img.compress(Bitmap.CompressFormat.PNG, 50, baos)
        val bytes = baos.toByteArray()
        newImage = Base64.encodeToString(bytes, Base64.DEFAULT)

        val responseListener: Response.Listener<String?> =
            Response.Listener<String?> { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val success = jsonResponse.getBoolean("success")
                    if(success) {
                        // TODO: 새로고침 이 방법 좀.. 수정하고 싶다
                        // todo: 지금 방법: article adapter에서 '수정하기'를 누르면, community activity가 종료되고 본 페이지로 이동
                        // todo: -> update 후 본 페이지 종료 휴, 다시 community activity를 띄움
                        val intent = Intent(this, CommunityActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(0, 0) // 인텐트 효과 없애기
                        this.finish()
                    } else {
                        Toast.makeText(this,"게시글 수정에 실패하였습니다.\n잠시 뒤 다시 시도해 주세요.",Toast.LENGTH_LONG).show()
                        return@Listener
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        val rqst = UpdateArticleRequest(existingArticleNo, newImage, binding.content.text.toString(), responseListener)
        val queue = Volley.newRequestQueue(this)
        queue.add(rqst)
    }
}