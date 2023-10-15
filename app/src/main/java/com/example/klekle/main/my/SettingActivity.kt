package com.example.klekle.main.my

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.example.klekle.R
import com.example.klekle.auth.LoginActivity
import com.example.klekle.databinding.ActivitySettingBinding
import com.example.klekle.util.DeleteUserRequest
import org.json.JSONException
import org.json.JSONObject

class SettingActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivitySettingBinding
    lateinit var sharedPreferences: SharedPreferences

    lateinit var btnSettingNotification : Button
    lateinit var btnSettingTheme : Button
    lateinit var btnDeleteUser : Button

    // 회원 탈퇴 다이얼로그
    lateinit var alertDialog : AlertDialog
    lateinit var builder : AlertDialog.Builder
    lateinit var dialogDeleteUser : View

    lateinit var userid : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        binding = ActivitySettingBinding.inflate(layoutInflater)

        btnSettingNotification = findViewById(R.id.btn_setting_notification)
        btnSettingTheme = findViewById(R.id.btn_setting_theme)
        btnDeleteUser = findViewById(R.id.btn_delete_user)

        sharedPreferences = this.getSharedPreferences("login_info", AppCompatActivity.MODE_PRIVATE)!!
        userid = sharedPreferences.getString("loginedId", null).toString()
    }

    override fun onStart() {
        super.onStart()
        btnSettingNotification.setOnClickListener(this)
        btnSettingTheme.setOnClickListener(this)
        btnDeleteUser.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_setting_notification -> {
                // todo
                Log.d("TEST:D", "알림 설정")
            }
            R.id.btn_setting_theme -> {
                // todo
                Log.d("TEST:D", "테마")
            }
            R.id.btn_delete_user -> {
                // 회원 탈퇴
                Log.d("TEST:D", "회원 탈퇴")
                deleteUser()
            }
        }
    }

    private fun deleteUser() {
        val inflater = this.layoutInflater;

        try{
            dialogDeleteUser = inflater.inflate(R.layout.dialog_delete_user, null)
            val btn_deleteYes = dialogDeleteUser.findViewById<Button>(R.id.btn_delete_yes)
            val btn_deleteNo = dialogDeleteUser.findViewById<Button>(R.id.btn_delete_no)

            builder = AlertDialog.Builder(this)
            builder.setView(dialogDeleteUser)

            btn_deleteYes.setOnClickListener {
                val responseListener: Response.Listener<String> =
                    Response.Listener { response ->
                        try {
                            val jasonObject = JSONObject(response)
                            val success = jasonObject.getBoolean("success")
                            if (success) { // 수정에 성공한 경우
                                clearSharedPreferenceAndMoveToStartPage() // 로그아웃과 동일하게, 로컬 데이터 삭제 후 시작 화면으로 이동 시킴
                                Toast.makeText(this, "회원 탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "회원 탈퇴에 실패했습니다.\n잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                val deleteUserRequest =
                    DeleteUserRequest(userid, responseListener)
                val queue = Volley.newRequestQueue(this)
                queue.add(deleteUserRequest)
            }
            btn_deleteNo.setOnClickListener {
                try {
                    alertDialog.dismiss()
                }
                catch (e : Exception){
                    e.printStackTrace()
                }
            }
            alertDialog = builder.create()
            try {
                alertDialog.show()
            }
            catch (e : Exception){
                e.printStackTrace()
            }
        }
        catch(e : Exception){
            e.printStackTrace()
        }
    }

    private fun clearSharedPreferenceAndMoveToStartPage() {
        val editor = sharedPreferences.edit()
        editor?.clear()
        editor?.apply()

        val intent = Intent(this, LoginActivity::class.java)
        this.finishAffinity()
        startActivity(intent)
    }
}