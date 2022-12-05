package com.example.klekle.signup

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.example.klekle.R
import com.example.klekle.databinding.FragmentInsertUserInfoBinding
import com.google.android.material.snackbar.Snackbar
import org.json.JSONException
import org.json.JSONObject
import java.util.regex.Pattern

/**
 * A simple [Fragment] subclass.
 * Use the [InsertUserInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InsertUserInfoFragment : Fragment() {
    private var _binding: FragmentInsertUserInfoBinding? = null

    private val binding get() = _binding!!

    private var userid: String? = null
    private var email: String? = null
    private var userpw: String? = null
    private var userpwch: String? = null
    private var nickname: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View { _binding = FragmentInsertUserInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // binding.joinId.requestFocus()

        val pattern = Patterns.EMAIL_ADDRESS

        binding.btnNext.setOnClickListener {
            userid = binding.joinId.text.toString()
            email = binding.joinEmail.text.toString()
            userpw = binding.joinPw.text.toString()
            userpwch = binding.joinPwCheck.text.toString()
            nickname = binding.joinNickname.text.toString()

            if (isRegularId(userid!!)) {
                binding.errorId.text = "" // 안내 메세지 숨김
            }
            else {
                binding.errorId.text = "2자 이상의 영어 소문자\n또는 숫자의 조합으로 작성해 주세요."
            }

            if (isRegularPw(userpw!!)) {
                binding.errorPw.text = "" // 안내 메세지 숨김
            }
            else {
                binding.errorPw.text = "영문 대/소문자, 숫자, 특수문자(!, ?, *, @, #, \$, %,\n^, &, +, =)를 포함하여 8자 이상 작성해 주세요."
            }

            if (userpw.equals(userpwch)) {
                binding.errorPwch.text = ""
            }
            else {
                binding.errorPwch.text = "비밀번호가 일치하지 않습니다."
                // Snackbar.make(view, "비밀번호가 비밀번호 확인란과 일치하지 않습니다.", Snackbar.LENGTH_LONG).show();
            }

            if (nickname.equals("")) {
                binding.errorNick.text = "닉네임을 설정해 주세요."
                // Snackbar.make(view, "닉네임을 설정해 주세요.", Snackbar.LENGTH_SHORT).show();
            }
            else {
                binding.errorNick.text = "" // 안내 메세지 숨김
            }

            if (email.equals("") || pattern.matcher(email).matches()) {
                binding.errorEmail.text = "잊어버린 계정을 되찾을 때 필요합니다."
            }
            else {
                binding.errorEmail.text = "올바른 이메일 형식이 아닙니다."
                // Snackbar.make(view, "올바른 이메일 형식이 아닙니다.", Snackbar.LENGTH_SHORT).show();
            }

            if (binding.errorId.text.equals("") && binding.errorPw.text.equals("") && binding.errorPwch.text.equals("") && binding.errorEmail.text.equals("잊어버린 계정을 되찾을 때 필요합니다.")) {
                val responseListener: Response.Listener<String> =
                    Response.Listener { response ->
                        try {
                            val jsonResponse = JSONObject(response)
                            val success = jsonResponse.getBoolean("success")
                            if (success) {
                                val responseListener2: Response.Listener<String> =
                                    Response.Listener { response ->
                                        try {
                                            val jsonResponse2 = JSONObject(response)
                                            val success2 = jsonResponse2.getBoolean("success")
                                            if (success2) {
                                                // 값 전송
                                                val bundle = bundleOf("userid" to userid, "email" to email, "userpw" to userpw, "nickname" to nickname)
                                                setFragmentResult("request", bundle)

                                                // 넘어가기
                                                findNavController().navigate(
                                                    R.id.action_InsertUserInfoFragment_to_insertBodyInfoFragment2,
                                                    bundle
                                                )
                                            } else {
                                                Snackbar.make(view, "이미 사용 중인 닉네임 입니다.", Snackbar.LENGTH_SHORT).show();
                                            }
                                        } catch (e: JSONException) {
                                            e.printStackTrace()
                                        }
                                    }
                                val nicknameValRequest = NicknameValRequest(nickname, responseListener2)
                                val queue2 = Volley.newRequestQueue(context)
                                queue2.add(nicknameValRequest)
                            } else {
                                Snackbar.make(view, "이미 사용 중인 아이디 입니다.", Snackbar.LENGTH_SHORT).show();
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                val validateRequest = ValidateRequest(userid, responseListener)
                val queue = Volley.newRequestQueue(context)
                queue.add(validateRequest)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun isRegularPw(password: String): Boolean {
        // 영문, 숫자, 특수문자
        val pwPattern = "^.*(?=^.{8,20}\$)(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!?*@#\$%^&+=]).*\$"
        return (Pattern.matches(pwPattern, password))
    }
    fun isRegularId(id: String): Boolean {
        val idPattern1 = "^(?=.*[a-z])[a-z]{2,20}\$" // 영문
        val idPattern2 = "^(?=.*[a-z])(?=.*[0-9])[a-z[0-9]]{2,20}\$" // 영문, 숫자
        return (Pattern.matches(idPattern1, id) ||
                Pattern.matches(idPattern2, id))
    }
}