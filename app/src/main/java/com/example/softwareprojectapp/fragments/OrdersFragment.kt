package com.example.softwareprojectapp.fragments

import android.content.Context
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
import com.example.softwareprojectapp.activities.ProfileActivity
import com.example.softwareprojectapp.adapters.OrdersAdapter
import com.example.softwareprojectapp.databinding.FragmentOrdersBinding
import com.example.softwareprojectapp.viewmodels.ViewModelOrdersViewPager

class OrdersFragment : Fragment() {

    private lateinit var ordersFragBinding: FragmentOrdersBinding
    private lateinit var ordersAdapter: OrdersAdapter

    private val vm by lazy { ViewModelProviders.of(this).get(ViewModelOrdersViewPager::class.java) }
    private val emailId by lazy {
        activity?.getSharedPreferences(R.string.prefs_file.toString(), Context.MODE_PRIVATE)?.getString("email", null) ?: ""
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        ordersFragBinding = FragmentOrdersBinding.inflate(layoutInflater)
        return ordersFragBinding.root
        //return inflater.inflate(R.layout.fragment_my_orders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        addSnapshotOrders(emailId)
    }

    private fun setupRecyclerView() {
        ordersAdapter = OrdersAdapter {
            Toast.makeText(context, "EZZZZZ", Toast.LENGTH_SHORT).show()
        }
        ordersFragBinding.recyclerViewOrders.adapter = ordersAdapter
        ordersFragBinding.recyclerViewOrders.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    private fun addSnapshotOrders(emailBuyer: String){
        vm.fetchOrders(emailBuyer).observe(viewLifecycleOwner, Observer {
            ordersAdapter.listOrders = it
            ordersAdapter.notifyDataSetChanged()
        })
    }
}