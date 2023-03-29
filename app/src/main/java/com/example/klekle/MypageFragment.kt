package com.example.klekle

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.example.klekle.auth.LoginActivity
import com.example.klekle.databinding.FragmentMypageBinding
import com.google.android.material.snackbar.Snackbar

class MypageFragment : Fragment() {
    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!! // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언

    lateinit var sharedPreferences: SharedPreferences

    // 로그아웃 시, 확인을 위한 팝업
    lateinit var alertDialog : AlertDialog
    lateinit var builder : AlertDialog.Builder

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMypageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sharedPreferences = activity?.getSharedPreferences("login_info", AppCompatActivity.MODE_PRIVATE)

        binding.btnLogout.setOnClickListener {
            try{
                var str_tittle = "로그아웃"
                var str_message = "정말로 로그아웃 하시겠습니까?"
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