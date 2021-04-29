package com.example.szakdolgozat.ui.paymentoptions

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.szakdolgozat.databinding.PaymentOptionsFragmentBinding
import com.example.szakdolgozat.ui.IItemLongClickListener

class PaymentOptionsFragment : Fragment() {

    private val viewModel: PaymentOptionsViewModel by lazy {
        ViewModelProvider(this).get(PaymentOptionsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = PaymentOptionsFragmentBinding.inflate(inflater, container, false)
        binding.model = viewModel
        val adapter = PaymentOptionsListAdapter(object : IItemLongClickListener<PaymentMethod> {
            override fun onItemLongClick(item: PaymentMethod) {
                viewModel.setDefaultPaymentMethod(item)
            }
        })
        binding.paymentOptionsRecyclerView.adapter = adapter

        viewModel.paymentMethods.observe(viewLifecycleOwner) {
            Log.d(TAG, it.toString())
            adapter.submitList(it)
        }

        binding.fabAddPaymentMethod.setOnClickListener {
            startActivity(Intent(activity, CreditCardDetailsActivity::class.java))
        }

        return binding.root
    }

    companion object {
        private const val TAG = "PaymentOptionsFragment"
    }

}