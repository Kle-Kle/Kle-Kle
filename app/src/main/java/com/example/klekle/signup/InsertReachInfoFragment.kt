package com.example.klekle.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.klekle.databinding.FragmentInsertReachInfoBinding

/**
 * A simple [Fragment] subclass.
 * Use the [InsertReachInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InsertReachInfoFragment : Fragment() {
    private var _binding: FragmentInsertReachInfoBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View { _binding = FragmentInsertReachInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // binding.joinId.requestFocus()
        binding.btnRegister.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("reach", binding.inputReach.text.toString())
        }

        // argument를 추가하면 [Class명 + Args] 형태의 NavArgs 클래스가 자동생성 되는데, 여기서 꺼내어 쓸 수 있다..

        /*
        val userid = InsertBodyInfoFragmentArgs.fromBundle(requireArguments()).userid
        val userpw = InsertBodyInfoFragmentArgs.fromBundle(requireArguments()).userpw
        val userpwch = InsertBodyInfoFragmentArgs.fromBundle(requireArguments()).userpwcheck
        val nickname = InsertBodyInfoFragmentArgs.fromBundle(requireArguments()).nickname

        val height = InsertReachInfoFragmentArgs.fromBundle(requireArguments()).height
        val weight = InsertReachInfoFragmentArgs.fromBundle(requireArguments()).weight
         */
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}