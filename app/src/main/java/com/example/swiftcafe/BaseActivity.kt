package com.example.swiftcafe

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

open class BaseActivity : AppCompatActivity() {

    private lateinit var swipeRefreshLayout: CustomSwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)  // Ensure this layout contains SwipeRefreshLayout

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        recyclerView = findViewById(R.id.recyclerView)

        swipeRefreshLayout.setOnRefreshListener {
            refreshData()
        }
    }

    open fun refreshData() {
        // Override this in your activities to refresh the data
        recyclerView.adapter?.notifyDataSetChanged()  // Update RecyclerView adapter

        // Stop the refreshing animation
        swipeRefreshLayout.isRefreshing = false

        // Optionally show a message
        Toast.makeText(this, "Data refreshed", Toast.LENGTH_SHORT).show()
    }
}
