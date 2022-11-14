package com.example.klekle.signup

import android.os.Bundle
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

/**
 * A simple [Fragment] subclass.
 * Use the [InsertUserInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InsertUserInfoFragment : Fragment() {
    private var _binding: FragmentInsertUserInfoBinding? = null

    private val binding get() = _binding!!

    private var userid: String? = null
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

        binding.btnNext.setOnClickListener {
            userid = binding.joinId.text.toString()
            userpw = binding.joinPw.text.toString()
            userpwch = binding.joinPwCheck.text.toString()
            nickname = binding.joinNickname.text.toString()

            if (userid.equals("")) {
                Snackbar.make(view, "아이디는 빈 칸일 수 없습니다.", Snackbar.LENGTH_SHORT).show();
            }
            else {
                if (userpw.equals("")) {
                    Snackbar.make(view, "비밀번호는 빈 칸일 수 없습니다.", Snackbar.LENGTH_SHORT).show();
                }
                else if (userpw.equals(userpwch)) {
                    if (nickname.equals("")) {
                        Snackbar.make(view, "닉네임을 설정해 주세요.", Snackbar.LENGTH_SHORT).show();
                    }
                    else {
                        val responseListener: Response.Listener<String> =
                            Response.Listener { response ->
                                try {
                                    val jsonResponse = JSONObject(response)
                                    val success = jsonResponse.getBoolean("success")
                                    if (success) {
                                        // 값 전송
                                        val bundle = bundleOf("userid" to userid, "userpw" to userpw, "nickname" to nickname)
                                        setFragmentResult("request", bundle)

                                        // 넘어가기
                                        findNavController().navigate(
                                            R.id.action_InsertUserInfoFragment_to_insertBodyInfoFragment2,
                                            bundle
                                        )
                                    } else {
                                        Snackbar.make(view, "사용할 수 없는 아이디 입니다.", Snackbar.LENGTH_SHORT).show();
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
                else {
                    Snackbar.make(view, "비밀번호가 비밀번호 확인란과 일치하지 않습니다.", Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}