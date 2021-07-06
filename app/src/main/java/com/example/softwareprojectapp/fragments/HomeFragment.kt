package com.example.softwareprojectapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.softwareprojectapp.R
import com.example.softwareprojectapp.adapters.ProductHomeAdapter
import com.example.softwareprojectapp.databinding.FragmentHomeBinding
import com.example.softwareprojectapp.viewmodels.ViewModelProductHome

class HomeFragment : Fragment() {

    private lateinit var homeFragBinding: FragmentHomeBinding
    private lateinit var productHomeAdapter: ProductHomeAdapter

    private val vm by lazy { ViewModelProviders.of(this).get(ViewModelProductHome::class.java) }
    private val navigator by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        homeFragBinding = FragmentHomeBinding.inflate(layoutInflater)
        return homeFragBinding.root
        //return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycleView()
        fetchProductsSnapshot()
    }

    private fun setupRecycleView() {
        productHomeAdapter = ProductHomeAdapter{
            navigator.navigate(R.id.action_homeFragment_to_productViewFragment, bundleOf("productData" to it))
        }
        homeFragBinding.recyclerViewProductsHome.adapter = productHomeAdapter
        homeFragBinding.recyclerViewProductsHome.layoutManager = GridLayoutManager(requireContext(), 2)
    }

    private fun fetchProductsSnapshot(){
        vm.fetchProductsSnapshot().observe(viewLifecycleOwner, Observer {
            productHomeAdapter.listProducts = it
            productHomeAdapter.notifyDataSetChanged()
        })
    }


}