package com.example.klekle.signup

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.example.klekle.LoginActivity
import com.example.klekle.R
import com.example.klekle.databinding.FragmentInsertReachInfoBinding
import com.google.android.material.snackbar.Snackbar
import org.json.JSONException
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 * Use the [InsertReachInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InsertReachInfoFragment : Fragment() {
    private var _binding: FragmentInsertReachInfoBinding? = null

    private val binding get() = _binding!!

    private var userid: String? = null
    private var email: String? = null
    private var userpw: String? = null
    private var nickname: String? = null

    private var height: String? = null
    private var weight: String? = null
    private var reach: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View { _binding = FragmentInsertReachInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener("request1") { request1, bundle ->
            bundle.getString("userid")?.let { value ->
                userid = value
            }
            bundle.getString("email")?.let { value ->
                email = value
            }
            bundle.getString("userpw")?.let { value ->
                userpw = value
            }
            bundle.getString("nickname")?.let { value ->
                nickname = value
            }
            bundle.getString("height")?.let { value ->
                height = value
            }
            bundle.getString("weight")?.let { value ->
                weight = value
            }
        }

        binding.btnRegister.setOnClickListener {
            reach = binding.inputReach.text.toString()

//            println("$userid, $height, $reach")

            val responseListener: Response.Listener<String> =
                Response.Listener { response ->
                    try {
                        val jsonResponse = JSONObject(response)
                        val success = jsonResponse.getBoolean("success")
                        if (success) {
                            // 넘어가기
                            val intent = Intent(activity, LoginActivity::class.java)
                            startActivity(intent)
                        } else {
                            Snackbar.make(view, "입력 조건을 다시 한 번 확인해 주세요.", Snackbar.LENGTH_SHORT).show();
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            val registerRequest = RegisterRequest(userid, email, userpw, nickname, height, weight, reach, responseListener)
            var queue = Volley.newRequestQueue(context)
            queue.add(registerRequest)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}