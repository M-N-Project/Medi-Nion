package com.example.medi_nion

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class ScheduleFragment : Fragment(R.layout.schedule) { //간호사 스케쥴표 화면(구현 어케하누,,)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val button = view.findViewById<Button>(R.id.schedule_btn)
        button.setOnClickListener {
            val intent = Intent(context, Schedule_Add::class.java)
            startActivity(intent)
        }
    }
}