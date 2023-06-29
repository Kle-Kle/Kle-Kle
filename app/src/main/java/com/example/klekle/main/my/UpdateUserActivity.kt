package com.example.klekle.main.my

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.example.klekle.R
import org.json.JSONException
import org.json.JSONObject

class UpdateUserActivity : AppCompatActivity() {
    private val toolbar: Toolbar? = null
    private lateinit var inputEmail: EditText
    private lateinit var inputPassword: EditText
    private lateinit var inputPwCheck: EditText
    private var btnAmend: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_user)

        // 툴바 생성
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생성

        inputEmail = findViewById(R.id.input_email)
        inputPassword = findViewById(R.id.input_pw)
        inputPwCheck = findViewById(R.id.input_pw_check)

        // 현재 로그인 중인 사용자의 ID
        val sharedPreferences = getSharedPreferences("login_info", MODE_PRIVATE)
        val userid = sharedPreferences.getString("loginedId", null)

        // 기존에 저장돼 있던 계정 정보를 불러옴
        val responseListener: Response.Listener<String> =
            Response.Listener { response ->
                try {
                    val jsonObject = JSONObject(response)
                    val success = jsonObject.getBoolean("success")
                    if (success) {
                        // Database에 저장되어 있던, 사용자의 계정 정보 가져오기에 성공
                        inputEmail.setText(jsonObject.getString("email"))
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "계정 정보 불러오기에 실패했습니다.\n잠시 뒤에 다시 시도해 주세요.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        val getEmailRequest = GetEmailRequest(userid, responseListener)
        val queue = Volley.newRequestQueue(this@UpdateUserActivity)
        queue.add(getEmailRequest)

        // '수정' 버튼 클릭 시 실행되는 부분
        btnAmend = findViewById<View>(R.id.btn_amend) as Button
        btnAmend!!.setOnClickListener {
            val email = inputEmail.getText().toString()
            val userpw = inputPassword.getText().toString()
            val userpwch = inputPwCheck.getText().toString()
            val responseListener: Response.Listener<String> =
                Response.Listener { response ->
                    try {
                        val jasonObject = JSONObject(response)
                        val success = jasonObject.getBoolean("success")
                        if (success) { // 정보 수정에 성공한 경우
                            Toast.makeText(
                                applicationContext,
                                "정보가 수정되었습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "정보 수정에 실패했습니다.\n잠시 뒤에 다시 시도해 주세요.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            val updateUserRequest =
                UpdateUserRequest(userid, email, userpw, responseListener)
            val queue = Volley.newRequestQueue(this@UpdateUserActivity)
            queue.add(updateUserRequest)
        }
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