package com.example.medi_nion

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter

class BusinessAdapter(private val itemList : List<BusinessBoardItem>) : Adapter<BusinessBoardViewHolder>() {
    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusinessBoardViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.business_board_items, parent, false)
        return BusinessBoardViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: BusinessBoardViewHolder, position: Int) {
        val item = itemList[position]
        holder.apply {
            bind(item)
        }
    }
}