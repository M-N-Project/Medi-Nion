package com.example.medi_nion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BoardViewModel: ViewModel() {

    private val _itemList = MutableLiveData<ArrayList<BoardItem>>()

    val itemList: LiveData<ArrayList<BoardItem>> get() = _itemList

    init {
        val recyclerViewItems: ArrayList<BoardItem> = ArrayList()
        recyclerViewItems.add(BoardItem("title1", "subtitle1"))
        recyclerViewItems.add(BoardItem( "title2", "subtitle2"))
        recyclerViewItems.add(BoardItem("title3", "subtitle3"))
        recyclerViewItems.add(BoardItem( "title4", "subtitle4"))
        recyclerViewItems.add(BoardItem( "title5", "subtitle5"))

        _itemList.value = recyclerViewItems
    }

    fun setItemList(recyclerViewItems: ArrayList<BoardItem>) {
        _itemList.value = recyclerViewItems
    }

}