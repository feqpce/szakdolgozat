package com.example.szakdolgozat.ui.receipts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.szakdolgozat.R
import com.example.szakdolgozat.ui.cart.Product

class ReceiptsInnerAdapter :
    ListAdapter<Product, ReceiptInnerViewHolder>(ReceiptsInnerDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptInnerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.inner_receipt_list_item, parent, false)

        return ReceiptInnerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReceiptInnerViewHolder, position: Int) {
        val product = getItem(position)

        holder.bind(product)
    }
}

class ReceiptInnerViewHolder(val view: View) :
    RecyclerView.ViewHolder(view) {

    fun bind(product: Product) {
        view.findViewById<TextView>(R.id.receipt_item_text_view).text = product.toString()
    }
}

class ReceiptsInnerDiff : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem.id == newItem.id
    }
}