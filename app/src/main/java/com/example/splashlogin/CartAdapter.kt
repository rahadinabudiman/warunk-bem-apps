package com.example.splashlogin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CartAdapter(private val produkList: List<Produk>, private val total: Int) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val produk = produkList[position]
        holder.bind(produk, total)
    }

    override fun getItemCount(): Int {
        return produkList.size
    }

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val namaProdukTextView: TextView = itemView.findViewById(R.id.namaProdukTextView)
        private val hargaProdukTextView: TextView = itemView.findViewById(R.id.hargaProdukTextView)
        private val quantityTextView: TextView = itemView.findViewById(R.id.tvQuantity)
        private val minusButton: Button = itemView.findViewById(R.id.btnMinus)
        private val plusButton: Button = itemView.findViewById(R.id.btnPlus)
        private val produkImageView: ImageView = itemView.findViewById(R.id.imageView8)

        fun bind(produk: Produk, total: Int) {
            namaProdukTextView.text = produk.name
            hargaProdukTextView.text = "Rp. ${produk.price}"
            quantityTextView.text = produk.stock.toString()

            minusButton.setOnClickListener {
                // TODO: Implement logic to decrease the quantity
                // For example: update the quantityTextView and perform any necessary calculations
            }

            plusButton.setOnClickListener {
                // TODO: Implement logic to increase the quantity
                // For example: update the quantityTextView and perform any necessary calculations
            }

            // TODO: Load and display the product image using Glide or Picasso
            // For example:
            Glide.with(itemView)
                .load(produk.image)
                .into(produkImageView)
        }
    }
}