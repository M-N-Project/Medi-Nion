package com.example.medi_nion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BoardViewModel: ViewModel() {

    private val _itemList = MutableLiveData<ArrayList<BoardItem>>()

    val itemList: LiveData<ArrayList<BoardItem>> get() = _itemList

    init {
        val recyclerViewItems: ArrayList<BoardItem> = ArrayList()

        _itemList.value = recyclerViewItems
    }

    fun setItemList(recyclerViewItems: ArrayList<BoardItem>) {
        _itemList.value = recyclerViewItems
    }

}