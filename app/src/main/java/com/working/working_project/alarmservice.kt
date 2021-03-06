package com.working.working_project

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class alarmservice: Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel("Alarm", "$packageName-${getString(R.string.app_name)}", NotificationManager.IMPORTANCE_NONE) //ID, 채널이름, 중요도 설정

            channel.setSound(null, null) //사운드 설정
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE //잠금화면에서 보일지 안보일지 설정.

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)

            var builder = NotificationCompat.Builder(this, channel.toString())
            var notification:Notification = builder.setOngoing(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build()

            startForeground(1, notification)

        }

        var intent = Intent(this, alarmactivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // 새로운 테스크 생성하고 액티비티를 추가.
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // Task 안의 모든 속성을 제거 후, 해당 액티비티를 활성화하여 Top이 되도록함.
        startActivity(intent)

        if (Build.VERSION.SDK_INT >= 26) {
            stopForeground(true) // 포어그라운드 서비스 제어.
        }

        stopSelf() // 서비스 종료

        return START_NOT_STICKY // 강제 종료되어도 서비스 재시작 안함.
    }

        override fun onBind(intent: Intent?): IBinder? {
            return null
        }
}