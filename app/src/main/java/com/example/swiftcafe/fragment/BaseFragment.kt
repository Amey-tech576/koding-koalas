package com.example.swiftcafe

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
            refreshData() // Trigger data refresh
        }

        return rootView
    }

    open fun refreshData() {
        recyclerView.adapter?.notifyDataSetChanged() // Notify RecyclerView adapter

        // Delay stopping the animation to 2 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            swipeRefreshLayout.isRefreshing = false
        }, 2000)
    }
}
