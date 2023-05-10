
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.medi_nion.Login
import com.example.medi_nion.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FirebaseMessagingService : FirebaseMessagingService() {
    private var msg: String? = null
    private var title: String? = null
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("onmessagerecie", remoteMessage.toString())
        title = remoteMessage.notification!!.title
        msg = remoteMessage.notification!!.body
        val intent = Intent(this, Login::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val contentIntent: PendingIntent = PendingIntent.getActivity(
            this, 0, Intent(
                this,
                MainActivity::class.java
            ), 0
        )
        val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this)
            .setContentTitle(title)
            .setContentText(msg)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setVibrate(longArrayOf(1, 1000))
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, mBuilder.build())
        mBuilder.setContentIntent(contentIntent)
    }

    companion object {
        private const val TAG = "FirebaseMsgService"
    }
}