package com.example.klekle.main.my

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.klekle.MainActivity
import com.example.klekle.R
import com.example.klekle.databinding.ActivityUpdateProfileBinding
import com.example.klekle.util.BitmapConverter
import com.example.klekle.util.UpdateProfileRequest
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class UpdateProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateProfileBinding
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)
        binding = ActivityUpdateProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // 툴바 생성
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생성

        val sharedPreferences = getSharedPreferences("login_info", Activity.MODE_PRIVATE)
        val userid = sharedPreferences.getString("loginedId", null)
        val image = sharedPreferences.getString("profile", null)

        if (image != null) {
            // 기존 프로필 이미지 정보 가져옴
            val bm = BitmapConverter.stringToBitmap(image)
            binding.profileImage.setImageBitmap(bm)
        }

        binding.btnUpdateProfile.isEnabled = false // click listner가 작성되었다는 시점에서 자동으로 clickable이 true로 설정됨.. 따라서 setClickable(false) 대신 이렇게 설정
        binding.profileImage.setOnClickListener {
            // 갤러리 호출
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            activityResult.launch(intent)
        }

        // 완료 버튼 이벤트
        binding.btnUpdateProfile.setOnClickListener {
            val img : Bitmap = binding.profileImage.drawable.toBitmap()
            var newImage = ""

            val baos = ByteArrayOutputStream()
            img.compress(Bitmap.CompressFormat.PNG, 50, baos)
            val bytes = baos.toByteArray()
            newImage = Base64.encodeToString(bytes, Base64.DEFAULT)

            val responseListener: Response.Listener<String> =
                Response.Listener { response ->
                    try {
                        val jasonObject = JSONObject(response)
                        val success = jasonObject.getBoolean("success")
                        if (success) { // 변경에 성공한 경우
                            val autoLogin = sharedPreferences.edit()
                            autoLogin.putString("profile", newImage) // shared preference 에 image(string) 새로 저장
                            autoLogin.apply()

                            val intent = Intent(this, MainActivity::class.java)
                            this.finishAffinity()
                            startActivity(intent)

                            Toast.makeText(this, "프로필 사진이 변경되었습니다.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "프로필 사진 변경에 실패했습니다.\n잠시 뒤에 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            val updateProfileRequest =
                UpdateProfileRequest(newImage, userid, responseListener)
            val queue = Volley.newRequestQueue(this)
            queue.add(updateProfileRequest)
        }
    }

    // 이미지 업로드 결과 가져오기
    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){

        // 결과 코드 OK, 결가값 null 아니면
        if(it.resultCode == RESULT_OK && it.data != null){
            //값 담기
            val uri  = it.data!!.data

            // 화면에 보여주기
            Glide.with(this)
                .load(uri) //이미지
                .into(binding.profileImage) //보여줄 위치
        }

        binding.btnUpdateProfile.isEnabled = true
        binding.btnUpdateProfile.setTextColor(ContextCompat.getColor(this, R.color.primary_600))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // toolbar의 back키 눌렀을 때 동작
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}