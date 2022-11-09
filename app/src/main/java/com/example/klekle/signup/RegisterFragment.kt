package com.example.klekle.signup

import android.os.Bundle
import com.example.klekle.signup.RegisterFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import com.example.klekle.R
import com.example.klekle.databinding.FragmentRegisterBinding
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View { _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.joinId.requestFocus()
        binding.btnNext.setOnClickListener {
            val nickname = binding.joinId.text.toString()
            if (nickname.isEmpty()) {
                Snackbar.make(binding.btnNext, "Nickname must not be empty.", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val bundle = Bundle()
            bundle.putString("nickname", binding.joinId.text.toString())

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