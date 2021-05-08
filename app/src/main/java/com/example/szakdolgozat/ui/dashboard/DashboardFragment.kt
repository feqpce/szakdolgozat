package com.example.szakdolgozat.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.szakdolgozat.databinding.DashboardFragmentBinding

class DashboardFragment : Fragment() {

    private val viewModel: DashboardViewModel by lazy {
        ViewModelProvider(this, DashboardViewModelFactory())
            .get(DashboardViewModel::class.java)
    }

    private lateinit var binding: DashboardFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DashboardFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.model = viewModel

        return binding.root
    }

}