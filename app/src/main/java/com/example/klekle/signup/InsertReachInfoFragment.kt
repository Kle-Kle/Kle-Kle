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

    private var reach: String? = null

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
            reach = binding.inputReach.text.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}