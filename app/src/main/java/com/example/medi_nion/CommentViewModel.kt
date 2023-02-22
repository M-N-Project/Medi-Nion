package com.example.medi_nion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CommentViewModel: ViewModel() {

    private val _itemList = MutableLiveData<ArrayList<CommentItem>>()

    val itemList: LiveData<ArrayList<CommentItem>> get() = _itemList

    init {
        val recyclerViewItems: ArrayList<CommentItem> = ArrayList()

        _itemList.value = recyclerViewItems
    }

    fun setItemList(recyclerViewItems: ArrayList<CommentItem>) {
        _itemList.value = recyclerViewItems
    }

}