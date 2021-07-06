package com.example.softwareprojectapp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.softwareprojectapp.fragments.MyOrdersFragment
import com.example.softwareprojectapp.fragments.OrdersFragment

class PagerAdapter(fragmentManager: FragmentManager): FragmentStatePagerAdapter(fragmentManager) {

    override fun getCount(): Int = 2

    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> MyOrdersFragment()
            else -> OrdersFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when(position){
            0 -> "Ordenes"
            else -> "Pedidos"
        }
    }

}