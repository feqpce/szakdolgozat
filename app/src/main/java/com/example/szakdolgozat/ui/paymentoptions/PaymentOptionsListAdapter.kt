package com.example.szakdolgozat.ui.paymentoptions

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.szakdolgozat.CustomApplication
import com.example.szakdolgozat.databinding.CreditCardPreviewBinding
import com.example.szakdolgozat.ui.IItemLongClickListener

class PaymentsDiff : DiffUtil.ItemCallback<PaymentMethod>() {

    override fun areItemsTheSame(oldItem: PaymentMethod, newItem: PaymentMethod): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: PaymentMethod, newItem: PaymentMethod): Boolean {
        return oldItem.id == newItem.id
    }
}

class PaymentOptionsListAdapter(val longClickListener: IItemLongClickListener<PaymentMethod>? = null) :
    ListAdapter<PaymentMethod, PaymentOptionsListAdapter.PaymentMethodViewHolder>(PaymentsDiff()) {

    private val selectedPaymentMethodID =
        CustomApplication.getPaymentRepository().defaultPaymentMethod

    inner class PaymentMethodViewHolder(val paymentMethodBinding: CreditCardPreviewBinding) :
        RecyclerView.ViewHolder(paymentMethodBinding.root) {


        fun bind(data: PaymentMethod) {
            paymentMethodBinding.method = data
            paymentMethodBinding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentMethodViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CreditCardPreviewBinding.inflate(inflater, parent, false)
        return PaymentMethodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaymentMethodViewHolder, position: Int) {
        val item = getItem(position)

        selectedPaymentMethodID.observeForever { id ->
            holder.itemView.setBackgroundColor(
                if (item.id == id) {
                    Color.argb(64, 0, 255, 255)
                } else {
                    Color.TRANSPARENT
                }
            )
        }
        longClickListener?.let { listener ->
            holder.itemView.setOnLongClickListener {
                listener.onItemLongClick(item)
                true
            }
        }
        holder.bind(item)
    }
}