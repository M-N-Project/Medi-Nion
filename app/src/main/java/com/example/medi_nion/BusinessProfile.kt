package com.example.medi_nion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.business_home.*
import org.json.JSONException
import org.json.JSONObject


var businessitems = ArrayList<BusinessProfileItem>()
//val viewModel = BoardViewModel()
lateinit var businessadapter : BusinessRecyclerAdapter
lateinit var businessmJsonString1: String
var businesserrorString: String? = null

class BusinessProfile : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) { //프레그먼트로 생길 문제들은 추후에 생각하기,,
        super.onCreate(savedInstanceState)
        setContentView(R.layout.business_profile_home)

        val url = "http://seonho.dothome.co.kr/BusinessProfile.php"

        fetchData()

    }

    fun fetchData() {
        // url to post our data
        val url = "http://seonho.dothome.co.kr/BusinessProfile.php"

        val queue = Volley.newRequestQueue(this@BusinessProfile)

        val request: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                try {
                    val jsonObject = JSONObject(response)
                    val name = jsonObject.getString("Channel_Name")
                    val message = jsonObject.getString("Channel_Message")
                    //val BusinessProfileItem = BusinessProfileItem(name, message, "", "", "")
                    //businessitems.add(BusinessProfileItem)
                    //val adapter = BusinessRecyclerAdapter(businessitems)
                    //BusinessBoardRecyclerView.adapter = adapter
                    //Log.d("lllaaa", BusinessProfileItem.Channel_Name)

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error -> // method to handle errors.
                Toast.makeText(this@BusinessProfile, "Fail to get course$error", Toast.LENGTH_SHORT)
                    .show()
            }) {
            override fun getBodyContentType(): String {
                // as we are passing data in the form of url encoded
                // so we are passing the content type below
                return "application/x-www-form-urlencoded; charset=UTF-8"
            }

        }
        queue.add(request)

    }

    data class JsonObj(val result: List<BusinessProfileItem>)
}