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
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.example.klekle.MainActivity
import com.example.klekle.auth.LoginActivity
import com.example.klekle.databinding.FragmentMypageBinding
import com.example.klekle.main.my.AppInfoActivity
import com.example.klekle.main.my.SettingActivity
import com.example.klekle.main.my.UpdateBodyActivity
import com.example.klekle.main.my.UpdateNicknameRequest
import com.example.klekle.main.my.UpdateUserActivity
import org.json.JSONException
import org.json.JSONObject

class MypageFragment : Fragment() {
    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!! // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언

    lateinit var sharedPreferences: SharedPreferences

    // 로그아웃 시 or 닉네임 변경 시에 나타날 팝업
    lateinit var alertDialog : AlertDialog
    lateinit var builder : AlertDialog.Builder
    lateinit var et_newNickname : EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMypageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sharedPreferences = activity?.getSharedPreferences("login_info", AppCompatActivity.MODE_PRIVATE)!!
        val nickname = sharedPreferences.getString("nickname", null)
        binding.tvNickname.setText(nickname)

        binding.btnUpdateNickname.paintFlags = Paint.UNDERLINE_TEXT_FLAG // 밑줄로 clickable 임을 표시..
        binding.btnUpdateNickname.setOnClickListener {
            try{
                var str_tittle = "닉네임 변경"
                var str_message = "새 닉네임을 입력해 주세요."
                var str_buttonOK = "변경"
                var str_buttonNO = "취소"
                et_newNickname = EditText(activity)

                builder = AlertDialog.Builder(requireActivity())
                builder.setTitle(str_tittle) // 팝업창 타이틀 지정
                builder.setMessage(str_message) // 팝업창 내용 지정
                builder.setView(et_newNickname)

                builder.setPositiveButton(str_buttonOK, DialogInterface.OnClickListener { dialog, which ->
                    var newNickname : String = et_newNickname.text.toString()
                    val responseListener: Response.Listener<String> =
                        Response.Listener { response ->
                            try {
                                val jasonObject = JSONObject(response)
                                val success = jasonObject.getBoolean("success")
                                if (success) { // 수정에 성공한 경우
                                    // 한 번이라도 로그인 한 적 있다면 -> 자동 로그인되도록
                                    val sharedPreferences: SharedPreferences =
                                        requireActivity().getSharedPreferences("login_info", Activity.MODE_PRIVATE)
                                    val autoLogin = sharedPreferences.edit()
                                    autoLogin.putString("nickname", newNickname) // shared preference 에 nickname 새로 저장
                                    autoLogin.apply()

                                    val intent = Intent(activity, MainActivity::class.java) // 닉네임 변경 후, 페이지 새로고침이 되어야 변경 된 닉네임이 새롭게 보일 것 같으므론
                                    activity?.finishAffinity()
                                    startActivity(intent)

                                    Toast.makeText(
                                        activity,
                                        "닉네임이 수정되었습니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        activity,
                                        "닉네임 수정에 실패했습니다.\n잠시 후 다시 시도해 주세요.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    val updateNicknameRequest =
                        UpdateNicknameRequest(newNickname, nickname, responseListener)
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

        binding.btnGoToUpdateUser.setOnClickListener {
            val intent = Intent(activity, UpdateUserActivity::class.java)
            startActivity(intent)
        }

        binding.btnGoToUpdateBody.setOnClickListener {
            val intent = Intent(activity, UpdateBodyActivity::class.java)
            startActivity(intent)
        }

        binding.btnGoToSetting.setOnClickListener {
            val intent = Intent(activity, SettingActivity::class.java)
            startActivity(intent)
        }

        binding.btnGoToAppInfo.setOnClickListener {
            val intent = Intent(activity, AppInfoActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogout.setOnClickListener {
            try{
                var str_tittle = "로그아웃"
                var str_message = "정말 로그아웃 하시겠습니까?"
                var str_buttonOK = "확인"
                var str_buttonNO = "취소"
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}