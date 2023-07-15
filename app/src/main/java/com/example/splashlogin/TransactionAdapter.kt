package com.example.splashlogin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class TransactionAdapter(private val transactionHistoryList: List<TransactionHistory>) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView8)
        val textProductName: TextView = itemView.findViewById(R.id.textProductName)
        val textTransactionDate: TextView = itemView.findViewById(R.id.textTransactionDate)
        val textTransactionTime: TextView = itemView.findViewById(R.id.textTransactionTime)
        val textTransactionPrice: TextView = itemView.findViewById(R.id.textTransactionPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transactionHistory = transactionHistoryList[position]

        // use Glide or Picasso to load the image from the URL
        Glide.with(holder.itemView.context)
            .load(transactionHistory.image)
            .into(holder.imageView)

        holder.textProductName.text = transactionHistory.name
        holder.textTransactionDate.text = transactionHistory.created_at
        holder.textTransactionTime.text = transactionHistory.waktu
        holder.textTransactionPrice.text = "${transactionHistory.harga}"
    }

    override fun getItemCount(): Int {
        return transactionHistoryList.size
    }
}