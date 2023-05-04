package com.example.medi_nion

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.example.medi_nion.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray

private var backPressedTime: Long = 0

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    //mainactivity, 여기서는 프레그먼트 제어를 담당

    //OnNavigationItemSelectedListener
    private lateinit var viewPager2: ViewPager2
    lateinit var binding: ActivityMainBinding

//    private var id: String? = null
//    private var nickname: String? = null
//    private var userType: String? = null
//    private var userDept: String? = null
//    private var passwd: String? = null
//    private var userMedal: Int = 0

    companion object {
        private var NOTIFICATION_ID = "medinion"
        private var NOTIFICATION_NAME = "인증 알림"
        private val ALARM_REQUEST_CODE = 1001
    }
    private lateinit var manager: NotificationManager
    private lateinit var builder: NotificationCompat.Builder
    lateinit var notificationPermission: ActivityResultLauncher<String>

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

        val notificationPermissionCheck = ContextCompat.checkSelfPermission(
            this@MainActivity,
            android.Manifest.permission.POST_NOTIFICATIONS
        )
        if (notificationPermissionCheck != PackageManager.PERMISSION_GRANTED) { // 권한이 없는 경우
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                10000
            )
        } else { //권한이 있는 경우
            Log.d("0-09123","notinoti")
        }

        notificationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                Log.d("ontintno", "notinoti")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notification()
                }
            } else {
                Toast.makeText(baseContext, "권한을 승인해야 일정 알림을 받을 수 있습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            NotificationRequest()
        }

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(true)


        val tabLayout: BottomNavigationView  = findViewById(R.id.bottomNavigationView)

        binding.linearLayout.isUserInputEnabled = true //false시 스크롤 막힘


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
                binding.linearLayout.setCurrentItem(0, false) //bottomnNavigation 클릭시엔 스와이프되지 않게하기
                return true
            }
            R.id.scheduleFragment -> {
                binding.linearLayout.setCurrentItem(1, false)
                return true
            }
            R.id.menuFragment -> {
                binding.linearLayout.setCurrentItem(2, false)
                return true
            }
            R.id.businessFragment -> {
                binding.linearLayout.setCurrentItem(3, false)
                return true
            }
            R.id.profileFragment -> {
                binding.linearLayout.setCurrentItem(4, false)
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
                val intent = Intent(this, SearchActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.alarm -> {
                Log.d("Alarm", "?")
                return true
            }
            else -> {return super.onOptionsItemSelected(item)}
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun notification() {
        notificationPermission.launch(android.Manifest.permission.POST_NOTIFICATIONS)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun NotificationRequest() {
        val receiverIntent: Intent = Intent(
            this@MainActivity,
            AlarmReceiver::class.java
        )
        val pendingIntent: PendingIntent =
            PendingIntent.getBroadcast(this@MainActivity,
                ALARM_REQUEST_CODE, receiverIntent,
                PendingIntent.FLAG_MUTABLE
            )
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
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
                        setAlarm(ALARM_REQUEST_CODE, "인증할 수 없습니다. 인증을 다시 시도해주세요.\n프로필 메뉴 > 설정")
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

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setAlarm(alarm_code: Int, content: String){
        AlarmFunctions_opencv(applicationContext).callAlarm(alarm_code, content)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel() {
        manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //NotificationChannel 인스턴스를 createNotificationChannel()에 전달하여 앱 알림 채널을 시스템에 등록
        manager.createNotificationChannel(
            NotificationChannel(
                NOTIFICATION_ID,
                NOTIFICATION_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
        )

    }
}