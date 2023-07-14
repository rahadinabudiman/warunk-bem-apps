import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.splashlogin.DetailProduk
import com.example.splashlogin.Produk
import com.example.splashlogin.R
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class ProdukAdapter(private val produkList: List<Produk>) : RecyclerView.Adapter<ProdukAdapter.ProdukViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdukViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_produk, parent, false)
        return ProdukViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProdukViewHolder, position: Int) {
        val produk = produkList[position]
        holder.bind(produk)
    }

    override fun getItemCount(): Int {
        return produkList.size
    }

    inner class ProdukViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageViewProduk: ImageView = itemView.findViewById(R.id.productImage)
        private val textViewNamaProduk: TextView = itemView.findViewById(R.id.productName)
        private val textViewHargaProduk: TextView = itemView.findViewById(R.id.productPrice)
        private val buttonBeli: Button = itemView.findViewById(R.id.buyButton)

        fun bind(produk: Produk) {
            // Set data to views
            Picasso.get().load(produk.image).into(imageViewProduk)
            textViewNamaProduk.text = produk.name
            textViewHargaProduk.text = formatCurrency(produk.price)

            // Set click listener
            buttonBeli.setOnClickListener {
                val idProduk = produk.id // Ganti dengan ID produk yang sesuai
                saveSelectedProductId(idProduk)

                val intent = Intent(context, DetailProduk::class.java)
                context.startActivity(intent)
            }
        }

        private fun formatCurrency(price: Int): String {
            val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            currencyFormat.currency = Currency.getInstance("IDR")
            return currencyFormat.format(price.toLong())
        }

        private fun saveSelectedProductId(productId: String) {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString("selectedProductId", productId)
            editor.apply()
        }
    }
}