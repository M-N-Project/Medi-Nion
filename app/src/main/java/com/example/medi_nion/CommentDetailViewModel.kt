package com.example.medi_nion

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CommentDetailViewModel: ViewModel() {

    private val _itemList = MutableLiveData<ArrayList<CommentDetailItem>>()

    val itemList: LiveData<ArrayList<CommentDetailItem>> get() = _itemList

    init {
        val recyclerViewItems: ArrayList<CommentDetailItem> = ArrayList()

        _itemList.value = recyclerViewItems
    }

    fun getItemIndexList(position : Int){
        itemList.value?.get(position)
    }

    fun setItemList(recyclerViewItems: ArrayList<CommentDetailItem>) {
        _itemList.value = recyclerViewItems
    }
}