package com.example.klekle.signup

import android.os.Bundle
import com.example.klekle.signup.InsertBodyInfoFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.klekle.R
import com.example.klekle.databinding.FragmentInsertBodyInfoBinding
import com.example.klekle.databinding.FragmentRegisterBinding
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass.
 * Use the [InsertBodyInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InsertBodyInfoFragment : Fragment() {
    private var _binding: FragmentInsertBodyInfoBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View { _binding = FragmentInsertBodyInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnNext.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("height", binding.inputHeight.text.toString())
            bundle.putString("weight", binding.inputWeight.text.toString())

            findNavController().navigate(
                R.id.action_insertBodyInfoFragment2_to_insertReachInfoFragment,
                bundle
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}