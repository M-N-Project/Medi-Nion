package com.example.medi_nion

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter

class BusinessAdapter(private val itemList : List<BusinessBoardItem>) : Adapter<BusinessBoardViewHolder>() {
    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): BusinessBoardViewHolder {
        val inflatedView = LayoutInflater.from(viewGroup.context).inflate(R.layout.business_board_items, viewGroup, false)
        return BusinessBoardViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: BusinessBoardViewHolder, position: Int) {
        val item = itemList[position]
        holder.apply {
            bind(item)
        }
    }
}