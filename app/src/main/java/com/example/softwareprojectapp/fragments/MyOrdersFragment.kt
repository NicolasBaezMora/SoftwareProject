package com.example.softwareprojectapp.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.softwareprojectapp.R
import com.example.softwareprojectapp.activities.ViewOrderActivity
import com.example.softwareprojectapp.adapters.OrdersAdapter
import com.example.softwareprojectapp.databinding.FragmentMyOrdersBinding
import com.example.softwareprojectapp.viewmodels.ViewModelMyOrdersViewPager

class MyOrdersFragment : Fragment() {

    private lateinit var myOrdersFragBinding: FragmentMyOrdersBinding
    private lateinit var ordersAdapter: OrdersAdapter

    private val vm by lazy { ViewModelProviders.of(this).get(ViewModelMyOrdersViewPager::class.java) }
    private val emailId by lazy {
        activity?.getSharedPreferences(R.string.prefs_file.toString(), Context.MODE_PRIVATE)?.getString("email", null) ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        myOrdersFragBinding = FragmentMyOrdersBinding.inflate(layoutInflater)
        return myOrdersFragBinding.root
        //return inflater.inflate(R.layout.fragment_my_orders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        addSnapshotOrders(emailId)
    }

    private fun setupRecyclerView() {
        ordersAdapter = OrdersAdapter {
            val intentOrder = Intent(requireContext(), ViewOrderActivity::class.java).apply {
                putExtra("order", it)
            }
            startActivity(intentOrder)
        }
        myOrdersFragBinding.recyclerViewMyOrders.adapter = ordersAdapter
        myOrdersFragBinding.recyclerViewMyOrders.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    private fun addSnapshotOrders(emailSeller: String){
        vm.fetchMyOrders(emailSeller).observe(viewLifecycleOwner, Observer {
            ordersAdapter.listOrders = it
            ordersAdapter.notifyDataSetChanged()
        })
    }

}