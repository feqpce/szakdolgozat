package com.example.szakdolgozat.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.szakdolgozat.R
import com.example.szakdolgozat.databinding.DashboardFragmentBinding
import com.google.firebase.auth.FirebaseAuth

class DashboardFragment : Fragment() {

    companion object {
        fun newInstance() = DashboardFragment()
    }

    private val viewModel: DashboardViewModel by lazy {
        ViewModelProvider(this, DashboardViewModelFactory())
            .get(DashboardViewModel::class.java)
    }

    private lateinit var binding: DashboardFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (FirebaseAuth.getInstance().currentUser == null) {
            findNavController().navigate(R.id.loginFragment)
        }

        binding = DashboardFragmentBinding.inflate(inflater, container, false)
        binding.model = viewModel

        return binding.root
    }

}