package com.example.medi_nion

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.medical_news_recycler.*
import kotlinx.coroutines.DelicateCoroutinesApi
import org.jsoup.Jsoup
import java.util.concurrent.Executors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MedicalNews : AppCompatActivity() {

    val items : ArrayList<MediInfo> = arrayListOf()

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.medical_news_recycler)
        Log.d("haha1", "sd")
        // Create a thread pool with a fixed number of threads
        val threadPool = Executors.newFixedThreadPool(4)

        GlobalScope.launch(Dispatchers.IO) {
            val url = " https://www.medicaltimes.com/Main/News/List.html?SectionTop=important/index.nhn"
            var base_url = "https://www.medicaltimes.com/Main/News/List.html?SectionTop=important/"

            val doc = Jsoup.connect(url)
                .timeout(5000) // Set a timeout value (in milliseconds)
                .get()

            val today = doc.select("div.newsList_wrap article.newsList_cont")
            val title = doc.title()
            println("haha2: ${today.size}")

            val items = mutableListOf<MediInfo>()

            today.forEach { item ->
                val item_link = base_url + item.select("a").attr("href")
                val item_title = item.select("h4.headLine").text()
                val item_img = item.select("img").attr("src")
                val item_content = item.select("div.list_txt").text()
                val item_time = item.select("span.newsList_cont_date").text()

                println("haha3: $item_link")

                items.add(MediInfo(item_link, item_title, item_img, item_content, item_time))
            }

            launch(Dispatchers.Main) {
                medicalRecyclerview.layoutManager = LinearLayoutManager(this@MedicalNews, RecyclerView.VERTICAL, false)
                val adapter = MedicalNewsAdapter(items as ArrayList<MediInfo>, this@MedicalNews)
                medicalRecyclerview.adapter = adapter
                println("haha4: ${items.size}")
            }
        }.start()
    }
    data class MediInfo (
        val siteUrl: String,
        val title: String,
        val image: String,
        val content: String,
        val time: String
    )
}
