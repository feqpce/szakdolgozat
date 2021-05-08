package com.example.szakdolgozat.ui.receipts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.szakdolgozat.databinding.FragmentReceiptsBinding

class ReceiptsFragment : Fragment() {
    val viewModel by viewModels<ReceiptsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val binding = FragmentReceiptsBinding.inflate(inflater, container, false)
        val adapter = ReceiptsAdapter()

        binding.lifecycleOwner = viewLifecycleOwner
        binding.receiptsList.adapter = adapter

        viewModel.receipts.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        }

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getReceipts()
    }
}