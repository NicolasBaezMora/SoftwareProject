package com.example.softwareprojectapp.adapters

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.softwareprojectapp.R
import com.example.softwareprojectapp.databinding.ItemProductHomeBinding
import com.example.softwareprojectapp.models.Product
import com.squareup.picasso.Picasso

class ProductHomeAdapter(
        val onClickListener: (itemProduct: Product) -> Unit
): RecyclerView.Adapter<ProductHomeAdapter.ProductViewHolder>() {

    var listProducts = listOf<Product>()

    inner class ProductViewHolder(itemProductView: View): RecyclerView.ViewHolder(itemProductView){

        private val itemProductBinding = ItemProductHomeBinding.bind(itemProductView)

        fun bind(itemProduct: Product){
            itemProductBinding.textViewTitleProduct.text = itemProduct.title
            itemProductBinding.textViewPriceProduct.text = Html.fromHtml("<b>$</b>${itemProduct.price}")
            if (itemProduct.urlImage != "") Picasso.get().load(itemProduct.urlImage).into(itemProductBinding.imageViewProduct)
            itemProductBinding.LayoutItemProduct.setOnClickListener{ onClickListener(itemProduct) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product_home, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(listProducts[position])
    }

    override fun getItemCount(): Int = listProducts.size

}