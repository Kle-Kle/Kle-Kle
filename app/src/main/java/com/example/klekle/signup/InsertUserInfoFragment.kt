package com.example.klekle.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.klekle.R
import com.example.klekle.databinding.FragmentInsertUserInfoBinding

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
            val bundle = Bundle()
            userid = binding.joinId.text.toString()
            userpw = binding.joinPw.text.toString()
            userpwch = binding.joinPwCheck.text.toString()
            nickname = binding.joinNickname.text.toString()

            findNavController().navigate(
                R.id.action_registerFragment_to_insertBodyInfoFragment2,
                bundle
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}