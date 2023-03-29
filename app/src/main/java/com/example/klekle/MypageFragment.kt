package com.example.klekle

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.example.klekle.auth.LoginActivity
import com.example.klekle.databinding.FragmentMypageBinding
import com.google.android.material.snackbar.Snackbar

class MypageFragment : Fragment() {
    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!! // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언

    lateinit var sharedPreferences: SharedPreferences

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
            var editor = sharedPreferences?.edit()
            editor?.clear()
            editor?.apply()
            
            val intent = Intent(activity, LoginActivity::class.java)
            activity?.finishAffinity()
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}