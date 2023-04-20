package com.example.medi_nion

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.example.medi_nion.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray


private var backPressedTime: Long = 0

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener { //mainactivity, 여기서는 프레그먼트 제어를 담당

    //OnNavigationItemSelectedListener
    private lateinit var viewPager2: ViewPager2
    lateinit var binding: ActivityMainBinding

//    private var id: String? = null
//    private var nickname: String? = null
//    private var userType: String? = null
//    private var userDept: String? = null
//    private var passwd: String? = null
//    private var userMedal: Int = 0

    private var NOTIFICATION_ID = "medinion"
    private var NOTIFICATION_NAME = "인증 알림"

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) { //프레그먼트로 생길 문제들은 추후에 생각하기,,
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ///id자리
        val infomap = HashMap<String, String>()
        infomap.put("id", intent.getStringExtra("id").toString())
        infomap.put("nickname", intent.getStringExtra("nickname").toString())
        infomap.put("userType", intent.getStringExtra("userType").toString())
        infomap.put("userDept", intent.getStringExtra("userDept").toString())
        infomap.put("passwd", intent.getStringExtra("passwd").toString())
        infomap.put("userMedal", intent.getIntExtra("userMedal", 0).toString())


        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(true)

        //채널 생성
        val notificationManager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val IMPORTANCE: Int = NotificationManager.IMPORTANCE_HIGH
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(NOTIFICATION_ID, NOTIFICATION_NAME, IMPORTANCE)
            notificationManager.createNotificationChannel(channel)
        }

        NotificationRequest()

        linearLayout.isUserInputEnabled = true //false시 스크롤 막힘

        //여기서 linearLayout은 ViewPager2, id바꿀시 혹시 에러날까봐 냅둠
        binding.linearLayout.adapter = ViewPagerAdapter2_Main(this, infomap)

        binding.linearLayout.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    binding.bottomNavigationView.menu.getItem(position).isChecked = true
                }
            }
        )

        binding.bottomNavigationView.setOnNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        //supportFragmentManager.beginTransaction().replace(R.id.linearLayout, menuFragment).commit()

        var id = intent.getStringExtra("id")
        var nickname = intent.getStringExtra("nickname")
        var userType = intent.getStringExtra("userType")
        var userDept = intent.getStringExtra("userDept")
        var passwd = intent.getStringExtra("passwd")
        var userMedal = intent.getIntExtra("userMedal", 0)

        when(item.itemId) {
            R.id.homeFragment -> {
                binding.linearLayout.currentItem = 0
                return true
            }
            R.id.menuFragment -> {
                binding.linearLayout.currentItem = 1
                return true
            }
            R.id.scheduleFragment -> {
                binding.linearLayout.currentItem = 2
                return true
            }
            R.id.businessFragment -> {
                binding.linearLayout.currentItem = 3
                return true
            }
            R.id.profileFragment -> {
                binding.linearLayout.currentItem = 4
                return true
            }
            else -> {
                return false
            }
        }
    }

    override fun onBackPressed() {
        if(System.currentTimeMillis() - backPressedTime >= 2000) {
            backPressedTime = System.currentTimeMillis()
            Toast.makeText(this, "\'뒤로\' 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else {
            finishAffinity()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.titlebar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar items
        when(item.itemId){
            R.id.search -> {
                return true
            }
            R.id.alarm -> {
                Log.d("Alarm", "?")
                return true
            }
            else -> {return super.onOptionsItemSelected(item)}
        }
    }

    fun NotificationRequest() {
        val url = "http://seonho.dothome.co.kr/notification.php"

        var Userid = intent?.getStringExtra("id").toString() //요청한 사람의 아이디
        var identity_check = "false"
        val notificationManager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val jsonArray: JSONArray

        val request = Login_Request(
            Request.Method.POST,
            url,
            { response ->
                val jsonArray = JSONArray(response)

                for (i in 0 until jsonArray.length()) {

                    val item = jsonArray.getJSONObject(i)

                    val id = item.getString("id")

                    Log.d("NOTIFICATION", "$id, $identity_check")

                    if (id == Userid) {
                        val builder: NotificationCompat.Builder =
                            NotificationCompat.Builder(this@MainActivity, NOTIFICATION_ID)
                                .setContentTitle("[Medi_Nion] 사용자 인증 알림") //타이틀 TEXT
                                .setContentText("인증할 수 없습니다. 인증을 다시 시도해주세요.\n프로필 메뉴 > 설정") //세부내용 TEXT
                                .setSmallIcon(R.drawable.logo) //필수 (안해주면 에러)
                        notificationManager.notify(0, builder.build())
                    }
                }


            }, { Log.d("notification Failed", "error......${error(applicationContext)}") },
            hashMapOf(
                "id" to Userid,
                "identity_check" to identity_check
            )
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
}