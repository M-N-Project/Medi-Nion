package com.example.medi_nion

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.medical_seminar_recycler.*
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import java.util.concurrent.Executors

class MedicalSeminar : AppCompatActivity() {

    data class MediSeminar (
        val saminar_siteUrl: String,
        val seminar_title: String,
        val seminar_time_date: String
    )

    val items : ArrayList<MediSeminar> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.medical_seminar_recycler)
        Log.d("haha1", "sd")

        kosmi() //대한의료정보학회함수 넣어놓음(이거만 완벽하게 된다 주아야 ㅜㅜ)
    }





    //웹크롤링 함수 모음(아산, 삼성, 대한의료정보학회, 한국보건산업진흥원,, kimes는 url만 따오기)
    //해야할일: 만든함수들 xml에 클릭이벤트 연결하기(될지 모르겠음), 각각 세부 url로 연결 안되는거 해결하기
    @OptIn(DelicateCoroutinesApi::class)
    private fun asan() {       //아산병원 웹크롤링 함수(아산 클릭시 넘어가는거 onCreate에 추가하기)
        GlobalScope.launch(Dispatchers.IO) {
            val url = "https://www.amc.seoul.kr/asan/academy/event/eventList.do"
            var base_url = "https://www.amc.seoul.kr/asan/academy/event/eventList.do"

            val doc = Jsoup.connect(url)
                .timeout(5000) // Set a timeout value (in milliseconds)
                .get()

            val today = doc.select("div.dayListWrap ul.dayListBox")
            val title = doc.title()
            println("haha2: ${today.size}")

            val items = mutableListOf<MediSeminar>()

            today.forEach { item ->
                val item_link = base_url + item.select("a[onclick*=fnDetail('1379')]").attr("href")
                val item_title = item.select("div.dayListTitle").text()
                val item_time = item.select("div.dayListTitle2").text()

                println("haha3: $item_link")

                items.add(MediSeminar(item_link, item_title, item_time))
            }

            launch(Dispatchers.Main) {
                seminarrecyclerview.layoutManager = LinearLayoutManager(this@MedicalSeminar, RecyclerView.VERTICAL, false)
                val adapter = MedicalSeminarAdapter(items as ArrayList<MediSeminar>, this@MedicalSeminar)
                seminarrecyclerview.adapter = adapter
                println("haha4: ${items.size}")
            }
        }.start()
    }

    ////////////////////////////////////////////////////////////////////////

    @OptIn(DelicateCoroutinesApi::class)
    private fun samsung() {       //삼성병원 웹크롤링 함수(아산 클릭시 넘어가는거 onCreate에 추가하기)
        GlobalScope.launch(Dispatchers.IO) {
            val url = "https://www.samsunghospital.com/home/info/scienceList.do"
            var base_url = "https://www.samsunghospital.com/home/info/scienceList.do"

            val doc = Jsoup.connect(url)
                .timeout(5000) // Set a timeout value (in milliseconds)
                .get()

            val today = doc.select("div.card-content-textarea")
            val title = doc.title()
            println("haha2: ${today.size}")

            val items = mutableListOf<MediSeminar>()

            today.forEach { item ->
                val item_link = item.select("a").attr("href")
                val item_title = item.select("a.card-content-title").text()
                val item_time = item.select("div.card-contet-text.card-conference-content ul").text()

                println("haha3: $item_link")

                items.add(MediSeminar(item_link, item_title, item_time))
            }

            launch(Dispatchers.Main) {
                seminarrecyclerview.layoutManager = LinearLayoutManager(this@MedicalSeminar, RecyclerView.VERTICAL, false)
                val adapter = MedicalSeminarAdapter(items as ArrayList<MediSeminar>, this@MedicalSeminar)
                seminarrecyclerview.adapter = adapter
                println("haha4: ${items.size}")
            }
        }.start()
    }

    /////////////////////////////////////////////////////////////////

    @OptIn(DelicateCoroutinesApi::class)
    private fun kosmi() {       //대한의료협회 웹크롤링 함수(아산 클릭시 넘어가는거 onCreate에 추가하기)
        GlobalScope.launch(Dispatchers.IO) {
            val url = "https://www.kosmi.org/bbs/board.php?bo_table=sub4_1"
            var base_url = "https://www.kosmi.org/bbs/board.php?botable=sub4_1"

            val doc = Jsoup.connect(url)
                .timeout(5000) // Set a timeout value (in milliseconds)
                .get()

            val today = doc.select("li.list_li_box a.bo_subject")
            val title = doc.title()
            println("haha2: ${today.size}")

            val items = mutableListOf<MediSeminar>()

            today.forEach { item ->
                val item_link =  item.select("a").attr("href")
                val item_title = item.select("a.bo_subject").text()
                val item_time = item.select("div.bo_cnt_place").text()

                println("haha3: $item_link")

                items.add(MediSeminar(item_link, item_title, item_time))
            }

            launch(Dispatchers.Main) {
                seminarrecyclerview.layoutManager = LinearLayoutManager(this@MedicalSeminar, RecyclerView.VERTICAL, false)
                val adapter = MedicalSeminarAdapter(items as ArrayList<MediSeminar>, this@MedicalSeminar)
                seminarrecyclerview.adapter = adapter
                println("haha4: ${items.size}")
            }
        }.start()
    }

    ////////////////////////////////////////////////////////////////

    @OptIn(DelicateCoroutinesApi::class)
    private fun khidi() {       //한국보건산업진흥원 웹크롤링 함수(아산 클릭시 넘어가는거 onCreate에 추가하기)
        GlobalScope.launch(Dispatchers.IO) {
            val url = "https://www.khidi.or.kr/board?menuId=MENU00095&siteId=SITE00002"
            var base_url = "https://www.khidi.or.kr/board?menuId=MENU00095&siteId=SITE00002"

            val doc = Jsoup.connect(url)
                .timeout(5000) // Set a timeout value (in milliseconds)
                .get()

            val today = doc.select("td.ellipsis")
            val title = doc.title()
            println("haha2: ${today.size}")

            val items = mutableListOf<MediSeminar>()

            today.forEach { item ->
                val item_link = item.select("a").attr("href")
                val item_title = item.select("a.title").text()
                val item_time = item.select("td").text()

                println("haha3: $item_link")

                items.add(MediSeminar(item_title, item_time, item_link))
            }

            launch(Dispatchers.Main) {
                seminarrecyclerview.layoutManager = LinearLayoutManager(this@MedicalSeminar, RecyclerView.VERTICAL, false)
                val adapter = MedicalSeminarAdapter(items as ArrayList<MediSeminar>, this@MedicalSeminar)
                seminarrecyclerview.adapter = adapter
                println("haha4: ${items.size}")
            }
        }.start()

    }
}