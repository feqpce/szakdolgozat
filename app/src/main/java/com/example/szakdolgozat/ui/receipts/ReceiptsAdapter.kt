package com.example.szakdolgozat.ui.receipts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.szakdolgozat.databinding.ReceiptListItemBinding
import com.example.szakdolgozat.ui.cart.Product

class ReceiptsAdapter : ListAdapter<Receipt, ReceiptsAdapter.ReceiptViewHolder>(ReceiptsDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptViewHolder {
        val binding =
            ReceiptListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val adapter = ReceiptsInnerAdapter()
        binding.receiptProductsList.adapter = adapter
        binding.lifecycleOwner = parent.findViewTreeLifecycleOwner()

        return ReceiptViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReceiptViewHolder, position: Int) {
        val receipt = getItem(position)

        holder.bind(receipt)
    }

    inner class ReceiptViewHolder(val binding: ReceiptListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(receipt: Receipt) {
            (binding.receiptProductsList.adapter as ListAdapter<Product, ReceiptInnerViewHolder>).submitList(
                receipt.items
            )
            binding.receipt = receipt
            binding.executePendingBindings()
        }
    }

}

class ReceiptsDiff : DiffUtil.ItemCallback<Receipt>() {
    override fun areItemsTheSame(oldItem: Receipt, newItem: Receipt): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Receipt, newItem: Receipt): Boolean {
        return oldItem.items == newItem.items
    }
}