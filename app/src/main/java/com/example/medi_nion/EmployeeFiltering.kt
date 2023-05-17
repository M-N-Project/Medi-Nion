package com.example.medi_nion

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class EmployeeFiltering : AppCompatActivity() {
    private lateinit var doneBtn:Button
    private lateinit var loca_radioGroup1:RadioGroup
    private lateinit var loca_radioGroup2:RadioGroup
    private lateinit var loca_radioGroup3:RadioGroup

    private lateinit var dept_radioGroup1:RadioGroup
    private lateinit var dept_radioGroup2:RadioGroup
    private lateinit var dept_radioGroup3:RadioGroup
    private lateinit var dept_radioGroup4:RadioGroup
    private lateinit var dept_radioGroup5:RadioGroup

    private lateinit var hos_radioGroup1:RadioGroup
    private lateinit var hos_radioGroup2:RadioGroup

    private lateinit var loca_clear:ImageView
    private lateinit var dept_clear:ImageView
    private lateinit var hos_clear:ImageView

    private var location = -1
    private var dept = -1
    private var hospital = -1

    override fun onCreate(savedInstanceState: Bundle?) { //병원 정보 프로필 액티비티
        super.onCreate(savedInstanceState)
        setContentView(R.layout.employee_info_filtering)

        doneBtn = findViewById<Button>(R.id.button)
        loca_radioGroup1 = findViewById<RadioGroup>(R.id.loca_radioGroup)
        loca_radioGroup2 = findViewById<RadioGroup>(R.id.loca_radioGroup2)
        loca_radioGroup3 = findViewById<RadioGroup>(R.id.loca_radioGroup3)

        dept_radioGroup1 = findViewById<RadioGroup>(R.id.dept_radioGroup)
        dept_radioGroup2 = findViewById<RadioGroup>(R.id.dept_radioGroup2)
        dept_radioGroup3 = findViewById<RadioGroup>(R.id.dept_radioGroup3)
        dept_radioGroup4 = findViewById<RadioGroup>(R.id.dept_radioGroup4)
        dept_radioGroup5 = findViewById<RadioGroup>(R.id.dept_radioGroup5)

        hos_radioGroup1 = findViewById<RadioGroup>(R.id.hos_radioGroup)
        hos_radioGroup2 = findViewById<RadioGroup>(R.id.hos_radioGroup2)

        loca_clear = findViewById<ImageView>(R.id.loca_clear)
        dept_clear = findViewById<ImageView>(R.id.dept_clear)
        hos_clear = findViewById<ImageView>(R.id.hos_clear)

        ///////////////// 근무지역 라디오그룹 제어 /////////////////////////
        loca_radioGroup1.clearCheck()
        loca_radioGroup2.clearCheck()
        loca_radioGroup3.clearCheck()

        loca_radioGroup1.setOnCheckedChangeListener(listener1())
        loca_radioGroup2.setOnCheckedChangeListener(listener2())
        loca_radioGroup3.setOnCheckedChangeListener(listener3())

        //////////////// 진료과 라디오 그룹 제어 ///////////////////////////
        dept_radioGroup1.clearCheck()
        dept_radioGroup2.clearCheck()
        dept_radioGroup3.clearCheck()
        dept_radioGroup4.clearCheck()
        dept_radioGroup5.clearCheck()

        dept_radioGroup1.setOnCheckedChangeListener(dept_listener1())
        dept_radioGroup2.setOnCheckedChangeListener(dept_listener2())
        dept_radioGroup3.setOnCheckedChangeListener(dept_listener3())
        dept_radioGroup4.setOnCheckedChangeListener(dept_listener4())
        dept_radioGroup5.setOnCheckedChangeListener(dept_listener5())

        //////////////// 병원 종류 라디오 그룹 제어 ///////////////////////////
        hos_radioGroup1.clearCheck()
        hos_radioGroup2.clearCheck()

        hos_radioGroup1.setOnCheckedChangeListener(hos_listener1())
        hos_radioGroup2.setOnCheckedChangeListener(hos_listener2())

        //////////////// 선택 완료 버튼 클릭 이벤트 ///////////////////////////
        doneBtn.setOnClickListener {
            val location_text = if(location!= -1) findViewById<RadioButton>(location).text else ""
            val dept_text = if(dept!= -1) findViewById<RadioButton>(dept).text else ""
            val hospital_text = if(hospital!= -1) findViewById<RadioButton>(hospital).text else ""

            val intent = Intent(applicationContext, HospitalProfile::class.java)
            intent.putExtra("location",location_text)
            intent.putExtra("dept",dept_text)
            intent.putExtra("hospital",hospital_text)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        //////////////// 선택해제버튼 클릭 이벤트 ///////////////////////////
        loca_clear.setOnClickListener{
            loca_radioGroup3.clearCheck()
            loca_radioGroup2.clearCheck()
            loca_radioGroup1.clearCheck()
        }
        dept_clear.setOnClickListener {
            dept_radioGroup1.clearCheck()
            dept_radioGroup2.clearCheck()
            dept_radioGroup3.clearCheck()
            dept_radioGroup4.clearCheck()
            dept_radioGroup5.clearCheck()
        }
        hos_clear.setOnClickListener {
            hos_radioGroup1.clearCheck()
            hos_radioGroup2.clearCheck()
        }

    }

    inner class listener1:RadioGroup.OnCheckedChangeListener{
        override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
            if(p1 != -1) {
                loca_radioGroup2.setOnCheckedChangeListener(null)
                loca_radioGroup3.setOnCheckedChangeListener(null)
                loca_radioGroup2.clearCheck()
                loca_radioGroup3.clearCheck()
                loca_radioGroup2.setOnCheckedChangeListener(listener2())
                loca_radioGroup3.setOnCheckedChangeListener(listener3())
                location= p1
            }
        }
    }
    inner class listener2:RadioGroup.OnCheckedChangeListener{
        override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
            if(p1 != -1) {
                loca_radioGroup1.setOnCheckedChangeListener(null)
                loca_radioGroup3.setOnCheckedChangeListener(null)
                loca_radioGroup1.clearCheck()
                loca_radioGroup3.clearCheck()
                loca_radioGroup1.setOnCheckedChangeListener(listener1())
                loca_radioGroup3.setOnCheckedChangeListener(listener3())
                location= p1
            }
        }
    }
    inner class listener3:RadioGroup.OnCheckedChangeListener{
        override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
            if(p1 != -1) {
                loca_radioGroup2.setOnCheckedChangeListener(null)
                loca_radioGroup1.setOnCheckedChangeListener(null)
                loca_radioGroup2.clearCheck()
                loca_radioGroup1.clearCheck()
                loca_radioGroup2.setOnCheckedChangeListener(listener2())
                loca_radioGroup1.setOnCheckedChangeListener(listener1())
                location= p1
            }
        }
    }


    inner class dept_listener1:RadioGroup.OnCheckedChangeListener{
        override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
            if(p1 != -1) {
                dept_radioGroup2.setOnCheckedChangeListener(null)
                dept_radioGroup3.setOnCheckedChangeListener(null)
                dept_radioGroup4.setOnCheckedChangeListener(null)
                dept_radioGroup5.setOnCheckedChangeListener(null)
                dept_radioGroup2.clearCheck()
                dept_radioGroup3.clearCheck()
                dept_radioGroup4.clearCheck()
                dept_radioGroup5.clearCheck()
                dept_radioGroup2.setOnCheckedChangeListener(dept_listener2())
                dept_radioGroup3.setOnCheckedChangeListener(dept_listener3())
                dept_radioGroup4.setOnCheckedChangeListener(dept_listener4())
                dept_radioGroup5.setOnCheckedChangeListener(dept_listener5())
                dept= p1
            }
        }
    }
    inner class dept_listener2:RadioGroup.OnCheckedChangeListener{
        override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
            if(p1 != -1) {
                dept_radioGroup1.setOnCheckedChangeListener(null)
                dept_radioGroup3.setOnCheckedChangeListener(null)
                dept_radioGroup4.setOnCheckedChangeListener(null)
                dept_radioGroup5.setOnCheckedChangeListener(null)
                dept_radioGroup1.clearCheck()
                dept_radioGroup3.clearCheck()
                dept_radioGroup4.clearCheck()
                dept_radioGroup5.clearCheck()
                dept_radioGroup1.setOnCheckedChangeListener(dept_listener1())
                dept_radioGroup3.setOnCheckedChangeListener(dept_listener3())
                dept_radioGroup4.setOnCheckedChangeListener(dept_listener4())
                dept_radioGroup5.setOnCheckedChangeListener(dept_listener5())
                dept= p1
            }
        }
    }
    inner class dept_listener3:RadioGroup.OnCheckedChangeListener{
        override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
            if(p1 != -1) {
                dept_radioGroup2.setOnCheckedChangeListener(null)
                dept_radioGroup1.setOnCheckedChangeListener(null)
                dept_radioGroup4.setOnCheckedChangeListener(null)
                dept_radioGroup5.setOnCheckedChangeListener(null)
                dept_radioGroup2.clearCheck()
                dept_radioGroup1.clearCheck()
                dept_radioGroup4.clearCheck()
                dept_radioGroup5.clearCheck()
                dept_radioGroup2.setOnCheckedChangeListener(dept_listener2())
                dept_radioGroup1.setOnCheckedChangeListener(dept_listener1())
                dept_radioGroup4.setOnCheckedChangeListener(dept_listener4())
                dept_radioGroup5.setOnCheckedChangeListener(dept_listener5())
                dept= p1
            }
        }
    }
    inner class dept_listener4:RadioGroup.OnCheckedChangeListener{
        override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
            if(p1 != -1) {
                dept_radioGroup2.setOnCheckedChangeListener(null)
                dept_radioGroup3.setOnCheckedChangeListener(null)
                dept_radioGroup1.setOnCheckedChangeListener(null)
                dept_radioGroup5.setOnCheckedChangeListener(null)
                dept_radioGroup2.clearCheck()
                dept_radioGroup3.clearCheck()
                dept_radioGroup1.clearCheck()
                dept_radioGroup5.clearCheck()
                dept_radioGroup2.setOnCheckedChangeListener(dept_listener2())
                dept_radioGroup3.setOnCheckedChangeListener(dept_listener3())
                dept_radioGroup1.setOnCheckedChangeListener(dept_listener1())
                dept_radioGroup5.setOnCheckedChangeListener(dept_listener5())
                dept= p1
            }
        }
    }
    inner class dept_listener5:RadioGroup.OnCheckedChangeListener{
        override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
            if(p1 != -1) {
                dept_radioGroup2.setOnCheckedChangeListener(null)
                dept_radioGroup3.setOnCheckedChangeListener(null)
                dept_radioGroup4.setOnCheckedChangeListener(null)
                dept_radioGroup1.setOnCheckedChangeListener(null)
                dept_radioGroup2.clearCheck()
                dept_radioGroup3.clearCheck()
                dept_radioGroup4.clearCheck()
                dept_radioGroup1.clearCheck()
                dept_radioGroup2.setOnCheckedChangeListener(dept_listener2())
                dept_radioGroup3.setOnCheckedChangeListener(dept_listener3())
                dept_radioGroup4.setOnCheckedChangeListener(dept_listener4())
                dept_radioGroup1.setOnCheckedChangeListener(dept_listener1())
                dept= p1
            }
        }
    }

    inner class hos_listener1:RadioGroup.OnCheckedChangeListener{
        override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
            if(p1 != -1) {
                hos_radioGroup2.setOnCheckedChangeListener(null)
                hos_radioGroup2.clearCheck()
                hos_radioGroup2.setOnCheckedChangeListener(hos_listener2())
                hospital = p1
            }
        }
    }
    inner class hos_listener2:RadioGroup.OnCheckedChangeListener{
        override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
            if(p1 != -1) {
                hos_radioGroup1.setOnCheckedChangeListener(null)
                hos_radioGroup1.clearCheck()
                hos_radioGroup1.setOnCheckedChangeListener(hos_listener1())
                hospital = p1
            }
        }
    }
}