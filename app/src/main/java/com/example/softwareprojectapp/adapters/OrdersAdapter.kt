package com.example.softwareprojectapp.adapters

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.softwareprojectapp.R
import com.example.softwareprojectapp.databinding.ItemOrderBinding
import com.example.softwareprojectapp.models.Order
import com.example.softwareprojectapp.models.SubOrder
import com.squareup.picasso.Picasso

class OrdersAdapter(
        val onClickOrder: (order: Order) -> Unit
): RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    var listOrders = listOf<Order>()

    inner class OrderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val itemOrderBinding = ItemOrderBinding.bind(itemView)

        fun bind(item: Order){
            Picasso.get().load(item.urlImage).into(itemOrderBinding.imgView)
            itemOrderBinding.textViewTitle.text = item.title
            itemOrderBinding.textViewTotal.text = item.total.toString()
            itemOrderBinding.textViewAddress.text = item.address
            itemOrderBinding.textViewState.text = Html.fromHtml("<b>${item.state}</b>")

            itemOrderBinding.itemViewLayout.setOnClickListener{
                onClickOrder(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(listOrders[position])
    }

    override fun getItemCount(): Int = listOrders.size

}