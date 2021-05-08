package com.example.szakdolgozat

import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("total_price")
fun TextView.setTotalPrice(total: Int) {
    this.text = context.getString(R.string.cart_total, total)
}