package com.example.szakdolgozat.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.szakdolgozat.databinding.CartItemBinding
import com.example.szakdolgozat.ui.IItemLongClickListener
import com.example.szakdolgozat.ui.cart.dummy.DummyContent.DummyItem

/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 */


class MyDiffUtil : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem.productName == newItem.productName
    }

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem.numberInCart.value == newItem.numberInCart.value
    }

}

class ProductRecyclerViewAdapter(val longClickListener: IItemLongClickListener<Product>? = null) :
    ListAdapter<Product, ProductRecyclerViewAdapter.ViewHolder>(MyDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CartItemBinding.inflate(inflater, parent, false)

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        longClickListener?.let {
            holder.itemView.setOnLongClickListener {
                longClickListener.onItemLongClick(item)
                true
            }
        }

        holder.bind(item)
    }

    inner class ViewHolder(val binding: CartItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.numberInCartPicker.minValue = 1
            binding.numberInCartPicker.maxValue = 99
            binding.numberInCartPicker.value = product.numberInCart.value!!

            binding.product = product
            binding.executePendingBindings()
        }
    }
}