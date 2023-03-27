package com.example.klekle.auth.signup

import android.os.Bundle
import com.example.klekle.auth.signup.InsertBodyInfoFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.example.klekle.R
import com.example.klekle.databinding.FragmentInsertBodyInfoBinding
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass.
 * Use the [InsertBodyInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InsertBodyInfoFragment : Fragment() {
    private var _binding: FragmentInsertBodyInfoBinding? = null

    private val binding get() = _binding!!

    private var userid: String? = null
    private var email: String? = null
    private var userpw: String? = null
    private var nickname: String? = null

    private var height: String? = null
    private var weight: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View { _binding = FragmentInsertBodyInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener("request") { request, bundle ->
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
        }

        binding.btnNext.setOnClickListener {
            height = binding.inputHeight.text.toString()
            weight = binding.inputWeight.text.toString()

            if (height.equals("")) {
                Snackbar.make(view, "신장 입력은 필수입니다.", Snackbar.LENGTH_SHORT).show();
            }
            else {
//                println("$userid, $height")

                // 값 전송
                val bundle = bundleOf("userid" to userid, "email" to email, "userpw" to userpw, "nickname" to nickname, "height" to height, "weight" to weight)
                setFragmentResult("request1", bundle)

                // 넘어가기
                findNavController().navigate(
                    R.id.action_insertBodyInfoFragment2_to_insertReachInfoFragment,
                    bundle
                )
            }

//            System.out.println(userid)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}