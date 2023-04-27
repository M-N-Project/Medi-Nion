package com.example.medi_nion

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.medical_news_recycler.*
import org.jsoup.Jsoup

class MedicalNews : AppCompatActivity() {

    val items : ArrayList<MediInfo> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.medical_news_recycler)
        Log.d("haha1", "sd")
        Thread(Runnable {
            val url = "https://www.medicaltimes.com/Main/News/List.html?SectionTop=important"
            var base_url = "https://www.medicaltimes.com/Main/News/List.html?SectionTop=important"
            val doc = Jsoup.connect(url).get()
            val today = doc.select("div.newsList_wrap article.newsList_cont")
            val title = doc.title()
            Log.d("haha2", today.size.toString())
            today.forEach { item ->
                val item_link = base_url + item.select("a").attr("href")
                val item_title = item.select("h4.headLine").text()
                val item_img = item.select("img").attr("src")
                val item_contnent = item.select("div.list_txt").text()
                val item_time = item.select("span.newsList_cont_date").text()

//                println(item_link)
//                println(item_title)
//                println(item_thumb)
//                println(item_summary)
                Log.d("haha3", "sd")
                //arrayList 리스트에 추가해 준다.
                items.add(MediInfo(item_link, item_title, item_img, item_contnent, item_time))
            }

            this@MedicalNews.runOnUiThread(java.lang.Runnable {
                //println(doc)
                //어답터 연결하기
                medicalRecyclerview.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                val adapter = MedicalNewsAdapter(items, this)
                medicalRecyclerview.adapter = adapter
                Log.d("haha4", items.size.toString())
            })
        }).start()
    }
    data class MediInfo (
        val siteUrl: String,
        val title: String,
        val image: String,
        val content: String,
        val time: String
    )
}
