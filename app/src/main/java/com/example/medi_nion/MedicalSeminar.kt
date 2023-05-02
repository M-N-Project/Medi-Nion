package com.example.medi_nion

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.medical_seminar_recycler.*
import kotlinx.coroutines.*
import org.jsoup.Jsoup

class MedicalSeminar : AppCompatActivity() {

    data class MediSeminar (
        val saminar_siteUrl: String,
        val seminar_title: String,
        val seminar_time_date: String,
        val seminar_location: String
    )

    val items : ArrayList<MediSeminar> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.medical_seminar_recycler)

        asan()
        samsung()
        kosmi() //대한의료정보학회함수 넣어놓음(이거만 완벽하게 된다 주아야 ㅜㅜ)
        khidi()

        val kimes = findViewById<LinearLayout>(R.id.ms_kimes)
        kimes.setOnClickListener {
            val openUrl = Intent(Intent.ACTION_VIEW)
            openUrl.data = Uri.parse("https://kimes.kr/kor/")
            startActivity(openUrl)
        }
    }





    //웹크롤링 함수 모음(아산, 삼성, 대한의료정보학회, 한국보건산업진흥원,, kimes는 url만 따오기)
    //해야할일: 만든함수들 xml에 클릭이벤트 연결하기(될지 모르겠음), 각각 세부 url로 연결 안되는거 해결하기
    @OptIn(DelicateCoroutinesApi::class)
    private fun asan() {       //아산병원 웹크롤링 함수(아산 클릭시 넘어가는거 onCreate에 추가하기)
        GlobalScope.launch(Dispatchers.IO) {

            val url = "https://www.amc.seoul.kr/asan/academy/event/eventList.do"
            var base_url = "https://www.amc.seoul.kr/asan/academy/event/eventDetail.do"

            val doc = Jsoup.connect(url)
                .timeout(5000) // Set a timeout value (in milliseconds)
                .get()

            val today = doc.select("div.dayListWrap ul.dayListBox li.dayListBoxLeft")
            val title = doc.title()

            val items = mutableListOf<MediSeminar>()

            today.forEach { item ->
                val item_link = base_url + "?eventId=" + item.select("div.dayListTitle a").attr("onclick").replace("[^0-9]".toRegex(), "")
                val item_title = item.select("div.dayListTitle").text()
                var item_time = item.select("div.dayListTitle2").text()
                val item_location = item_time.split("행사일 : ")[0]
                item_time = "일시 : " + item_time.split("행사일 : ")[1]

                items.add(MediSeminar(item_link, item_title, item_time, item_location))
            }

            launch(Dispatchers.Main) {
                asanRecycler.layoutManager = LinearLayoutManager(this@MedicalSeminar, RecyclerView.VERTICAL, false)
                val adapter = MedicalSeminarAdapter(items as ArrayList<MediSeminar>, this@MedicalSeminar)
                asanRecycler.adapter = adapter
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

            val items = mutableListOf<MediSeminar>()

            today.forEach { item ->
                val item_link = "https://www.samsunghospital.com" + item.select("a").attr("href")
                val item_title = item.select("a.card-content-title").text()
                var item_time = item.select("div.card-content-text.card-conference-content ul li").text()
                val item_location = "장소 : " + item_time.split("장소 : ")[1]
                item_time = "일시 : " + item_time.split("장소 : ")[0].split("시간 : ")[1]

                items.add(MediSeminar(item_link, item_title, item_time, item_location))
            }

            launch(Dispatchers.Main) {
                samsungRecycler.layoutManager = LinearLayoutManager(this@MedicalSeminar, RecyclerView.VERTICAL, false)
                val adapter = MedicalSeminarAdapter(items as ArrayList<MediSeminar>, this@MedicalSeminar)
                samsungRecycler.adapter = adapter
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

            val today = doc.select("li.list_li_box")
            val title = doc.title()

            val items = mutableListOf<MediSeminar>()

            today.forEach { item ->
                val item_link =  item.select("a").attr("href")
                var item_title = item.select("div.bo_cnt.bo_width40 a.bo_subject").text()
                item_title = item_title.split("장소")[0]
                val item_location = item.select("div.bo_cnt_place").text()

                val item_month = item.select("div.bo_width10 div.bo_month").text()
                val item_day = item.select("div.bo_width20 div.bo_day").text()
                val item_time = "일시 : " + item_month + " " + item_day

                items.add(MediSeminar(item_link, item_title, item_time, item_location))
            }

            launch(Dispatchers.Main) {
                daehanRecycler.layoutManager = LinearLayoutManager(this@MedicalSeminar, RecyclerView.VERTICAL, false)
                val adapter = MedicalSeminarAdapter(items as ArrayList<MediSeminar>, this@MedicalSeminar)
                daehanRecycler.adapter = adapter
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

            val items = mutableListOf<MediSeminar>()

            today.forEach { item ->
                val item_link = "https://www.khidi.or.kr/" + item.select("a").attr("href")
                val item_time = ""
                val item_location=""
                val item_title = item.select("td").text()

                items.add(MediSeminar(item_link, item_title, item_time, item_location))
            }

            launch(Dispatchers.Main) {
                khidiRecycler.layoutManager = LinearLayoutManager(this@MedicalSeminar, RecyclerView.VERTICAL, false)
                val adapter = MedicalSeminarAdapter(items as ArrayList<MediSeminar>, this@MedicalSeminar)
                khidiRecycler.adapter = adapter
            }
        }.start()

    }
}