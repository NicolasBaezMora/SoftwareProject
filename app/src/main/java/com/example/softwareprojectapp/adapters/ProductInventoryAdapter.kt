package com.example.softwareprojectapp.adapters

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.softwareprojectapp.R
import com.example.softwareprojectapp.models.Product
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_product_home.view.*
import kotlinx.android.synthetic.main.item_product_inventory.view.*

class ProductInventoryAdapter(
        val onClickEditListener: (product: Product) -> Unit,
        val onClickDeleteListener: (product: Product) -> Unit
): RecyclerView.Adapter<ProductInventoryAdapter.ProductInventoryViewHolder>() {

    var listProducts = listOf<Product>()

    inner class ProductInventoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(itemProduct: Product){
            if (itemProduct.urlImage != ""){
                Picasso.get()
                        .load(itemProduct.urlImage)
                        .into(itemView.imageViewProductInventory)
            }
            itemView.textViewTitleProductInventory.text = Html.fromHtml("<b>${itemProduct.title}</b>")
            itemView.textViewPriceProductInventory.text = Html.fromHtml("<b>$</b>${itemProduct.price}")
            itemView.textViewDescriptionProductInventory.text = itemProduct.description

            itemView.btnEditProduct.setOnClickListener { onClickEditListener(itemProduct) }
            itemView.btnDeleteProduct.setOnClickListener { onClickDeleteListener(itemProduct) }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductInventoryViewHolder {
        val viewItemProductInventory = LayoutInflater.from(parent.context).inflate(
                R.layout.item_product_inventory,
                parent,
                false
        )
        return ProductInventoryViewHolder(viewItemProductInventory)
    }

    override fun onBindViewHolder(holder: ProductInventoryViewHolder, position: Int) {
        holder.bind(listProducts[position])
    }

    override fun getItemCount(): Int {
        return listProducts.size
    }
}