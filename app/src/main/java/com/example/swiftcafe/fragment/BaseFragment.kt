package com.example.swiftcafe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

open class BaseFragment : Fragment() {

    private lateinit var swipeRefreshLayout: CustomSwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_base, container, false)

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout)
        recyclerView = rootView.findViewById(R.id.recyclerView)

        swipeRefreshLayout.setOnRefreshListener {
            refreshData()
        }

        return rootView
    }

    open fun refreshData() {
        // Override this in your fragments to refresh the data
        var notifyDataSetChanged =true
            recyclerView.adapter?.notifyDataSetChanged()  // Update RecyclerView adapter

        // Stop the refreshing animation
        if(notifyDataSetChanged==true) {
            swipeRefreshLayout.isRefreshing = false
        }
    }
}
