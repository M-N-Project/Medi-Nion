package com.example.medi_nion

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CalendarViewModel : ViewModel() {
    private val _itemList = MutableLiveData<ArrayList<CalendarItem>>()
    private val items = ArrayList<CalendarItem>()

    val itemList: LiveData<ArrayList<CalendarItem>> get() = _itemList

    init {
    }

    fun setItemList(recyclerViewItems: ArrayList<CalendarItem>) {
        _itemList.value = recyclerViewItems
    }

    fun addItemList(item : CalendarItem){
        items.add(item)
    }

    fun editItemList(item : CalendarItem){
        _itemList.value = items
    }

    fun clearItemList(){
        _itemList.value?.clear()
    }

}