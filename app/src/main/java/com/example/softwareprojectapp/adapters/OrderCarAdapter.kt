package com.example.softwareprojectapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.softwareprojectapp.R
import com.example.softwareprojectapp.databinding.ItemOrderCarBinding
import com.example.softwareprojectapp.models.SubOrder
import com.squareup.picasso.Picasso

class OrderCarAdapter constructor (
        var listSubOrders: List<SubOrder>,
        val onClickDeleteOrder: (itemSubOrder: SubOrder) -> Unit
): RecyclerView.Adapter<OrderCarAdapter.OrderViewHolder>() {


    inner class OrderViewHolder(view: View): RecyclerView.ViewHolder(view){

        private val itemOrderBinding = ItemOrderCarBinding.bind(view)

        fun bind(itemSubOrder: SubOrder){
            Picasso.get().load(itemSubOrder.product.urlImage).into(itemOrderBinding.imageViewOrder)
            itemOrderBinding.textViewTitleOrder.text = itemSubOrder.product.title
            itemOrderBinding.textViewTotalPrice.text = itemSubOrder.total.toString()
            itemOrderBinding.btnDeleteOrder.setOnClickListener{
                onClickDeleteOrder(itemSubOrder)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order_car, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(listSubOrders[position])
    }

    override fun getItemCount(): Int = listSubOrders.size

}