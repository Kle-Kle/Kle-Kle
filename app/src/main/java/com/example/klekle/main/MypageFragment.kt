package com.example.klekle.main

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.example.klekle.MainActivity
import com.example.klekle.R
import com.example.klekle.auth.LoginActivity
import com.example.klekle.databinding.FragmentMypageBinding
import com.example.klekle.main.my.AppInfoActivity
import com.example.klekle.main.my.SettingActivity
import com.example.klekle.main.my.UpdateBodyActivity
import com.example.klekle.util.UpdateNicknameRequest
import com.example.klekle.main.my.UpdateProfileActivity
import com.example.klekle.main.my.UpdateUserActivity
import com.example.klekle.util.BitmapConverter
import com.example.klekle.util.UpdateStatusMessageRequest
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONException
import org.json.JSONObject

class MypageFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!! // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언

    lateinit var sharedPreferences: SharedPreferences

    // 로그아웃 시 or 닉네임 변경 시에 나타날 팝업
    lateinit var alertDialog : AlertDialog
    lateinit var builder : AlertDialog.Builder
    lateinit var et_newNickname : EditText
    lateinit var view_updateStatusMessage : View

    lateinit var btnUpdateNickname : TextView
    lateinit var btnUpdateStatusMessage : TextView
    lateinit var btnGoToUpdateProfile : CircleImageView
    lateinit var btnGoToUpdateUser : LinearLayout
    lateinit var btnGoToUpdateBody : LinearLayout
    lateinit var btnGoToSetting : LinearLayout
    lateinit var btnGoToAppInfo : LinearLayout
    lateinit var btnLogout : LinearLayout

    lateinit var userid : String
    lateinit var nickname : String
    lateinit var statusMessage : String
    lateinit var image : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMypageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sharedPreferences = activity?.getSharedPreferences("login_info", AppCompatActivity.MODE_PRIVATE)!!
        userid = sharedPreferences.getString("loginedId", null).toString()
        nickname = sharedPreferences.getString("nickname", null).toString()
        statusMessage = sharedPreferences.getString("statusMessage", null).toString()
        image = sharedPreferences.getString("profile", null).toString()

        // 기존 아이디 정보 가져옴
        binding.tvUserid.text = userid
        // 기존 닉네임 정보 가져욤
        binding.tvNickname.text = nickname
        // 기존 상태메시지 정보 가져욤
        if (statusMessage == "" || statusMessage == "null") {
            binding.tvStatusMessage.text = "(상태메시지를 설정해 보세요.)"
        }
        else {
            binding.tvStatusMessage.text = statusMessage
        }
        // 기존 프로필 사진 정보 가져옴
        val bm = BitmapConverter.stringToBitmap(image)
        binding.profileImage.setImageBitmap(bm)

        btnUpdateNickname = view.findViewById(R.id.btn_update_nickname)
        btnUpdateStatusMessage = view.findViewById(R.id.btn_update_statusMessage)
        btnGoToUpdateProfile = view.findViewById(R.id.btn_goToUpdateProfile)
        btnGoToUpdateUser = view.findViewById(R.id.btn_goToUpdateUser)
        btnGoToUpdateBody = view.findViewById(R.id.btn_goToUpdateBody)
        btnGoToSetting = view.findViewById(R.id.btn_goToSetting)
        btnGoToAppInfo = view.findViewById(R.id.btn_goToAppInfo)
        btnLogout = view.findViewById(R.id.btn_logout)

        // 밑줄로 clickable 임을 표시..
        binding.btnUpdateNickname.paintFlags = Paint.UNDERLINE_TEXT_FLAG // nickname
        binding.btnUpdateStatusMessage.paintFlags = Paint.UNDERLINE_TEXT_FLAG // 상태메시지
    }

    override fun onStart() {
        super.onStart()
        btnUpdateNickname.setOnClickListener(this)
        btnUpdateStatusMessage.setOnClickListener(this)
        btnGoToUpdateProfile.setOnClickListener(this)
        btnGoToUpdateUser.setOnClickListener(this)
        btnGoToUpdateBody.setOnClickListener(this)
        btnGoToSetting.setOnClickListener(this)
        btnGoToAppInfo.setOnClickListener(this)
        btnLogout.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_update_nickname -> {
                updateNickname()
            }
            R.id.btn_update_statusMessage -> {
                updateStatusMessage()
            }
            R.id.btn_goToUpdateProfile -> {
                val intent = Intent(activity, UpdateProfileActivity::class.java)
                startActivity(intent)
            }
            R.id.btn_goToUpdateUser -> {
                val intent = Intent(activity, UpdateUserActivity::class.java)
                startActivity(intent)
            }
            R.id.btn_goToUpdateBody -> {
                val intent = Intent(activity, UpdateBodyActivity::class.java)
                startActivity(intent)
            }
            R.id.btn_goToSetting -> {
                val intent = Intent(activity, SettingActivity::class.java)
                startActivity(intent)
            }
            R.id.btn_goToAppInfo -> {
                val intent = Intent(activity, AppInfoActivity::class.java)
                startActivity(intent)
            }
            R.id.btn_logout -> {
                logout()
            }
        }
    }

    private fun updateNickname() {
        try{
            val str_tittle = "닉네임 변경"
            val str_message = "새 닉네임을 입력해 주세요."
            val str_buttonOK = "변경"
            val str_buttonNO = "취소"
            et_newNickname = EditText(activity)

            builder = AlertDialog.Builder(requireActivity())
            builder.setTitle(str_tittle) // 팝업창 타이틀 지정
            builder.setMessage(str_message) // 팝업창 내용 지정
            builder.setView(et_newNickname)

            builder.setPositiveButton(str_buttonOK, DialogInterface.OnClickListener { dialog, which ->
                val newNickname : String = et_newNickname.text.toString()
                val responseListener: Response.Listener<String> =
                    Response.Listener { response ->
                        try {
                            val jasonObject = JSONObject(response)
                            val success = jasonObject.getBoolean("success")
                            if (success) { // 수정에 성공한 경우
                                val sharedPreferences: SharedPreferences =
                                    requireActivity().getSharedPreferences("login_info", Activity.MODE_PRIVATE)
                                val autoLogin = sharedPreferences.edit()
                                autoLogin.putString("nickname", newNickname) // shared preference 에 nickname 새로 저장
                                autoLogin.apply()

                                val intent = Intent(activity, MainActivity::class.java) // 닉네임 변경 후, 페이지 새로고침이 되어야 변경 된 닉네임이 새롭게 보일 것 같으므론
                                activity?.finishAffinity()
                                startActivity(intent)

                                Toast.makeText(activity, "닉네임이 변경되었습니다.", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(activity, "닉네임 변경에 실패했습니다.\n사용할 수 없는 닉네임입니다.", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                val updateNicknameRequest =
                    UpdateNicknameRequest(newNickname, userid, responseListener)
                val queue = Volley.newRequestQueue(activity)
                queue.add(updateNicknameRequest)
            })
            builder.setNegativeButton(str_buttonNO, DialogInterface.OnClickListener { dialog, which ->
                try {
                    alertDialog.dismiss()
                }
                catch (e : Exception){
                    e.printStackTrace()
                }
            })
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

    private fun updateStatusMessage() {
        val inflater = requireActivity().layoutInflater;
        var currentCharCount = statusMessage.length // 기존 상태메시지의 길이

        try{
            val str_tittle = "상태메시지 변경"
            val str_message = "상태메시지는 최대 280자까지 입력 가능합니다."
            val str_buttonOK = "변경"
            val str_buttonNO = "취소"

            view_updateStatusMessage = inflater.inflate(R.layout.dialog_update_status_message, null)
            val et_newStatusMessage = view_updateStatusMessage.findViewById<EditText>(R.id.et_newStatusMessage)
            val tv_currentCharCount = view_updateStatusMessage.findViewById<TextView>(R.id.tv_currentCharCount)

            et_newStatusMessage.setText(statusMessage)  // 기존 상태메시지 불러옴
            tv_currentCharCount.text = "($currentCharCount/280)"

            et_newStatusMessage.addTextChangedListener {
                currentCharCount = et_newStatusMessage.length()
                tv_currentCharCount.text = "($currentCharCount/280)"
            }

            builder = AlertDialog.Builder(requireActivity())
            builder.setTitle(str_tittle) // 팝업창 타이틀 지정
            builder.setMessage(str_message) // 팝업창 내용 지정
            builder.setView(view_updateStatusMessage)

            builder.setPositiveButton(str_buttonOK, DialogInterface.OnClickListener { dialog, which ->
                val newStatusMessage : String = et_newStatusMessage.text.toString()
                val responseListener: Response.Listener<String> =
                    Response.Listener { response ->
                        try {
                            val jasonObject = JSONObject(response)
                            val success = jasonObject.getBoolean("success")
                            if (success) { // 수정에 성공한 경우
                                val sharedPreferences: SharedPreferences =
                                    requireActivity().getSharedPreferences("login_info", Activity.MODE_PRIVATE)
                                val autoLogin = sharedPreferences.edit()
                                autoLogin.putString("statusMessage", newStatusMessage) // shared preference 에 nickname 새로 저장
                                autoLogin.apply()

                                val intent = Intent(activity, MainActivity::class.java) // 닉네임 변경 후, 페이지 새로고침이 되어야 변경 된 닉네임이 새롭게 보일 것 같으므론
                                activity?.finishAffinity()
                                startActivity(intent)

                                Toast.makeText(activity, "상태메시지가 변경되었습니다.", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(activity, "상태메시지 변경에 실패했습니다.\n잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                val updateStatusMessageRequest =
                    UpdateStatusMessageRequest(newStatusMessage, userid, responseListener)
                val queue = Volley.newRequestQueue(activity)
                queue.add(updateStatusMessageRequest)
            })
            builder.setNegativeButton(str_buttonNO, DialogInterface.OnClickListener { dialog, which ->
                try {
                    alertDialog.dismiss()
                }
                catch (e : Exception){
                    e.printStackTrace()
                }
            })
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

    private fun logout() {
        try{
            val str_tittle = "로그아웃"
            val str_message = "정말 로그아웃 하시겠습니까?"
            val str_buttonOK = "확인"
            val str_buttonNO = "취소"
//                var str_buttonNature = "냠"

            builder = AlertDialog.Builder(requireActivity())
            builder.setTitle(str_tittle) // 팝업창 타이틀 지정
            builder.setMessage(str_message) // 팝업창 내용 지정
//                builder.setCancelable(false) // 외부 레이아웃을 클릭해도 팝업창이 사라지지 않게 설정
            builder.setPositiveButton(str_buttonOK, DialogInterface.OnClickListener { dialog, which ->
                var editor = sharedPreferences?.edit()
                editor?.clear()
                editor?.apply()

                val intent = Intent(activity, LoginActivity::class.java)
                activity?.finishAffinity()
                startActivity(intent)
            })
            builder.setNegativeButton(str_buttonNO, DialogInterface.OnClickListener { dialog, which ->
                try {
                    alertDialog.dismiss()
                }
                catch (e : Exception){
                    e.printStackTrace()
                }
            })
//                builder.setNeutralButton(str_buttonNature, DialogInterface.OnClickListener { dialog, which ->
//                    // Neutural Button 입니다..
//                })
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}